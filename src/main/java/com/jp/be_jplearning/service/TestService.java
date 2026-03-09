package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.*;

import java.util.List;

public interface TestService {
    PaginationResponse<TestSummaryResponse> getTestsByTopic(Long topicId, int page, int size);

    StartTestResponse startTest(Long testId, StartTestRequest request);

    List<LearnerQuestionResponse> getTestQuestions(Long testId, Long attemptId);

    SubmitTestResponse submitTest(Long resultId, SubmitTestRequest request);

    TestResultDetailResponse getTestResultDetail(Long resultId, Long profileId);

    PaginationResponse<TestHistoryResponse> getTestHistoryByProfile(Long profileId, int page, int size);
}
