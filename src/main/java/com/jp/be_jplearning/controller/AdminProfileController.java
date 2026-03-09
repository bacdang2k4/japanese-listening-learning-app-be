package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.AdminProfileResponse;
import com.jp.be_jplearning.service.AdminProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/profiles")
@RequiredArgsConstructor
@Tag(name = "Admin Profile APIs", description = "Endpoints for viewing learner learning progress")
public class AdminProfileController {

    private final AdminProgressService adminProgressService;

    @GetMapping
    @Operation(summary = "Get all learner profiles with progress")
    public ResponseEntity<ApiResponse<PaginationResponse<AdminProfileResponse>>> getProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "startDate,desc") String sort) {

        PaginationResponse<AdminProfileResponse> response = adminProgressService.getProfiles(page, size, keyword, sort);
        return ResponseEntity.ok(ApiResponse.<PaginationResponse<AdminProfileResponse>>builder()
                .success(true)
                .message("Profiles retrieved successfully")
                .data(response)
                .build());
    }
}
