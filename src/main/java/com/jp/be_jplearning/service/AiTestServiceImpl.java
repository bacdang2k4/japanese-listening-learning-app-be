package com.jp.be_jplearning.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp.be_jplearning.common.ResourceNotFoundException;
import com.jp.be_jplearning.dto.*;
import com.jp.be_jplearning.entity.*;
import com.jp.be_jplearning.entity.enums.GenerationStatusEnum;
import com.jp.be_jplearning.entity.enums.ReviewActionEnum;
import com.jp.be_jplearning.entity.enums.TestStatusEnum;
import com.jp.be_jplearning.integration.AiClient;
import com.jp.be_jplearning.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiTestServiceImpl implements AiTestService {

    private final TopicRepository topicRepository;
    private final AudioTestRepository audioTestRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final AIGenerationLogRepository aiGenerationLogRepository;
    private final TestReviewRepository testReviewRepository;
    private final AdminRepository adminRepository;

    private final AiClient aiClient;
    private final ObjectMapper objectMapper;
    private final AiAudioGenerationProcessor audioGenerationProcessor;

    @Value("${ai.model}")
    private String aiModel;

    @Override
    @Transactional(noRollbackFor = com.jp.be_jplearning.common.BusinessException.class)
    public AiTestResponse generateTest(AiGenerateRequest request) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + request.getTopicId()));

        Admin currentAdmin = getCurrentAdmin();

        AudioTest test = new AudioTest();
        test.setTestName(request.getTestName());
        test.setTopic(topic);
        test.setDuration(request.getDuration());
        test.setTotalQuestions(request.getQuestionCount());
        test.setStatus(TestStatusEnum.AI_GENERATED); // Keep as AI_GENERATED while audio creates
        test.setIsAiGenerated(true);
        test.setCreatedByAdmin(currentAdmin);
        test.setCreatedAt(LocalDateTime.now());

        test = audioTestRepository.save(test);

        String prompt = buildPrompt(request, topic);

        AIGenerationLog logEntry = new AIGenerationLog();
        logEntry.setTest(test);
        logEntry.setPrompt(prompt);
        logEntry.setModel(aiModel);
        logEntry.setGeneratedAt(LocalDateTime.now());

        try {
            String rawJson = aiClient.generateListeningTest(prompt);
            logEntry.setRawResponse(rawJson);

            // Parse response
            Map<String, Object> aiMap = objectMapper.readValue(rawJson, new TypeReference<Map<String, Object>>() {
            });
            String rawTranscript = (String) aiMap.get("transcript");
            String processedTranscript = rawTranscript;
            if (processedTranscript != null) {
                processedTranscript = processedTranscript.replace("\n", " ").replace("\\n", " ").replaceAll("\\s+", " ")
                        .trim();
            }
            final String transcript = processedTranscript;

            test.setTranscript(transcript);
            audioTestRepository.save(test);

            List<Map<String, Object>> questionsMap = (List<Map<String, Object>>) aiMap.get("questions");
            saveQuestionsAndAnswers(test, questionsMap);

            logEntry.setStatus(GenerationStatusEnum.SUCCESS);
            AIGenerationLog savedLog = aiGenerationLogRepository.save(logEntry);

            Long finalTestId = test.getId();
            Long finalLogId = savedLog.getId();

            // Trigger Async Audio Generation AFTER the transaction is successfully
            // committed to DB
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    audioGenerationProcessor.generateAudioAndUpload(finalTestId, transcript, finalLogId);
                }
            });

        } catch (Exception e) {
            log.error("Failed AI Test Generation", e);
            logEntry.setStatus(GenerationStatusEnum.FAILED);

            String errorDetail = e.getMessage();
            if (e.getCause() != null) {
                errorDetail += " | Cause: " + e.getCause().getMessage();
            }
            if (logEntry.getRawResponse() != null) {
                logEntry.setRawResponse(logEntry.getRawResponse() + "\n\n--- ERROR LOG ---\n" + errorDetail);
            } else {
                logEntry.setRawResponse("Generation Error: " + errorDetail);
            }

            test.setStatus(TestStatusEnum.REJECTED);
            audioTestRepository.save(test);
            aiGenerationLogRepository.save(logEntry);

            throw new com.jp.be_jplearning.common.BusinessException(
                    "AI Generation failed. Check logs for details: " + e.getMessage());
        }

        return getGeneratedTest(test.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public AiTestResponse getGeneratedTest(Long testId) {
        AudioTest test = audioTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found"));

        List<Question> questions = questionRepository.findByTestId(testId);
        List<QuestionResponse> questionResponses = questions.stream().map(q -> {
            List<Answer> answers = answerRepository.findByQuestionId(q.getId());
            List<AnswerResponse> answerResponses = answers.stream()
                    .map(a -> AnswerResponse.builder()
                            .answerId(a.getId())
                            .content(a.getContent())
                            .isCorrect(a.getIsCorrect())
                            .build())
                    .collect(Collectors.toList());
            return QuestionResponse.builder()
                    .questionId(q.getId())
                    .content(q.getContent())
                    .testId(testId)
                    .answers(answerResponses)
                    .build();
        }).collect(Collectors.toList());

        return AiTestResponse.builder()
                .testId(test.getId())
                .testName(test.getTestName())
                .transcript(test.getTranscript())
                .topicId(test.getTopic().getId())
                .status(test.getStatus().name())
                .audioUrl(test.getAudioUrl())
                .questions(questionResponses)
                .build();
    }

    @Override
    @Transactional
    public void approveTest(Long testId) {
        AudioTest test = audioTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found"));
        test.setStatus(TestStatusEnum.PUBLISHED);
        audioTestRepository.save(test);

        TestReview review = new TestReview();
        review.setAction(ReviewActionEnum.APPROVED);
        review.setAdmin(getCurrentAdmin());
        review.setTest(test);
        review.setCreatedAt(LocalDateTime.now());
        testReviewRepository.save(review);
    }

    @Override
    @Transactional
    public void rejectTest(Long testId, AiRejectRequest request) {
        AudioTest test = audioTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found"));
        test.setStatus(TestStatusEnum.REJECTED);
        audioTestRepository.save(test);

        TestReview review = new TestReview();
        review.setAction(ReviewActionEnum.REJECTED);
        review.setAdmin(getCurrentAdmin());
        review.setTest(test);
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());
        testReviewRepository.save(review);
    }

    // Internal Helpers

    private void saveQuestionsAndAnswers(AudioTest test, List<Map<String, Object>> questionsMap) {
        int qOrder = 1;
        for (Map<String, Object> qMap : questionsMap) {
            Question question = new Question();
            question.setTest(test);
            question.setContent((String) qMap.get("content"));
            question.setQuestionOrder(qOrder++);
            question.setCreatedAt(LocalDateTime.now());
            question = questionRepository.save(question);

            List<Map<String, Object>> ansMaps = (List<Map<String, Object>>) qMap.get("answers");
            int aOrder = 1;
            List<Answer> answers = new ArrayList<>();
            for (Map<String, Object> ansMap : ansMaps) {
                Answer answer = new Answer();
                answer.setQuestion(question);
                answer.setContent((String) ansMap.get("content"));
                answer.setIsCorrect((Boolean) ansMap.get("isCorrect"));
                answer.setAnswerOrder(aOrder++);
                answers.add(answer);
            }
            answerRepository.saveAll(answers);
        }
    }

    private Admin getCurrentAdmin() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return adminRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Admin profile not found for current session"));
    }

    private String buildPrompt(AiGenerateRequest req, Topic topic) {
        String promptTemplate = """
                Generate a Japanese listening test about the topic: "%s".
                JLPT Level: %s
                Difficulty: %s
                Number of questions: %d
                Audio duration target: about %d minutes.

                The listening transcript must be a short natural conversation between two speakers.

                You MUST output exactly ONE JSON object matching the schema below.
                Output NOTHING ELSE.
                Do NOT include markdown blocks, explanations, or additional text.

                SCHEMA:

                {
                  "transcript": "<speak>...</speak>",
                  "questions": [
                    {
                      "content": "...",
                      "answers": [
                        { "content": "...", "isCorrect": true },
                        { "content": "...", "isCorrect": false },
                        { "content": "...", "isCorrect": false },
                        { "content": "...", "isCorrect": false }
                      ]
                    }
                  ]
                }

                TRANSCRIPT REQUIREMENTS (VERY IMPORTANT):

                The "transcript" must be valid AWS Polly SSML.

                Use the following structure:

                - Wrap everything inside <speak>
                - Use <prosody> to control speed and pitch
                - Use <break time="..."/> pauses between sentences
                - Use simple natural Japanese conversation suitable for JLPT %s learners
                - The conversation must sound like two people greeting or talking naturally

                Example structure to follow:

                <speak><prosody rate="slow" pitch="low">おはようございます。日本語のリスニングテストへようこそ。</prosody><break time="1s"/>今から、短い会話を聞いてください。</speak><break time="0.5s"/><prosody rate="medium">女：おはようございます。</prosody><break time="0.6s"/><prosody rate="medium">男：おはようございます。元気ですか。</prosody><break time="0.6s"/><prosody rate="medium">女：はい、元気です。今日はいい天気ですね。</prosody><break time="0.6s"/><prosody rate="medium">男：そうですね。では、また明日。</prosody><break time="1s"/><prosody rate="fast" pitch="high">では、質問に答えてください。</prosody><break time="1s"/>今日は <say-as interpret-as="date">2026/03/07</say-as> です。<break time="0.5s"/>ここは <sub alias="Tokyo">東京</sub> です。</speak><break time="1s"/><break time="1s"/></speak>

                IMPORTANT RULES:

                1. Produce exactly %d questions in the "questions" array.
                2. Each question must have exactly 4 answers.
                3. Exactly ONE answer must have "isCorrect": true.
                4. Questions must be based on the transcript.
                5. The transcript must be a conversation (not narration).
                6. The transcript must be returned as a SINGLE LINE string.
                7. Do NOT include newline characters like \\n inside the transcript.
                8. Do NOT wrap JSON in markdown.
                9. The final output must be valid JSON.
                """;

        String levelName = topic.getLevel() != null ? topic.getLevel().getLevelName() : "N5";

        return String.format(
                promptTemplate,
                topic.getTopicName(),
                levelName,
                req.getDifficulty(),
                req.getQuestionCount(),
                req.getDuration(),
                levelName,
                req.getQuestionCount());
    }
}
