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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        test.setStatus(TestStatusEnum.AI_GENERATED);
        test.setIsAiGenerated(true);
        test.setCreatedByAdmin(currentAdmin);
        test.setCreatedAt(LocalDateTime.now());

        test = audioTestRepository.save(test);

        String prompt = buildPrompt(request, topic);

        AIGenerationLog logEntry = new AIGenerationLog();
        logEntry.setTest(test);
        logEntry.setPrompt(prompt);
        logEntry.setModel("DEFAULT_MOCK_MODEL");
        logEntry.setGeneratedAt(LocalDateTime.now());

        try {
            String rawJson = aiClient.generateListeningTest(prompt);
            logEntry.setRawResponse(rawJson);

            // Parse response
            Map<String, Object> aiMap = objectMapper.readValue(rawJson, new TypeReference<Map<String, Object>>() {
            });
            String transcript = (String) aiMap.get("transcript");

            test.setTranscript(transcript);
            test.setStatus(TestStatusEnum.PENDING_REVIEW);
            audioTestRepository.save(test);

            List<Map<String, Object>> questionsMap = (List<Map<String, Object>>) aiMap.get("questions");
            saveQuestionsAndAnswers(test, questionsMap);

            logEntry.setStatus(GenerationStatusEnum.SUCCESS);
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

        aiGenerationLogRepository.save(logEntry);

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
        return String.format(
                "Generate a Japanese listening test about the topic: '%s'. Level: %d. " +
                        "Difficulty: %s. Number of questions: %d. Audio duration context: ~%d minutes.\n\n" +
                        "You MUST output exactly ONE JSON object matching this schema. Output NOTHING ELSE. No markdown blocks, no conversational text.\n\n"
                        +
                        "{\n" +
                        "  \"transcript\": \"[Write a cohesive Japanese conversation/monologue transcript appropriate for the topic and level]\",\n"
                        +
                        "  \"questions\": [\n" +
                        "    {\n" +
                        "      \"content\": \"[Question 1 based on the transcript, in Japanese or English depending on level]\",\n"
                        +
                        "      \"answers\": [\n" +
                        "        { \"content\": \"[Answer option A]\", \"isCorrect\": true },\n" +
                        "        { \"content\": \"[Answer option B]\", \"isCorrect\": false },\n" +
                        "        { \"content\": \"[Answer option C]\", \"isCorrect\": false },\n" +
                        "        { \"content\": \"[Answer option D]\", \"isCorrect\": false }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}\n\n" +
                        "RULES:\n" +
                        "1. Produce exactly %d questions in the \"questions\" JSON array.\n" +
                        "2. Each question MUST have exactly 4 answers.\n" +
                        "3. Exactly ONE answer per question must have isCorrect set to true.",
                topic.getTopicName(), topic.getLevel().getId(), req.getDifficulty(), req.getQuestionCount(),
                req.getDuration(), req.getQuestionCount());
    }
}
