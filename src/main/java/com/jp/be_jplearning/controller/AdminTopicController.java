package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.dto.TopicRequest;
import com.jp.be_jplearning.dto.TopicResponse;
import com.jp.be_jplearning.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.jp.be_jplearning.common.PaginationResponse;

@RestController
@RequestMapping("/api/v1/admin/topics")
@RequiredArgsConstructor
@Tag(name = "Topic Admin APIs", description = "Endpoints for managing topics by admins")
public class AdminTopicController {

    private final TopicService topicService;

    @GetMapping
    @Operation(summary = "Get a paginated list of topics with filters")
    public ResponseEntity<ApiResponse<PaginationResponse<TopicResponse>>> getTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        PaginationResponse<TopicResponse> response = topicService.getTopics(page, size, levelId, keyword, sort);
        return ResponseEntity.ok(ApiResponse.<PaginationResponse<TopicResponse>>builder()
                .success(true)
                .message("Topics retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{topicId}")
    @Operation(summary = "Get a topic by its ID")
    public ResponseEntity<ApiResponse<TopicResponse>> getTopicById(@PathVariable Long topicId) {
        TopicResponse response = topicService.getTopicById(topicId);
        return ResponseEntity.ok(ApiResponse.<TopicResponse>builder()
                .success(true)
                .message("Topic retrieved successfully")
                .data(response)
                .build());
    }

    @PostMapping
    @Operation(summary = "Create a new topic")
    public ResponseEntity<ApiResponse<TopicResponse>> createTopic(@RequestBody @Valid TopicRequest request) {
        TopicResponse response = topicService.createTopic(request);
        return ResponseEntity.ok(ApiResponse.<TopicResponse>builder()
                .success(true)
                .message("Topic created successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{topicId}")
    @Operation(summary = "Update an existing topic")
    public ResponseEntity<ApiResponse<TopicResponse>> updateTopic(
            @PathVariable Long topicId,
            @RequestBody @Valid TopicRequest request) {

        TopicResponse response = topicService.updateTopic(topicId, request);
        return ResponseEntity.ok(ApiResponse.<TopicResponse>builder()
                .success(true)
                .message("Topic updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{topicId}")
    @Operation(summary = "Delete a topic manually")
    public ResponseEntity<ApiResponse<Void>> deleteTopic(@PathVariable Long topicId) {
        topicService.deleteTopic(topicId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Topic deleted successfully")
                .data(null)
                .build());
    }
}
