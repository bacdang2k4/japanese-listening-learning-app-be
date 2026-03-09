package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.LearnerResponse;

public interface LearnerService {
    PaginationResponse<LearnerResponse> getLearners(int page, int size, String keyword, String status, String sort);

    LearnerResponse getLearnerById(Long learnerId);

    LearnerResponse lockLearner(Long learnerId);

    LearnerResponse unlockLearner(Long learnerId);
}
