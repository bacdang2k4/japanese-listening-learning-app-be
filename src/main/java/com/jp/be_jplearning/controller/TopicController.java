package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.TopicResponse;
import com.jp.be_jplearning.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Tag(name = "Topic Public APIs", description = "Endpoints for fetching topics publicly")
public class TopicController {

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
}
