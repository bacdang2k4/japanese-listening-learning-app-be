package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.BusinessException;
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

    @Override
    @Transactional
    public String uploadAvatar(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("Avatar file cannot be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("Only image files are allowed");
        }

        // Get authenticated user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        Learner learner = learnerRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Learner not found"));

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

            log.info("Learner {} updated avatar successfully", username);
            return avatarUrl;

        } catch (IOException e) {
            log.error("Failed to read avatar file for upload", e);
            throw new BusinessException("Failed to process image file");
        }
    }

    @Override
    @Transactional
    public void deleteAvatar() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        Learner learner = learnerRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Learner not found"));

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
            log.info("Learner {} deleted avatar successfully", username);
        }
    }
}
