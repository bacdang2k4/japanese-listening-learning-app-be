package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.AdminTestDetailResponse;
import com.jp.be_jplearning.dto.AdminTestResultResponse;
import com.jp.be_jplearning.service.AdminProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/test-results")
@RequiredArgsConstructor
@Tag(name = "Admin Test Result APIs", description = "Endpoints for viewing all test results")
public class AdminTestResultController {

    private final AdminProgressService adminProgressService;

    @GetMapping
    @Operation(summary = "Get all test results with filters")
    public ResponseEntity<ApiResponse<PaginationResponse<AdminTestResultResponse>>> getTestResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) Boolean passed,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        PaginationResponse<AdminTestResultResponse> response = adminProgressService.getTestResults(
                page, size, keyword, mode, passed, sort);
        return ResponseEntity.ok(ApiResponse.<PaginationResponse<AdminTestResultResponse>>builder()
                .success(true)
                .message("Test results retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{resultId}")
    @Operation(summary = "Get test result detail by ID with question breakdown")
    public ResponseEntity<ApiResponse<AdminTestDetailResponse>> getTestResultDetail(
            @PathVariable Long resultId) {

        AdminTestDetailResponse response = adminProgressService.getTestResultDetail(resultId);
        return ResponseEntity.ok(ApiResponse.<AdminTestDetailResponse>builder()
                .success(true)
                .message("Test result detail retrieved successfully")
                .data(response)
                .build());
    }
}
