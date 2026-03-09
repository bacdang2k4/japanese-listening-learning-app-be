package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.BusinessException;
import com.jp.be_jplearning.common.ResourceNotFoundException;
import com.jp.be_jplearning.dto.LearnerAccountResponse;
import com.jp.be_jplearning.dto.UpdateLearnerInfoRequest;
import com.jp.be_jplearning.entity.Learner;
import com.jp.be_jplearning.integration.AwsS3Client;
import com.jp.be_jplearning.repository.LearnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearnerProfileServiceImpl implements LearnerProfileService {

    private final LearnerRepository learnerRepository;
    private final AwsS3Client awsS3Client;

    private static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    @Transactional(readOnly = true)
    public LearnerAccountResponse getMyAccount() {
        Learner learner = getCurrentLearner();
        return mapToAccountResponse(learner);
    }

    @Override
    @Transactional
    public LearnerAccountResponse updateMyInfo(UpdateLearnerInfoRequest request) {
        Learner learner = getCurrentLearner();

        if (!learner.getEmail().equals(request.getEmail())) {
            learnerRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(learner.getId())) {
                    throw new BusinessException("Email đã được sử dụng bởi tài khoản khác");
                }
            });
        }

        learner.setFirstName(request.getFirstName());
        learner.setLastName(request.getLastName());
        learner.setEmail(request.getEmail());
        learnerRepository.save(learner);

        log.info("Learner {} updated account info", learner.getUsername());
        return mapToAccountResponse(learner);
    }

    @Override
    @Transactional
    public String uploadAvatar(MultipartFile file) {
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

        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String fileName = UUID.randomUUID().toString() + extension;

            // Upload to S3
            String avatarUrl = awsS3Client.uploadImageBytes(fileName, file.getBytes(), contentType);

            // Update learner record
            learner.setAvatarUrl(avatarUrl);
            learnerRepository.save(learner);

            log.info("Learner {} updated avatar successfully", learner.getUsername());
            return avatarUrl;

        } catch (IOException e) {
            log.error("Failed to read avatar file for upload", e);
            throw new BusinessException("Failed to process image file");
        }
    }

    @Override
    @Transactional
    public void deleteAvatar() {
        Learner learner = getCurrentLearner();

        if (learner.getAvatarUrl() != null && !learner.getAvatarUrl().isEmpty()) {
            String avatarUrl = learner.getAvatarUrl();
            try {
                String fileName = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
                awsS3Client.deleteImage(fileName);
            } catch (Exception e) {
                log.warn("Failed to delete image from S3, proceeding to remove URL from DB", e);
            }
            learner.setAvatarUrl(null);
            learnerRepository.save(learner);
            log.info("Learner {} deleted avatar successfully", learner.getUsername());
        }
    }

    private Learner getCurrentLearner() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails ud) {
            username = ud.getUsername();
        } else {
            username = principal.toString();
        }
        return learnerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Learner not found"));
    }

    private LearnerAccountResponse mapToAccountResponse(Learner learner) {
        return LearnerAccountResponse.builder()
                .id(learner.getId())
                .username(learner.getUsername())
                .email(learner.getEmail())
                .firstName(learner.getFirstName())
                .lastName(learner.getLastName())
                .avatarUrl(learner.getAvatarUrl())
                .status(learner.getStatus() != null ? learner.getStatus().name() : null)
                .createdAt(learner.getCreatedAt())
                .build();
    }
}
