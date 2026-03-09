package com.jp.be_jplearning.service;

import com.jp.be_jplearning.dto.LearnerAccountResponse;
import com.jp.be_jplearning.dto.UpdateLearnerInfoRequest;
import org.springframework.web.multipart.MultipartFile;

public interface LearnerProfileService {
    LearnerAccountResponse getMyAccount();

    LearnerAccountResponse updateMyInfo(UpdateLearnerInfoRequest request);

    String uploadAvatar(MultipartFile file);

    void deleteAvatar();
}
