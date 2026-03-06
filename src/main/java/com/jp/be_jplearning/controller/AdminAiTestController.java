package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.dto.AiGenerateRequest;
import com.jp.be_jplearning.dto.AiRejectRequest;
import com.jp.be_jplearning.dto.AiTestResponse;
import com.jp.be_jplearning.service.AiTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/ai-tests")
@RequiredArgsConstructor
@Tag(name = "AI Test Management Admin APIs", description = "Endpoints for generating and publishing AI-driven audio tests")
public class AdminAiTestController {

    private final AiTestService aiTestService;

    @PostMapping("/generate")
    @Operation(summary = "Generate a new AI audio test")
    public ResponseEntity<ApiResponse<AiTestResponse>> generateTest(@RequestBody @Valid AiGenerateRequest request) {
        AiTestResponse response = aiTestService.generateTest(request);
        return ResponseEntity.ok(ApiResponse.<AiTestResponse>builder()
                .success(true)
                .message("AI test generated successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{testId}")
    @Operation(summary = "Get a generated AI audio test details")
    public ResponseEntity<ApiResponse<AiTestResponse>> getGeneratedTest(@PathVariable Long testId) {
        AiTestResponse response = aiTestService.getGeneratedTest(testId);
        return ResponseEntity.ok(ApiResponse.<AiTestResponse>builder()
                .success(true)
                .message("AI test retrieved successfully")
                .data(response)
                .build());
    }

    @PostMapping("/{testId}/approve")
    @Operation(summary = "Approve and publish an AI audio test")
    public ResponseEntity<ApiResponse<Void>> approveTest(@PathVariable Long testId) {
        aiTestService.approveTest(testId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("AI test approved and published successfully")
                .data(null)
                .build());
    }

    @PostMapping("/{testId}/reject")
    @Operation(summary = "Reject an AI audio test")
    public ResponseEntity<ApiResponse<Void>> rejectTest(
            @PathVariable Long testId,
            @RequestBody @Valid AiRejectRequest request) {

        aiTestService.rejectTest(testId, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("AI test rejected successfully")
                .data(null)
                .build());
    }
}
