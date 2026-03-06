package com.jp.be_jplearning.service;

import com.jp.be_jplearning.dto.AiGenerateRequest;
import com.jp.be_jplearning.dto.AiRejectRequest;
import com.jp.be_jplearning.dto.AiTestResponse;

public interface AiTestService {
    AiTestResponse generateTest(AiGenerateRequest request);

    AiTestResponse getGeneratedTest(Long testId);

    void approveTest(Long testId);

    void rejectTest(Long testId, AiRejectRequest request);
}
