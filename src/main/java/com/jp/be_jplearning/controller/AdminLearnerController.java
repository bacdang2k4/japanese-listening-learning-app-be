package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.LearnerResponse;
import com.jp.be_jplearning.service.LearnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/learners")
@RequiredArgsConstructor
@Tag(name = "Learner Admin APIs", description = "Endpoints for managing learners by admins")
public class AdminLearnerController {

    private final LearnerService learnerService;

    @GetMapping
    @Operation(summary = "Get a paginated list of learners with filters")
    public ResponseEntity<ApiResponse<PaginationResponse<LearnerResponse>>> getLearners(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        PaginationResponse<LearnerResponse> response = learnerService.getLearners(page, size, keyword, status, sort);
        return ResponseEntity.ok(ApiResponse.<PaginationResponse<LearnerResponse>>builder()
                .success(true)
                .message("Learners retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{learnerId}")
    @Operation(summary = "Get a learner by ID")
    public ResponseEntity<ApiResponse<LearnerResponse>> getLearnerById(@PathVariable Long learnerId) {
        LearnerResponse response = learnerService.getLearnerById(learnerId);
        return ResponseEntity.ok(ApiResponse.<LearnerResponse>builder()
                .success(true)
                .message("Learner retrieved successfully")
                .data(response)
                .build());
    }

    @PatchMapping("/{learnerId}/lock")
    @Operation(summary = "Lock a learner account")
    public ResponseEntity<ApiResponse<LearnerResponse>> lockLearner(@PathVariable Long learnerId) {
        LearnerResponse response = learnerService.lockLearner(learnerId);
        return ResponseEntity.ok(ApiResponse.<LearnerResponse>builder()
                .success(true)
                .message("Learner account locked successfully")
                .data(response)
                .build());
    }

    @PatchMapping("/{learnerId}/unlock")
    @Operation(summary = "Unlock a learner account")
    public ResponseEntity<ApiResponse<LearnerResponse>> unlockLearner(@PathVariable Long learnerId) {
        LearnerResponse response = learnerService.unlockLearner(learnerId);
        return ResponseEntity.ok(ApiResponse.<LearnerResponse>builder()
                .success(true)
                .message("Learner account unlocked successfully")
                .data(response)
                .build());
    }
}
