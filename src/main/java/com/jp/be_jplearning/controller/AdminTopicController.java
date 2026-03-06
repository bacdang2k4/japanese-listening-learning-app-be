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

@RestController
@RequestMapping("/api/admin/topics")
@RequiredArgsConstructor
@Tag(name = "Topic Admin APIs", description = "Endpoints for managing topics by admins")
public class AdminTopicController {

    private final TopicService topicService;

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
