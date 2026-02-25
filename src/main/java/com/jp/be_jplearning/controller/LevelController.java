package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
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

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Level Management", description = "Endpoints for managing levels")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

    @PostMapping("/levels")
    @Operation(summary = "Create a new level")
    public ResponseEntity<ApiResponse<LevelResponse>> createLevel(@Valid @RequestBody LevelRequest request) {
        LevelResponse response = levelService.createLevel(request);
        return new ResponseEntity<>(ApiResponse.<LevelResponse>builder()
                .success(true)
                .message("Level created successfully")
                .data(response)
                .build(), HttpStatus.CREATED);
    }

    @GetMapping("/levels")
    @Operation(summary = "Get all levels")
    public ResponseEntity<ApiResponse<List<LevelResponse>>> getAllLevels() {
        List<LevelResponse> response = levelService.getAllLevels();
        return ResponseEntity.ok(ApiResponse.<List<LevelResponse>>builder()
                .success(true)
                .message("Levels retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/levels/{levelId}")
    @Operation(summary = "Get level by ID")
    public ResponseEntity<ApiResponse<LevelResponse>> getLevelById(@PathVariable Long levelId) {
        LevelResponse response = levelService.getLevelById(levelId);
        return ResponseEntity.ok(ApiResponse.<LevelResponse>builder()
                .success(true)
                .message("Level retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/levels/{levelId}")
    @Operation(summary = "Update level by ID")
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

    @DeleteMapping("/levels/{levelId}")
    @Operation(summary = "Delete level by ID")
    public ResponseEntity<ApiResponse<Void>> deleteLevel(@PathVariable Long levelId) {
        levelService.deleteLevel(levelId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Level deleted successfully")
                .data(null)
                .build());
    }

    @GetMapping("/admins/{adminId}/levels")
    @Operation(summary = "Get all levels by Admin ID")
    public ResponseEntity<ApiResponse<List<LevelResponse>>> getLevelsByAdmin(@PathVariable Long adminId) {
        List<LevelResponse> response = levelService.getLevelsByAdmin(adminId);
        return ResponseEntity.ok(ApiResponse.<List<LevelResponse>>builder()
                .success(true)
                .message("Levels retrieved successfully for admin")
                .data(response)
                .build());
    }
}
