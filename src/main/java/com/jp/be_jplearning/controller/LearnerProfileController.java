package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.service.LearnerProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/learners/me")
@RequiredArgsConstructor
@Tag(name = "Learner Profile API", description = "Endpoints for learners to manage their own profiles")
public class LearnerProfileController {

    private final LearnerProfileService learnerProfileService;

    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('LEARNER')")
    @Operation(summary = "Upload Avatar", description = "Upload or update the learner's avatar image.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String avatarUrl = learnerProfileService.uploadAvatar(file);
        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                .success(true)
                .message("Avatar uploaded successfully")
                .data(Map.of("avatarUrl", avatarUrl))
                .build());
    }

    @DeleteMapping(value = "/avatar")
    @PreAuthorize("hasRole('LEARNER')")
    @Operation(summary = "Delete Avatar", description = "Delete the learner's avatar image.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> deleteAvatar() {
        learnerProfileService.deleteAvatar();
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Avatar deleted successfully")
                .build());
    }
}
