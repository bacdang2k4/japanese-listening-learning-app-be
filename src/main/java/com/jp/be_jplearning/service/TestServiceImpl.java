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
    private final ProfileLevelRepository profileLevelRepository;
    private final ProfileTopicRepository profileTopicRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final TestResultRepository testResultRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final LearnerAnswerRepository learnerAnswerRepository;
    private final LevelRepository levelRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<TestSummaryResponse> getTestsByTopic(Long topicId, int page, int size) {
        if (size > 50)
            throw new BusinessException("Maximum page size is 50");
        if (!topicRepository.existsById(topicId))
            throw new ResourceNotFoundException("Topic not found");

        Pageable pageable = PageRequest.of(page, size);
        Page<AudioTest> testPage = testRepository.findByTopicIdAndStatus(topicId, TestStatusEnum.PUBLISHED, pageable);

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

        if (test.getStatus() != TestStatusEnum.PUBLISHED) {
            throw new BusinessException("Test is not published");
        }

        Profile profile = profileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        Long topicLevelId = test.getTopic().getLevel().getId();
        ProfileLevelId plId = new ProfileLevelId(profile.getId(), topicLevelId);
        if (!profileLevelRepository.existsById(plId)) {
            throw new BusinessException("Level chưa được mở khóa cho profile này");
        }

        ProfileTopicId ptId = new ProfileTopicId();
        ptId.setProfileId(profile.getId());
        ptId.setTopicId(test.getTopic().getId());

        profileTopicRepository.findById(ptId)
                .orElseGet(() -> {
                    ProfileTopic pt = new ProfileTopic();
                    pt.setId(ptId);
                    pt.setProfile(profile);
                    pt.setTopic(test.getTopic());
                    pt.setStatus(ProgressStatusEnum.LEARNING);
                    return profileTopicRepository.save(pt);
                });

        TestAttempt attempt = new TestAttempt();
        attempt.setProfile(profile);
        attempt.setTest(test);
        attempt.setMode(TestModeEnum.valueOf(request.getMode().toUpperCase()));
        attempt.setStatus(AttemptStatusEnum.IN_PROGRESS);
        attempt.setStartedAt(LocalDateTime.now());

        TestAttempt saved = testAttemptRepository.save(attempt);

        return StartTestResponse.builder()
                .resultId(saved.getId())
                .profileId(profile.getId())
                .testId(test.getId())
                .mode(saved.getMode().name())
                .status(saved.getStatus().name())
                .startedAt(saved.getStartedAt())
                .build();
    }

    @Override
    @Transactional
    public SubmitTestResponse submitTest(Long resultId, SubmitTestRequest request) {
        TestAttempt attempt = testAttemptRepository.findById(resultId)
                .orElseThrow(
                        () -> new BusinessException("Cannot submit before starting the test (TestAttempt not found)"));

        if (!attempt.getProfile().getId().equals(request.getProfileId())) {
            throw new BusinessException("Cannot submit the result of another user");
        }

        if (attempt.getStatus() == AttemptStatusEnum.COMPLETED) {
            throw new BusinessException("Test is already completed");
        }

        if (attempt.getStatus() != AttemptStatusEnum.IN_PROGRESS) {
            throw new BusinessException("Test is not in progress");
        }

        List<Question> questions = questionRepository.findByTestId(attempt.getTest().getId());
        Map<Long, Question> questionMap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));

        int correctCount = 0;
        int totalQuestions = questions.size();

        for (LearnerAnswerRequest ansReq : request.getAnswers()) {
            Question question = questionMap.get(ansReq.getQuestionId());
            if (question == null)
                continue;

            LearnerAnswer learnerAnswer = new LearnerAnswer();
            learnerAnswer.setAttempt(attempt);
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
        boolean isPassed = score >= (attempt.getTest().getPassCondition() == null ? 80
                : attempt.getTest().getPassCondition());

        int calculatedTotalTime = attempt.getStartedAt() != null
                ? (int) Duration.between(attempt.getStartedAt(), LocalDateTime.now()).getSeconds()
                : 0;

        attempt.setStatus(AttemptStatusEnum.COMPLETED);
        attempt.setCompletedAt(LocalDateTime.now());
        testAttemptRepository.save(attempt);

        TestResult testResult = new TestResult();
        testResult.setAttempt(attempt);
        testResult.setScore(score);
        testResult.setCorrectAnswers(correctCount);
        testResult.setIsPassed(isPassed);
        testResult.setTotalTime(calculatedTotalTime);
        testResult.setCreatedAt(LocalDateTime.now());

        TestResult savedResult = testResultRepository.save(testResult);

        if (isPassed && attempt.getMode() == TestModeEnum.EXAM) {
            handleProgression(attempt.getProfile(), attempt.getTest().getTopic());
        }

        return SubmitTestResponse.builder()
                .resultId(savedResult.getId())
                .score(score)
                .isPassed(isPassed)
                .status(attempt.getStatus().name())
                .build();
    }

    private void handleProgression(Profile profile, Topic topic) {
        Long profileId = profile.getId();
        Long topicId = topic.getId();
        Level topicLevel = topic.getLevel();

        // 1. Check if this topic now has at least one passed EXAM
        long passedExams = testResultRepository.countPassedByProfileAndTopicAndMode(
                profileId, topicId, TestModeEnum.EXAM);
        if (passedExams <= 1) {
            // First EXAM pass for this topic -> mark ProfileTopic as PASS
            ProfileTopicId ptId = new ProfileTopicId();
            ptId.setProfileId(profileId);
            ptId.setTopicId(topicId);
            profileTopicRepository.findById(ptId).ifPresent(pt -> {
                if (pt.getStatus() != ProgressStatusEnum.PASS) {
                    pt.setStatus(ProgressStatusEnum.PASS);
                    profileTopicRepository.save(pt);
                }
            });
        }

        // 2. Check if all topics in the level are now PASS
        List<ProfileTopic> levelTopics = profileTopicRepository
                .findByIdProfileIdAndTopicLevelId(profileId, topicLevel.getId());
        List<Topic> allTopicsInLevel = topicRepository.findByLevelId(topicLevel.getId());

        if (allTopicsInLevel.isEmpty()) return;

        boolean allTopicsPassed = allTopicsInLevel.stream().allMatch(t -> {
            return levelTopics.stream()
                    .filter(pt -> pt.getTopic().getId().equals(t.getId()))
                    .anyMatch(pt -> pt.getStatus() == ProgressStatusEnum.PASS);
        });

        if (!allTopicsPassed) return;

        // 3. Mark ProfileLevel as PASS
        ProfileLevelId plId = new ProfileLevelId(profileId, topicLevel.getId());
        profileLevelRepository.findById(plId).ifPresent(pl -> {
            if (pl.getStatus() != ProgressStatusEnum.PASS) {
                pl.setStatus(ProgressStatusEnum.PASS);
                profileLevelRepository.save(pl);
            }
        });

        // 4. Unlock next level
        if (topicLevel.getLevelOrder() == null) return;
        int nextOrder = topicLevel.getLevelOrder() + 1;
        levelRepository.findByLevelOrder(nextOrder).ifPresent(nextLevel -> {
            ProfileLevelId nextPlId = new ProfileLevelId(profileId, nextLevel.getId());
            if (!profileLevelRepository.existsById(nextPlId)) {
                ProfileLevel nextPl = new ProfileLevel();
                nextPl.setId(nextPlId);
                nextPl.setProfile(profile);
                nextPl.setLevel(nextLevel);
                nextPl.setStatus(ProgressStatusEnum.LEARNING);
                profileLevelRepository.save(nextPl);

                List<Topic> nextTopics = topicRepository.findByLevelId(nextLevel.getId());
                for (Topic nextTopic : nextTopics) {
                    ProfileTopicId nextPtId = new ProfileTopicId();
                    nextPtId.setProfileId(profileId);
                    nextPtId.setTopicId(nextTopic.getId());
                    if (!profileTopicRepository.existsById(nextPtId)) {
                        ProfileTopic nextPt = new ProfileTopic();
                        nextPt.setId(nextPtId);
                        nextPt.setProfile(profile);
                        nextPt.setTopic(nextTopic);
                        nextPt.setStatus(ProgressStatusEnum.LEARNING);
                        profileTopicRepository.save(nextPt);
                    }
                }
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public TestResultDetailResponse getTestResultDetail(Long resultId, Long profileId) {
        TestResult testResult = testResultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("TestResult not found"));

        if (!testResult.getAttempt().getProfile().getId().equals(profileId)) {
            throw new BusinessException("Cannot access the result of another user");
        }

        List<LearnerAnswer> learnerAnswers = learnerAnswerRepository.findByAttemptId(testResult.getAttempt().getId());

        List<QuestionResultResponse> questionResults = learnerAnswers.stream().map(la -> {
            Question q = la.getQuestion();
            Answer selected = la.getSelectedAnswer();

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
                .testName(testResult.getAttempt().getTest().getTestName())
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
        Page<TestResult> resultPage = testResultRepository.findByAttempt_Profile_Id(profileId, pageable);

        List<TestHistoryResponse> content = resultPage.getContent().stream().map(tr -> TestHistoryResponse.builder()
                .resultId(tr.getId())
                .testName(tr.getAttempt().getTest().getTestName())
                .mode(tr.getAttempt().getMode().name())
                .score(tr.getScore())
                .isPassed(tr.getIsPassed())
                .createdAt(tr.getCreatedAt())
                .build()).collect(Collectors.toList());

        return new PaginationResponse<>(content, resultPage.getNumber(), resultPage.getSize(),
                resultPage.getTotalElements(), resultPage.getTotalPages(), resultPage.isLast());
    }
}
