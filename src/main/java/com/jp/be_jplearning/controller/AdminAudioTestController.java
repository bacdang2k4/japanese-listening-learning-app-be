package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.dto.AudioTestRequest;
import com.jp.be_jplearning.dto.AudioTestResponse;
import com.jp.be_jplearning.service.AudioTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.entity.enums.TestStatusEnum;

@RestController
@RequestMapping("/api/admin/audio-tests")
@RequiredArgsConstructor
@Tag(name = "Audio Test Admin APIs", description = "Endpoints for managing audio tests by admins")
public class AdminAudioTestController {

    private final AudioTestService audioTestService;

    @GetMapping
    @Operation(summary = "Get a paginated list of audio tests with filters")
    public ResponseEntity<ApiResponse<PaginationResponse<AudioTestResponse>>> getAudioTests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) TestStatusEnum status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        PaginationResponse<AudioTestResponse> response = audioTestService.getAudioTests(page, size, topicId, status,
                keyword, sort);
        return ResponseEntity.ok(ApiResponse.<PaginationResponse<AudioTestResponse>>builder()
                .success(true)
                .message("Audio tests retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{testId}")
    @Operation(summary = "Get an audio test by its ID")
    public ResponseEntity<ApiResponse<AudioTestResponse>> getAudioTestById(@PathVariable Long testId) {
        AudioTestResponse response = audioTestService.getAudioTestById(testId);
        return ResponseEntity.ok(ApiResponse.<AudioTestResponse>builder()
                .success(true)
                .message("Audio test retrieved successfully")
                .data(response)
                .build());
    }

    @PostMapping
    @Operation(summary = "Create a new audio test")
    public ResponseEntity<ApiResponse<AudioTestResponse>> createAudioTest(
            @RequestBody @Valid AudioTestRequest request) {
        AudioTestResponse response = audioTestService.createAudioTest(request);
        return ResponseEntity.ok(ApiResponse.<AudioTestResponse>builder()
                .success(true)
                .message("Audio test created successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{testId}")
    @Operation(summary = "Update an existing audio test")
    public ResponseEntity<ApiResponse<AudioTestResponse>> updateAudioTest(
            @PathVariable Long testId,
            @RequestBody @Valid AudioTestRequest request) {

        AudioTestResponse response = audioTestService.updateAudioTest(testId, request);
        return ResponseEntity.ok(ApiResponse.<AudioTestResponse>builder()
                .success(true)
                .message("Audio test updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{testId}")
    @Operation(summary = "Delete an audio test manually")
    public ResponseEntity<ApiResponse<Void>> deleteAudioTest(@PathVariable Long testId) {
        audioTestService.deleteAudioTest(testId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Audio test deleted successfully")
                .data(null)
                .build());
    }

    @PatchMapping("/{testId}/publish")
    @Operation(summary = "Publish an audio test")
    public ResponseEntity<ApiResponse<Void>> publishAudioTest(@PathVariable Long testId) {
        audioTestService.publishAudioTest(testId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Audio test published successfully")
                .data(null)
                .build());
    }

    @PatchMapping("/{testId}/reject")
    @Operation(summary = "Reject an audio test")
    public ResponseEntity<ApiResponse<Void>> rejectAudioTest(@PathVariable Long testId) {
        audioTestService.rejectAudioTest(testId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Audio test rejected successfully")
                .data(null)
                .build());
    }
}
