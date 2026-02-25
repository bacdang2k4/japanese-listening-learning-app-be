package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.*;

public interface TestService {
    PaginationResponse<TestSummaryResponse> getTestsByTopic(Long topicId, int page, int size);

    StartTestResponse startTest(Long testId, StartTestRequest request);

    SubmitTestResponse submitTest(Long resultId, SubmitTestRequest request);

    TestResultDetailResponse getTestResultDetail(Long resultId);

    PaginationResponse<TestHistoryResponse> getTestHistoryByProfile(Long profileId, int page, int size);
}
