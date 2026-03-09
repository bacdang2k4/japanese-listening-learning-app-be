package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.VocabBankRequest;
import com.jp.be_jplearning.dto.VocabBankResponse;
import com.jp.be_jplearning.dto.VocabBankVocabularyRequest;
import com.jp.be_jplearning.service.VocabBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/vocab-banks")
@RequiredArgsConstructor
@Tag(name = "VocabBank Admin APIs", description = "Endpoints for managing vocab banks by admins")
public class AdminVocabBankController {

    private final VocabBankService vocabBankService;

    @GetMapping
    @Operation(summary = "Get a paginated list of vocab banks with filters")
    public ResponseEntity<ApiResponse<PaginationResponse<VocabBankResponse>>> getVocabBanks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        PaginationResponse<VocabBankResponse> response = vocabBankService.getVocabBanks(page, size, topicId, keyword,
                sort);
        return ResponseEntity.ok(ApiResponse.<PaginationResponse<VocabBankResponse>>builder()
                .success(true)
                .message("Vocab banks retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{vocabBankId}")
    @Operation(summary = "Get a vocab bank by its ID with vocabulary list")
    public ResponseEntity<ApiResponse<VocabBankResponse>> getVocabBankById(@PathVariable Long vocabBankId) {
        VocabBankResponse response = vocabBankService.getVocabBankById(vocabBankId);
        return ResponseEntity.ok(ApiResponse.<VocabBankResponse>builder()
                .success(true)
                .message("Vocab bank retrieved successfully")
                .data(response)
                .build());
    }

    @PostMapping
    @Operation(summary = "Create a new vocab bank")
    public ResponseEntity<ApiResponse<VocabBankResponse>> createVocabBank(
            @RequestBody @Valid VocabBankRequest request) {
        VocabBankResponse response = vocabBankService.createVocabBank(request);
        return ResponseEntity.ok(ApiResponse.<VocabBankResponse>builder()
                .success(true)
                .message("Vocab bank created successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{vocabBankId}")
    @Operation(summary = "Update an existing vocab bank")
    public ResponseEntity<ApiResponse<VocabBankResponse>> updateVocabBank(
            @PathVariable Long vocabBankId,
            @RequestBody @Valid VocabBankRequest request) {

        VocabBankResponse response = vocabBankService.updateVocabBank(vocabBankId, request);
        return ResponseEntity.ok(ApiResponse.<VocabBankResponse>builder()
                .success(true)
                .message("Vocab bank updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{vocabBankId}")
    @Operation(summary = "Delete a vocab bank")
    public ResponseEntity<ApiResponse<Void>> deleteVocabBank(@PathVariable Long vocabBankId) {
        vocabBankService.deleteVocabBank(vocabBankId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Vocab bank deleted successfully")
                .data(null)
                .build());
    }

    @PostMapping("/{vocabBankId}/vocabularies")
    @Operation(summary = "Add vocabularies to a vocab bank")
    public ResponseEntity<ApiResponse<VocabBankResponse>> addVocabulariesToBank(
            @PathVariable Long vocabBankId,
            @RequestBody @Valid List<VocabBankVocabularyRequest> requests) {

        VocabBankResponse response = vocabBankService.addVocabulariesToBank(vocabBankId, requests);
        return ResponseEntity.ok(ApiResponse.<VocabBankResponse>builder()
                .success(true)
                .message("Vocabularies added to vocab bank successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{vocabBankId}/vocabularies/{vocabId}")
    @Operation(summary = "Remove a vocabulary from a vocab bank")
    public ResponseEntity<ApiResponse<Void>> removeVocabularyFromBank(
            @PathVariable Long vocabBankId,
            @PathVariable Long vocabId) {

        vocabBankService.removeVocabularyFromBank(vocabBankId, vocabId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Vocabulary removed from vocab bank successfully")
                .data(null)
                .build());
    }

    @PutMapping("/{vocabBankId}/vocabularies/reorder")
    @Operation(summary = "Reorder vocabularies in a vocab bank")
    public ResponseEntity<ApiResponse<VocabBankResponse>> reorderVocabulariesInBank(
            @PathVariable Long vocabBankId,
            @RequestBody @Valid List<VocabBankVocabularyRequest> requests) {

        VocabBankResponse response = vocabBankService.reorderVocabulariesInBank(vocabBankId, requests);
        return ResponseEntity.ok(ApiResponse.<VocabBankResponse>builder()
                .success(true)
                .message("Vocabularies reordered successfully")
                .data(response)
                .build());
    }
}
