package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.BusinessException;
import com.jp.be_jplearning.common.ResourceNotFoundException;
import com.jp.be_jplearning.dto.CreateProfileRequest;
import com.jp.be_jplearning.dto.ProfileProgressResponse;
import com.jp.be_jplearning.dto.ProfileResponse;
import com.jp.be_jplearning.entity.*;
import com.jp.be_jplearning.entity.enums.ProgressStatusEnum;
import com.jp.be_jplearning.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final LearnerRepository learnerRepository;
    private final ProfileRepository profileRepository;
    private final LevelRepository levelRepository;
    private final TopicRepository topicRepository;
    private final ProfileLevelRepository profileLevelRepository;
    private final ProfileTopicRepository profileTopicRepository;
    private final TestResultRepository testResultRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProfileResponse> getMyProfiles() {
        Learner learner = getCurrentLearner();
        List<Profile> profiles = profileRepository.findByLearnerId(learner.getId());

        return profiles.stream().map(this::mapToProfileResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProfileResponse createProfile(CreateProfileRequest request) {
        Learner learner = getCurrentLearner();

        Level level = levelRepository.findById(request.getLevelId())
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + request.getLevelId()));

        Profile profile = new Profile();
        profile.setLearner(learner);
        profile.setStartDate(LocalDateTime.now());
        profile = profileRepository.save(profile);

        ProfileLevelId plId = new ProfileLevelId(profile.getId(), level.getId());
        ProfileLevel profileLevel = new ProfileLevel();
        profileLevel.setId(plId);
        profileLevel.setProfile(profile);
        profileLevel.setLevel(level);
        profileLevel.setStatus(ProgressStatusEnum.LEARNING);
        profileLevelRepository.save(profileLevel);

        List<Topic> topics = topicRepository.findByLevelId(level.getId());
        for (Topic topic : topics) {
            ProfileTopicId ptId = new ProfileTopicId();
            ptId.setProfileId(profile.getId());
            ptId.setTopicId(topic.getId());

            ProfileTopic profileTopic = new ProfileTopic();
            profileTopic.setId(ptId);
            profileTopic.setProfile(profile);
            profileTopic.setTopic(topic);
            profileTopic.setStatus(ProgressStatusEnum.LEARNING);
            profileTopicRepository.save(profileTopic);
        }

        return mapToProfileResponse(profile, level);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileProgressResponse getProfileProgress(Long profileId) {
        Learner learner = getCurrentLearner();

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + profileId));

        if (!profile.getLearner().getId().equals(learner.getId())) {
            throw new BusinessException("Cannot access another user's profile");
        }

        List<Level> allLevels = levelRepository.findAllByOrderByLevelOrderAsc();
        List<ProfileLevel> profileLevels = profileLevelRepository.findByProfileIdWithLevel(profileId);
        Map<Long, ProfileLevel> profileLevelMap = profileLevels.stream()
                .collect(Collectors.toMap(pl -> pl.getLevel().getId(), pl -> pl));

        List<ProfileTopic> allProfileTopics = profileTopicRepository.findByProfileIdWithTopic(profileId);
        Map<Long, List<ProfileTopic>> profileTopicsByLevel = allProfileTopics.stream()
                .collect(Collectors.groupingBy(pt -> pt.getTopic().getLevel().getId()));

        List<ProfileProgressResponse.LevelProgressItem> levelItems = new ArrayList<>();

        for (Level level : allLevels) {
            ProfileLevel pl = profileLevelMap.get(level.getId());
            String levelStatus;
            List<ProfileProgressResponse.TopicProgressItem> topicItems = new ArrayList<>();

            if (pl == null) {
                levelStatus = "LOCKED";
            } else {
                levelStatus = pl.getStatus().name();

                List<Topic> topics = topicRepository.findByLevelId(level.getId());
                List<ProfileTopic> profileTopics = profileTopicsByLevel.getOrDefault(level.getId(), List.of());
                Map<Long, ProfileTopic> ptMap = profileTopics.stream()
                        .collect(Collectors.toMap(pt -> pt.getTopic().getId(), pt -> pt));

                for (Topic topic : topics) {
                    ProfileTopic pt = ptMap.get(topic.getId());
                    String topicStatus = pt != null ? pt.getStatus().name() : "LEARNING";

                    long testCount = testResultRepository.countByProfileAndTopic(profileId, topic.getId());
                    long passedCount = testResultRepository.countPassedByProfileAndTopic(profileId, topic.getId());

                    topicItems.add(ProfileProgressResponse.TopicProgressItem.builder()
                            .topicId(topic.getId())
                            .topicName(topic.getTopicName())
                            .status(topicStatus)
                            .testCount(testCount)
                            .passedTestCount(passedCount)
                            .build());
                }
            }

            levelItems.add(ProfileProgressResponse.LevelProgressItem.builder()
                    .levelId(level.getId())
                    .levelName(level.getLevelName())
                    .levelOrder(level.getLevelOrder())
                    .status(levelStatus)
                    .topics(topicItems)
                    .build());
        }

        return ProfileProgressResponse.builder()
                .profileId(profileId)
                .profileStatus(profile.getStatus().name())
                .levels(levelItems)
                .build();
    }

    private ProfileResponse mapToProfileResponse(Profile profile) {
        List<ProfileLevel> profileLevels = profileLevelRepository.findByProfileIdWithLevel(profile.getId());

        ProfileLevel currentLevel = profileLevels.stream()
                .filter(pl -> pl.getStatus() == ProgressStatusEnum.LEARNING)
                .findFirst()
                .orElse(profileLevels.isEmpty() ? null : profileLevels.getLast());

        return ProfileResponse.builder()
                .profileId(profile.getId())
                .status(profile.getStatus().name())
                .startDate(profile.getStartDate())
                .currentLevelName(currentLevel != null ? currentLevel.getLevel().getLevelName() : null)
                .currentLevelId(currentLevel != null ? currentLevel.getLevel().getId() : null)
                .build();
    }

    private ProfileResponse mapToProfileResponse(Profile profile, Level level) {
        return ProfileResponse.builder()
                .profileId(profile.getId())
                .status(profile.getStatus().name())
                .startDate(profile.getStartDate())
                .currentLevelName(level.getLevelName())
                .currentLevelId(level.getId())
                .build();
    }

    private Learner getCurrentLearner() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return learnerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Learner not found"));
    }
}
