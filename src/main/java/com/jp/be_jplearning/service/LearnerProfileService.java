package com.jp.be_jplearning.service;

import org.springframework.web.multipart.MultipartFile;

public interface LearnerProfileService {
    String uploadAvatar(MultipartFile file);

    void deleteAvatar();
}
