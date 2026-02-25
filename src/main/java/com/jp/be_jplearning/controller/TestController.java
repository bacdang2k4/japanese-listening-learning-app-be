package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.*;
import com.jp.be_jplearning.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Test Management", description = "Endpoints for test flow and execution")
@RequiredArgsConstructor
public class TestController {

        private final TestService testService;

        @GetMapping("/topics/{topicId}/tests")
        @Operation(summary = "Get Tests By Topic (PAGINATED)")
        public ResponseEntity<ApiResponse<PaginationResponse<TestSummaryResponse>>> getTestsByTopic(
                        @PathVariable Long topicId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                PaginationResponse<TestSummaryResponse> response = testService.getTestsByTopic(topicId, page, size);
                return ResponseEntity.ok(ApiResponse.<PaginationResponse<TestSummaryResponse>>builder()
                                .success(true)
                                .message("Tests retrieved successfully")
                                .data(response)
                                .build());
        }

        @PostMapping("/tests/{testId}/start")
        @Operation(summary = "Start Test")
        public ResponseEntity<ApiResponse<StartTestResponse>> startTest(
                        @PathVariable Long testId,
                        @Valid @RequestBody StartTestRequest request) {

                StartTestResponse response = testService.startTest(testId, request);
                return ResponseEntity.ok(ApiResponse.<StartTestResponse>builder()
                                .success(true)
                                .message("Test started successfully")
                                .data(response)
                                .build());
        }

        @PostMapping("/test-results/{resultId}/submit")
        @Operation(summary = "Submit Test")
        public ResponseEntity<ApiResponse<SubmitTestResponse>> submitTest(
                        @PathVariable Long resultId,
                        @Valid @RequestBody SubmitTestRequest request) {

                SubmitTestResponse response = testService.submitTest(resultId, request);
                return ResponseEntity.ok(ApiResponse.<SubmitTestResponse>builder()
                                .success(true)
                                .message("Test submitted successfully")
                                .data(response)
                                .build());
        }

        @GetMapping("/test-results/{resultId}")
        @Operation(summary = "Get Test Result Detail")
        public ResponseEntity<ApiResponse<TestResultDetailResponse>> getTestResultDetail(
                        @PathVariable Long resultId,
                        @RequestParam Long profileId) {

                TestResultDetailResponse response = testService.getTestResultDetail(resultId, profileId);
                return ResponseEntity.ok(ApiResponse.<TestResultDetailResponse>builder()
                                .success(true)
                                .message("Test result retrieved successfully")
                                .data(response)
                                .build());
        }

        @GetMapping("/profiles/{profileId}/test-results")
        @Operation(summary = "Get Test History By Profile (PAGINATED)")
        public ResponseEntity<ApiResponse<PaginationResponse<TestHistoryResponse>>> getTestHistoryByProfile(
                        @PathVariable Long profileId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                PaginationResponse<TestHistoryResponse> response = testService.getTestHistoryByProfile(profileId, page,
                                size);
                return ResponseEntity.ok(ApiResponse.<PaginationResponse<TestHistoryResponse>>builder()
                                .success(true)
                                .message("Test history retrieved successfully")
                                .data(response)
                                .build());
        }
}
