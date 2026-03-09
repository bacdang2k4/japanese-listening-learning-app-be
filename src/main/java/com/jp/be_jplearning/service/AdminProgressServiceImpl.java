package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.common.SortUtils;
import com.jp.be_jplearning.dto.AdminProfileResponse;
import com.jp.be_jplearning.dto.AdminTestResultResponse;
import com.jp.be_jplearning.entity.*;
import com.jp.be_jplearning.entity.enums.ProgressStatusEnum;
import com.jp.be_jplearning.entity.enums.TestModeEnum;
import com.jp.be_jplearning.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProgressServiceImpl implements AdminProgressService {

    private final ProfileRepository profileRepository;
    private final ProfileLevelRepository profileLevelRepository;
    private final ProfileTopicRepository profileTopicRepository;
    private final TestResultRepository testResultRepository;

    private static final Set<String> PROFILE_SORT_COLUMNS = Set.of("id", "startDate", "status");
    private static final Set<String> RESULT_SORT_COLUMNS = Set.of("id", "score", "createdAt", "isPassed");

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<AdminProfileResponse> getProfiles(int page, int size, String keyword, String sortStr) {
        Sort sort = SortUtils.parseSort(sortStr, PROFILE_SORT_COLUMNS, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Profile> profilePage = profileRepository.searchProfiles(keyword, pageable);

        List<AdminProfileResponse> content = profilePage.getContent().stream()
                .map(this::mapToAdminProfileResponse)
                .collect(Collectors.toList());

        return PaginationResponse.<AdminProfileResponse>builder()
                .content(content)
                .page(profilePage.getNumber())
                .size(profilePage.getSize())
                .totalElements(profilePage.getTotalElements())
                .totalPages(profilePage.getTotalPages())
                .last(profilePage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<AdminTestResultResponse> getTestResults(int page, int size, String keyword,
            String mode, Boolean passed, String sortStr) {
        Sort sort = SortUtils.parseSort(sortStr, RESULT_SORT_COLUMNS, "id");
        Pageable pageable = PageRequest.of(page, size, sort);

        TestModeEnum modeEnum = null;
        if (mode != null && !mode.isBlank()) {
            try {
                modeEnum = TestModeEnum.valueOf(mode.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        Page<TestResult> resultPage = testResultRepository.searchTestResults(keyword, modeEnum, passed, pageable);

        List<AdminTestResultResponse> content = resultPage.getContent().stream()
                .map(this::mapToAdminTestResultResponse)
                .collect(Collectors.toList());

        return PaginationResponse.<AdminTestResultResponse>builder()
                .content(content)
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .last(resultPage.isLast())
                .build();
    }

    private AdminProfileResponse mapToAdminProfileResponse(Profile profile) {
        Learner learner = profile.getLearner();

        List<ProfileLevel> profileLevels = profileLevelRepository.findByProfileIdWithLevel(profile.getId());
        int completedLevels = (int) profileLevels.stream()
                .filter(pl -> pl.getStatus() == ProgressStatusEnum.PASS)
                .count();

        List<ProfileTopic> allTopics = profileTopicRepository.findByProfileIdWithTopic(profile.getId());
        long completedTopics = allTopics.stream()
                .filter(pt -> pt.getStatus() == ProgressStatusEnum.PASS)
                .count();

        int totalScore = testResultRepository.sumScoreByProfileId(profile.getId());

        return AdminProfileResponse.builder()
                .profileId(profile.getId())
                .learnerId(learner.getId())
                .learnerName(learner.getLastName() + " " + learner.getFirstName())
                .learnerEmail(learner.getEmail())
                .learnerUsername(learner.getUsername())
                .status(profile.getStatus().name())
                .completedLevels(completedLevels)
                .completedTopics((int) completedTopics)
                .totalScore(totalScore)
                .startDate(profile.getStartDate())
                .build();
    }

    private AdminTestResultResponse mapToAdminTestResultResponse(TestResult result) {
        TestAttempt attempt = result.getAttempt();
        Learner learner = attempt.getProfile().getLearner();
        AudioTest test = attempt.getTest();

        return AdminTestResultResponse.builder()
                .resultId(result.getId())
                .learnerId(learner.getId())
                .learnerName(learner.getLastName() + " " + learner.getFirstName())
                .learnerEmail(learner.getEmail())
                .testName(test.getTestName())
                .levelName(test.getTopic() != null && test.getTopic().getLevel() != null
                        ? test.getTopic().getLevel().getLevelName() : null)
                .topicName(test.getTopic() != null ? test.getTopic().getTopicName() : null)
                .mode(attempt.getMode().name())
                .score(result.getScore())
                .isPassed(result.getIsPassed())
                .totalTime(result.getTotalTime())
                .completedAt(attempt.getCompletedAt())
                .build();
    }
}
