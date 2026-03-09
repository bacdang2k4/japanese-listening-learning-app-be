package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.VocabularyRequest;
import com.jp.be_jplearning.dto.VocabularyResponse;
import com.jp.be_jplearning.service.VocabularyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/vocabularies")
@RequiredArgsConstructor
@Tag(name = "Vocabulary Admin APIs", description = "Endpoints for managing vocabularies by admins")
public class AdminVocabularyController {

    private final VocabularyService vocabularyService;

    @GetMapping
    @Operation(summary = "Get a paginated list of vocabularies with keyword filter")
    public ResponseEntity<ApiResponse<PaginationResponse<VocabularyResponse>>> getVocabularies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        PaginationResponse<VocabularyResponse> response = vocabularyService.getVocabularies(page, size, keyword, sort);
        return ResponseEntity.ok(ApiResponse.<PaginationResponse<VocabularyResponse>>builder()
                .success(true)
                .message("Vocabularies retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{vocabId}")
    @Operation(summary = "Get a vocabulary by its ID")
    public ResponseEntity<ApiResponse<VocabularyResponse>> getVocabularyById(@PathVariable Long vocabId) {
        VocabularyResponse response = vocabularyService.getVocabularyById(vocabId);
        return ResponseEntity.ok(ApiResponse.<VocabularyResponse>builder()
                .success(true)
                .message("Vocabulary retrieved successfully")
                .data(response)
                .build());
    }

    @PostMapping
    @Operation(summary = "Create a new vocabulary")
    public ResponseEntity<ApiResponse<VocabularyResponse>> createVocabulary(
            @RequestBody @Valid VocabularyRequest request) {
        VocabularyResponse response = vocabularyService.createVocabulary(request);
        return ResponseEntity.ok(ApiResponse.<VocabularyResponse>builder()
                .success(true)
                .message("Vocabulary created successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{vocabId}")
    @Operation(summary = "Update an existing vocabulary")
    public ResponseEntity<ApiResponse<VocabularyResponse>> updateVocabulary(
            @PathVariable Long vocabId,
            @RequestBody @Valid VocabularyRequest request) {

        VocabularyResponse response = vocabularyService.updateVocabulary(vocabId, request);
        return ResponseEntity.ok(ApiResponse.<VocabularyResponse>builder()
                .success(true)
                .message("Vocabulary updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{vocabId}")
    @Operation(summary = "Delete a vocabulary")
    public ResponseEntity<ApiResponse<Void>> deleteVocabulary(@PathVariable Long vocabId) {
        vocabularyService.deleteVocabulary(vocabId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Vocabulary deleted successfully")
                .data(null)
                .build());
    }
}
