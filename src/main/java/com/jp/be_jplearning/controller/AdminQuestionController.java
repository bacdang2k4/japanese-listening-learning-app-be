package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.dto.QuestionRequest;
import com.jp.be_jplearning.dto.QuestionResponse;
import com.jp.be_jplearning.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
@Tag(name = "Question Admin APIs", description = "Endpoints for managing questions by admins")
public class AdminQuestionController {

    private final QuestionService questionService;

    @PostMapping
    @Operation(summary = "Create a new question with answers")
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(
            @RequestBody @Valid QuestionRequest request) {
        QuestionResponse response = questionService.createQuestion(request);
        return ResponseEntity.ok(ApiResponse.<QuestionResponse>builder()
                .success(true)
                .message("Question created successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{questionId}")
    @Operation(summary = "Update an existing question and its answers")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody @Valid QuestionRequest request) {
        QuestionResponse response = questionService.updateQuestion(questionId, request);
        return ResponseEntity.ok(ApiResponse.<QuestionResponse>builder()
                .success(true)
                .message("Question updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{questionId}")
    @Operation(summary = "Delete a question and its answers")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Question deleted successfully")
                .data(null)
                .build());
    }

    @GetMapping("/test/{testId}")
    @Operation(summary = "Get all questions for a specific audio test")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getQuestionsByTestId(@PathVariable Long testId) {
        List<QuestionResponse> response = questionService.getQuestionsByTestId(testId);
        return ResponseEntity.ok(ApiResponse.<List<QuestionResponse>>builder()
                .success(true)
                .message("Questions retrieved successfully")
                .data(response)
                .build());
    }
}
