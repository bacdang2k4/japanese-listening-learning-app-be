package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.BusinessException;
import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.common.ResourceNotFoundException;
import com.jp.be_jplearning.dto.*;
import com.jp.be_jplearning.entity.*;
import com.jp.be_jplearning.entity.enums.*;
import com.jp.be_jplearning.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final AudioTestRepository testRepository;
    private final TopicRepository topicRepository;
    private final ProfileRepository profileRepository;
    private final ProfileTopicRepository profileTopicRepository;
    private final TestResultRepository testResultRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final LearnerAnswerRepository learnerAnswerRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<TestSummaryResponse> getTestsByTopic(Long topicId, int page, int size) {
        if (size > 50)
            throw new BusinessException("Maximum page size is 50");
        if (!topicRepository.existsById(topicId))
            throw new ResourceNotFoundException("Topic not found");

        Pageable pageable = PageRequest.of(page, size);
        Page<AudioTest> testPage = testRepository.findByTopicIdAndStatus(topicId, TestStatusEnum.Published, pageable);

        List<TestSummaryResponse> content = testPage.getContent().stream().map(test -> TestSummaryResponse.builder()
                .testId(test.getId())
                .testName(test.getTestName())
                .duration(test.getDuration())
                .passCondition(test.getPassCondition())
                .build()).collect(Collectors.toList());

        return new PaginationResponse<>(content, testPage.getNumber(), testPage.getSize(),
                testPage.getTotalElements(), testPage.getTotalPages(), testPage.isLast());
    }

    @Override
    @Transactional
    public StartTestResponse startTest(Long testId, StartTestRequest request) {
        AudioTest test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found"));

        if (test.getStatus() != TestStatusEnum.Published) {
            throw new BusinessException("Test is not published");
        }

        Profile profile = profileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        ProfileTopicId ptId = new ProfileTopicId();
        ptId.setProfileId(profile.getId());
        ptId.setTopicId(test.getTopic().getId());

        ProfileTopic profileTopic = profileTopicRepository.findById(ptId)
                .orElseGet(() -> {
                    ProfileTopic pt = new ProfileTopic();
                    pt.setId(ptId);
                    pt.setProfile(profile);
                    pt.setTopic(test.getTopic());
                    pt.setStatus(ProfileLevelStatusEnum.Learning);
                    return profileTopicRepository.save(pt);
                });

        TestResult testResult = new TestResult();
        testResult.setProfileTopic(profileTopic);
        testResult.setTest(test);
        testResult.setMode(TestModeEnum.valueOf(request.getMode()));
        testResult.setStatus(TestResultStatusEnum.In_Progress);
        testResult.setCreatedAt(LocalDateTime.now());

        TestResult saved = testResultRepository.save(testResult);

        return StartTestResponse.builder()
                .resultId(saved.getId())
                .profileId(profile.getId())
                .testId(test.getId())
                .mode(saved.getMode().name())
                .status(saved.getStatus().name())
                .startedAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public SubmitTestResponse submitTest(Long resultId, SubmitTestRequest request) {
        TestResult testResult = testResultRepository.findById(resultId)
                .orElseThrow(
                        () -> new BusinessException("Cannot submit before starting the test (TestResult not found)"));

        if (!testResult.getProfileTopic().getProfile().getId().equals(request.getProfileId())) {
            throw new BusinessException("Cannot submit the result of another user");
        }

        if (testResult.getStatus() == TestResultStatusEnum.Completed) {
            throw new BusinessException("Test is already completed");
        }

        if (testResult.getStatus() != TestResultStatusEnum.In_Progress) {
            throw new BusinessException("Test is not in progress");
        }

        List<Question> questions = questionRepository.findByTestId(testResult.getTest().getId());
        Map<Long, Question> questionMap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));

        int correctCount = 0;
        int totalQuestions = questions.size();

        for (LearnerAnswerRequest ansReq : request.getAnswers()) {
            Question question = questionMap.get(ansReq.getQuestionId());
            if (question == null)
                continue; // Skip invalid question

            LearnerAnswer learnerAnswer = new LearnerAnswer();
            learnerAnswer.setTestResult(testResult);
            learnerAnswer.setQuestion(question);

            if (ansReq.getSelectedAnswerId() != null) {
                Answer answer = answerRepository.findById(ansReq.getSelectedAnswerId()).orElse(null);
                if (answer != null) {
                    learnerAnswer.setSelectedAnswer(answer);
                    if (Boolean.TRUE.equals(answer.getIsCorrect())) {
                        correctCount++;
                    }
                }
            }

            learnerAnswerRepository.save(learnerAnswer);
        }

        int score = totalQuestions == 0 ? 0 : (int) Math.round(((double) correctCount / totalQuestions) * 100);
        boolean isPassed = score >= (testResult.getTest().getPassCondition() == null ? 80
                : testResult.getTest().getPassCondition());

        int calculatedTotalTime = testResult.getCreatedAt() != null
                ? (int) Duration.between(testResult.getCreatedAt(), LocalDateTime.now()).getSeconds()
                : 0;

        testResult.setTotalTime(calculatedTotalTime);
        testResult.setScore(score);
        testResult.setIsPassed(isPassed);
        testResult.setStatus(TestResultStatusEnum.Completed);

        testResultRepository.save(testResult);

        return SubmitTestResponse.builder()
                .resultId(testResult.getId())
                .score(score)
                .isPassed(isPassed)
                .status(testResult.getStatus().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TestResultDetailResponse getTestResultDetail(Long resultId, Long profileId) {
        TestResult testResult = testResultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("TestResult not found"));

        if (!testResult.getProfileTopic().getProfile().getId().equals(profileId)) {
            throw new BusinessException("Cannot access the result of another user");
        }

        List<LearnerAnswer> learnerAnswers = learnerAnswerRepository.findByTestResultId(resultId);

        List<QuestionResultResponse> questionResults = learnerAnswers.stream().map(la -> {
            Question q = la.getQuestion();
            Answer selected = la.getSelectedAnswer();

            // Find correct answer for this question directly without extra query if mapped
            // or we could fetch it
            Answer correct = q.getTest() == null ? null
                    : answerRepository.findAll().stream()
                            .filter(a -> a.getQuestion().getId().equals(q.getId())
                                    && Boolean.TRUE.equals(a.getIsCorrect()))
                            .findFirst().orElse(null);

            return QuestionResultResponse.builder()
                    .questionId(q.getId())
                    .questionContent(q.getContent())
                    .selectedAnswer(selected != null ? selected.getContent() : null)
                    .correctAnswer(correct != null ? correct.getContent() : null)
                    .isCorrect(selected != null && Boolean.TRUE.equals(selected.getIsCorrect()))
                    .build();
        }).collect(Collectors.toList());

        return TestResultDetailResponse.builder()
                .resultId(testResult.getId())
                .testName(testResult.getTest().getTestName())
                .score(testResult.getScore())
                .isPassed(testResult.getIsPassed())
                .totalTime(testResult.getTotalTime())
                .questionResults(questionResults)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<TestHistoryResponse> getTestHistoryByProfile(Long profileId, int page, int size) {
        if (size > 50)
            throw new BusinessException("Maximum page size is 50");

        Pageable pageable = PageRequest.of(page, size);
        Page<TestResult> resultPage = testResultRepository.findByProfileTopic_Id_ProfileId(profileId, pageable);

        List<TestHistoryResponse> content = resultPage.getContent().stream().map(tr -> TestHistoryResponse.builder()
                .resultId(tr.getId())
                .testName(tr.getTest().getTestName())
                .mode(tr.getMode().name())
                .score(tr.getScore())
                .isPassed(tr.getIsPassed())
                .createdAt(tr.getCreatedAt())
                .build()).collect(Collectors.toList());

        return new PaginationResponse<>(content, resultPage.getNumber(), resultPage.getSize(),
                resultPage.getTotalElements(), resultPage.getTotalPages(), resultPage.isLast());
    }
}
