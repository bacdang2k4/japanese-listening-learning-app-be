package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.AdminProfileResponse;
import com.jp.be_jplearning.dto.AdminTestDetailResponse;
import com.jp.be_jplearning.dto.AdminTestResultResponse;

public interface AdminProgressService {
    PaginationResponse<AdminProfileResponse> getProfiles(int page, int size, String keyword, String sort);

    PaginationResponse<AdminTestResultResponse> getTestResults(int page, int size, String keyword,
            String mode, Boolean passed, String sort);

    AdminTestDetailResponse getTestResultDetail(Long resultId);
}
