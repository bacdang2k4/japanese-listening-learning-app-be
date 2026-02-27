package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.LevelRequest;
import com.jp.be_jplearning.dto.LevelResponse;
import com.jp.be_jplearning.service.LevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Level Management", description = "Endpoints for managing levels")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

    // ==========================================
    // PUBLIC APIs
    // ==========================================

    @GetMapping("/levels")
    @Operation(summary = "Get all levels (Public)")
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

    @GetMapping("/levels/{levelId}")
    @Operation(summary = "Get level by ID (Public)")
    public ResponseEntity<ApiResponse<LevelResponse>> getLevelById(@PathVariable Long levelId) {
        LevelResponse response = levelService.getLevelById(levelId);
        return ResponseEntity.ok(ApiResponse.<LevelResponse>builder()
                .success(true)
                .message("Level retrieved successfully")
                .data(response)
                .build());
    }

    // ==========================================
    // ADMIN APIs
    // ==========================================

    @PostMapping("/admin/levels")
    @Operation(summary = "Create a new level (Admin)")
    public ResponseEntity<ApiResponse<LevelResponse>> createLevel(@Valid @RequestBody LevelRequest request) {
        LevelResponse response = levelService.createLevel(request);
        return new ResponseEntity<>(ApiResponse.<LevelResponse>builder()
                .success(true)
                .message("Level created successfully")
                .data(response)
                .build(), HttpStatus.CREATED);
    }

    @PutMapping("/admin/levels/{levelId}")
    @Operation(summary = "Update level by ID (Admin)")
    public ResponseEntity<ApiResponse<LevelResponse>> updateLevel(
            @PathVariable Long levelId,
            @Valid @RequestBody LevelRequest request) {
        LevelResponse response = levelService.updateLevel(levelId, request);
        return ResponseEntity.ok(ApiResponse.<LevelResponse>builder()
                .success(true)
                .message("Level updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/admin/levels/{levelId}")
    @Operation(summary = "Delete level by ID (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteLevel(@PathVariable Long levelId) {
        levelService.deleteLevel(levelId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Level deleted successfully")
                .data(null)
                .build());
    }
}
