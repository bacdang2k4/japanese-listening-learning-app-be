package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.dto.CreateProfileRequest;
import com.jp.be_jplearning.dto.ProfileProgressResponse;
import com.jp.be_jplearning.dto.ProfileResponse;
import com.jp.be_jplearning.service.LearnerProfileService;
import com.jp.be_jplearning.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/learners/me")
@RequiredArgsConstructor
@Tag(name = "Learner Profile API", description = "Endpoints for learners to manage their own profiles")
public class LearnerProfileController {

    private final LearnerProfileService learnerProfileService;
    private final ProfileService profileService;

    @GetMapping("/profiles")
    @PreAuthorize("hasRole('LEARNER')")
    @Operation(summary = "Get My Profiles", description = "Get all profiles of the current learner.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<ProfileResponse>>> getMyProfiles() {
        List<ProfileResponse> profiles = profileService.getMyProfiles();
        return ResponseEntity.ok(ApiResponse.<List<ProfileResponse>>builder()
                .success(true)
                .message("Profiles retrieved successfully")
                .data(profiles)
                .build());
    }

    @PostMapping("/profiles")
    @PreAuthorize("hasRole('LEARNER')")
    @Operation(summary = "Create Profile", description = "Create a new learning profile with a starting level.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ProfileResponse>> createProfile(
            @Valid @RequestBody CreateProfileRequest request) {
        ProfileResponse profile = profileService.createProfile(request);
        return ResponseEntity.ok(ApiResponse.<ProfileResponse>builder()
                .success(true)
                .message("Profile created successfully")
                .data(profile)
                .build());
    }

    @GetMapping("/profiles/{profileId}/progress")
    @PreAuthorize("hasRole('LEARNER')")
    @Operation(summary = "Get Profile Progress", description = "Get level and topic progress for a profile.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ProfileProgressResponse>> getProfileProgress(@PathVariable Long profileId) {
        ProfileProgressResponse progress = profileService.getProfileProgress(profileId);
        return ResponseEntity.ok(ApiResponse.<ProfileProgressResponse>builder()
                .success(true)
                .message("Profile progress retrieved successfully")
                .data(progress)
                .build());
    }

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
