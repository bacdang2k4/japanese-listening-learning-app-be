package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.AiGenerateRequest;
import com.jp.be_jplearning.dto.AiGenerationLogResponse;
import com.jp.be_jplearning.dto.AiRejectRequest;
import com.jp.be_jplearning.dto.AiTestResponse;
import com.jp.be_jplearning.entity.AIGenerationLog;
import com.jp.be_jplearning.repository.AIGenerationLogRepository;
import com.jp.be_jplearning.service.AiTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/ai-tests")
@RequiredArgsConstructor
@Tag(name = "AI Test Management Admin APIs", description = "Endpoints for generating and publishing AI-driven audio tests")
public class AdminAiTestController {

    private final AiTestService aiTestService;
    private final AIGenerationLogRepository aiGenerationLogRepository;

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

    @GetMapping("/logs")
    @Operation(summary = "Get recent AI generation logs")
    public ResponseEntity<ApiResponse<PaginationResponse<AiGenerationLogResponse>>> getGenerationLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AIGenerationLog> logPage = aiGenerationLogRepository
                .findAllByOrderByGeneratedAtDesc(PageRequest.of(page, size));

        List<AiGenerationLogResponse> content = logPage.getContent().stream()
                .map(log -> AiGenerationLogResponse.builder()
                        .id(log.getId())
                        .testId(log.getTest() != null ? log.getTest().getId() : null)
                        .testName(log.getTest() != null ? log.getTest().getTestName() : null)
                        .model(log.getModel())
                        .status(log.getStatus() != null ? log.getStatus().name() : null)
                        .generatedAt(log.getGeneratedAt())
                        .build())
                .toList();

        PaginationResponse<AiGenerationLogResponse> response = new PaginationResponse<>(
                content, logPage.getNumber(), logPage.getSize(),
                logPage.getTotalElements(), logPage.getTotalPages(), logPage.isLast());

        return ResponseEntity.ok(ApiResponse.<PaginationResponse<AiGenerationLogResponse>>builder()
                .success(true)
                .message("AI generation logs retrieved successfully")
                .data(response)
                .build());
    }
}
