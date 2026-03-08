package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.dto.LevelRequest;
import com.jp.be_jplearning.dto.LevelResponse;
import com.jp.be_jplearning.service.LevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.jp.be_jplearning.common.PaginationResponse;

@RestController
@RequestMapping("/api/v1/admin/levels")
@RequiredArgsConstructor
@Tag(name = "Level Admin APIs", description = "Endpoints for managing levels by admins")
public class AdminLevelController {

    private final LevelService levelService;

    @GetMapping
    @Operation(summary = "Get all levels")
    public ResponseEntity<ApiResponse<PaginationResponse<LevelResponse>>> getAllLevels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginationResponse<LevelResponse> response = levelService.getAllLevels(page, size);
        return ResponseEntity.ok(ApiResponse.<PaginationResponse<LevelResponse>>builder()
                .success(true)
                .message("Levels retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{levelId}")
    @Operation(summary = "Get level by ID")
    public ResponseEntity<ApiResponse<LevelResponse>> getLevelById(@PathVariable Long levelId) {
        LevelResponse response = levelService.getLevelById(levelId);
        return ResponseEntity.ok(ApiResponse.<LevelResponse>builder()
                .success(true)
                .message("Level retrieved successfully")
                .data(response)
                .build());
    }

    @PostMapping
    @Operation(summary = "Create a new level")
    public ResponseEntity<ApiResponse<LevelResponse>> createLevel(@RequestBody @Valid LevelRequest request) {
        LevelResponse response = levelService.createLevel(request);
        return ResponseEntity.ok(ApiResponse.<LevelResponse>builder()
                .success(true)
                .message("Level created successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{levelId}")
    @Operation(summary = "Update an existing level")
    public ResponseEntity<ApiResponse<LevelResponse>> updateLevel(
            @PathVariable Long levelId,
            @RequestBody @Valid LevelRequest request) {

        LevelResponse response = levelService.updateLevel(levelId, request);
        return ResponseEntity.ok(ApiResponse.<LevelResponse>builder()
                .success(true)
                .message("Level updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{levelId}")
    @Operation(summary = "Delete a level manually")
    public ResponseEntity<ApiResponse<Void>> deleteLevel(@PathVariable Long levelId) {
        levelService.deleteLevel(levelId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Level deleted successfully")
                .data(null)
                .build());
    }
}
