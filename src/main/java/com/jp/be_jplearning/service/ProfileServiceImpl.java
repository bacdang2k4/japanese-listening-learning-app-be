package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.BusinessException;
import com.jp.be_jplearning.common.ResourceNotFoundException;
import com.jp.be_jplearning.dto.CreateProfileRequest;
import com.jp.be_jplearning.dto.ProfileProgressResponse;
import com.jp.be_jplearning.dto.ProfileResponse;
import com.jp.be_jplearning.entity.*;
import com.jp.be_jplearning.entity.enums.ProgressStatusEnum;
import com.jp.be_jplearning.integration.AwsS3Client;
import com.jp.be_jplearning.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final LearnerRepository learnerRepository;
    private final ProfileRepository profileRepository;
    private final LevelRepository levelRepository;
    private final TopicRepository topicRepository;
    private final ProfileLevelRepository profileLevelRepository;
    private final ProfileTopicRepository profileTopicRepository;
    private final TestResultRepository testResultRepository;
    private final AwsS3Client awsS3Client;

    private static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024; // 5MB

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
        profile.setName(request.getName());
        profile.setStartDate(LocalDateTime.now());
        profile = profileRepository.save(profile);

        ProfileLevelId plId = new ProfileLevelId(profile.getId(), level.getId());
        ProfileLevel profileLevel = new ProfileLevel();
        profileLevel.setId(plId);
        profileLevel.setProfile(profile);
        profileLevel.setLevel(level);
        profileLevel.setStatus(ProgressStatusEnum.LEARNING);
        profileLevelRepository.save(profileLevel);

        List<Topic> topics = topicRepository.findByLevelIdOrderByTopicOrderAsc(level.getId());
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

                List<Topic> topics = topicRepository.findByLevelIdOrderByTopicOrderAsc(level.getId());
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

    @Override
    @Transactional
    public String uploadProfileAvatar(Long profileId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("Avatar file cannot be empty");
        }
        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new BusinessException("Avatar file size must not exceed 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("Only image files are allowed");
        }

        Learner learner = getCurrentLearner();
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + profileId));
        if (!profile.getLearner().getId().equals(learner.getId())) {
            throw new BusinessException("Cannot modify another user's profile");
        }

        try {
            // Delete old avatar from S3 if exists
            if (profile.getAvatarUrl() != null && !profile.getAvatarUrl().isEmpty()) {
                try {
                    String oldKey = extractS3Key(profile.getAvatarUrl());
                    awsS3Client.deleteByKey(oldKey);
                } catch (Exception e) {
                    log.warn("Failed to delete old profile avatar from S3", e);
                }
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String fileName = UUID.randomUUID().toString() + extension;
            String s3Key = "avatars/profile/" + profileId + "/" + fileName;

            String avatarUrl = awsS3Client.uploadByKey(s3Key, file.getBytes(), contentType);

            profile.setAvatarUrl(avatarUrl);
            profileRepository.save(profile);

            log.info("Profile {} updated avatar successfully", profileId);
            return avatarUrl;

        } catch (IOException e) {
            log.error("Failed to read avatar file for upload", e);
            throw new BusinessException("Failed to process image file");
        }
    }

    @Override
    @Transactional
    public void deleteProfileAvatar(Long profileId) {
        Learner learner = getCurrentLearner();
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + profileId));
        if (!profile.getLearner().getId().equals(learner.getId())) {
            throw new BusinessException("Cannot modify another user's profile");
        }

        if (profile.getAvatarUrl() != null && !profile.getAvatarUrl().isEmpty()) {
            try {
                String s3Key = extractS3Key(profile.getAvatarUrl());
                awsS3Client.deleteByKey(s3Key);
            } catch (Exception e) {
                log.warn("Failed to delete profile avatar from S3, proceeding to remove URL from DB", e);
            }
            profile.setAvatarUrl(null);
            profileRepository.save(profile);
            log.info("Profile {} deleted avatar successfully", profileId);
        }
    }

    @Override
    @Transactional
    public ProfileResponse updateProfileName(Long profileId, String name) {
        Learner learner = getCurrentLearner();
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + profileId));
        if (!profile.getLearner().getId().equals(learner.getId())) {
            throw new BusinessException("Cannot modify another user's profile");
        }

        profile.setName(name);
        profileRepository.save(profile);
        log.info("Profile {} updated name to '{}'", profileId, name);
        return mapToProfileResponse(profile);
    }

    private String extractS3Key(String url) {
        // URL format: https://{bucket}.s3.{region}.amazonaws.com/{key}
        String marker = ".amazonaws.com/";
        int idx = url.indexOf(marker);
        if (idx >= 0) {
            return url.substring(idx + marker.length());
        }
        // Fallback: return the last path segments
        return url.substring(url.indexOf("avatars/"));
    }

    private ProfileResponse mapToProfileResponse(Profile profile) {
        List<ProfileLevel> profileLevels = profileLevelRepository.findByProfileIdWithLevel(profile.getId());

        ProfileLevel currentLevel = profileLevels.stream()
                .filter(pl -> pl.getStatus() == ProgressStatusEnum.LEARNING)
                .findFirst()
                .orElse(profileLevels.isEmpty() ? null : profileLevels.getLast());

        return ProfileResponse.builder()
                .profileId(profile.getId())
                .name(profile.getName())
                .avatarUrl(profile.getAvatarUrl())
                .status(profile.getStatus().name())
                .startDate(profile.getStartDate())
                .currentLevelName(currentLevel != null ? currentLevel.getLevel().getLevelName() : null)
                .currentLevelId(currentLevel != null ? currentLevel.getLevel().getId() : null)
                .build();
    }

    private ProfileResponse mapToProfileResponse(Profile profile, Level level) {
        return ProfileResponse.builder()
                .profileId(profile.getId())
                .name(profile.getName())
                .avatarUrl(profile.getAvatarUrl())
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
