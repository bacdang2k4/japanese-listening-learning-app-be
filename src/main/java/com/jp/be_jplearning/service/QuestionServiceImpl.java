package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.ResourceNotFoundException;
import com.jp.be_jplearning.dto.AnswerRequest;
import com.jp.be_jplearning.dto.AnswerResponse;
import com.jp.be_jplearning.dto.QuestionRequest;
import com.jp.be_jplearning.dto.QuestionResponse;
import com.jp.be_jplearning.entity.Answer;
import com.jp.be_jplearning.entity.AudioTest;
import com.jp.be_jplearning.entity.Question;
import com.jp.be_jplearning.repository.AnswerRepository;
import com.jp.be_jplearning.repository.AudioTestRepository;
import com.jp.be_jplearning.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final AudioTestRepository audioTestRepository;

    @Override
    @Transactional
    public QuestionResponse createQuestion(QuestionRequest request) {
        validateAnswers(request.getAnswers());

        AudioTest test = audioTestRepository.findById(request.getTestId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("AudioTest not found with id: " + request.getTestId()));

        Question question = new Question();
        question.setContent(request.getContent());
        question.setTest(test);
        question.setCreatedAt(LocalDateTime.now());

        // Let's set order if needed, but for now we skip or simple assign.
        question = questionRepository.save(question);

        List<Answer> answers = saveAnswersForQuestion(question, request.getAnswers());

        return mapToResponse(question, answers);
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestion(Long questionId, QuestionRequest request) {
        validateAnswers(request.getAnswers());

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        if (!question.getTest().getId().equals(request.getTestId())) {
            AudioTest test = audioTestRepository.findById(request.getTestId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("AudioTest not found with id: " + request.getTestId()));
            question.setTest(test);
        }

        question.setContent(request.getContent());
        question = questionRepository.save(question);

        // Remove old answers safely
        List<Answer> oldAnswers = answerRepository.findByQuestionId(questionId);
        answerRepository.deleteAll(oldAnswers);

        // Add new answers
        List<Answer> defaultConfiguredAnswers = saveAnswersForQuestion(question, request.getAnswers());

        return mapToResponse(question, defaultConfiguredAnswers);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException("Question not found with id: " + questionId);
        }
        questionRepository.deleteById(questionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByTestId(Long testId) {
        if (!audioTestRepository.existsById(testId)) {
            throw new ResourceNotFoundException("AudioTest not found with id: " + testId);
        }

        List<Question> questions = questionRepository.findByTestIdOrderByQuestionOrder(testId);

        List<Long> questionIds = questions.stream().map(Question::getId).toList();
        Map<Long, List<Answer>> answerMap = questionIds.isEmpty()
                ? Map.of()
                : answerRepository.findByQuestionIds(questionIds).stream()
                        .collect(Collectors.groupingBy(a -> a.getQuestion().getId()));

        return questions.stream()
                .map(q -> mapToResponse(q, answerMap.getOrDefault(q.getId(), List.of())))
                .collect(Collectors.toList());
    }

    private void validateAnswers(List<AnswerRequest> answers) {
        if (answers == null || answers.size() < 2) {
            throw new com.jp.be_jplearning.common.BusinessException("A question must have at least 2 answers");
        }

        long correctCount = answers.stream().filter(AnswerRequest::getIsCorrect).count();
        if (correctCount != 1) {
            throw new com.jp.be_jplearning.common.BusinessException("A question must have exactly one correct answer");
        }
    }

    private List<Answer> saveAnswersForQuestion(Question question, List<AnswerRequest> answerRequests) {
        List<Answer> answers = new ArrayList<>();
        int order = 1;
        for (AnswerRequest answerDto : answerRequests) {
            Answer answer = new Answer();
            answer.setContent(answerDto.getContent());
            answer.setIsCorrect(answerDto.getIsCorrect());
            answer.setQuestion(question);
            answer.setAnswerOrder(order++);
            answers.add(answer);
        }
        return answerRepository.saveAll(answers);
    }

    private QuestionResponse mapToResponse(Question question, List<Answer> answers) {
        List<AnswerResponse> answerResponses = answers.stream()
                .map(a -> AnswerResponse.builder()
                        .answerId(a.getId())
                        .content(a.getContent())
                        .isCorrect(a.getIsCorrect())
                        .build())
                .collect(Collectors.toList());

        return QuestionResponse.builder()
                .questionId(question.getId())
                .content(question.getContent())
                .testId(question.getTest() != null ? question.getTest().getId() : null)
                .answers(answerResponses)
                .build();
    }
}
