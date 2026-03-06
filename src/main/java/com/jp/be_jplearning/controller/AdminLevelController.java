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

@RestController
@RequestMapping("/api/admin/levels")
@RequiredArgsConstructor
@Tag(name = "Level Admin APIs", description = "Endpoints for managing levels by admins")
public class AdminLevelController {

    private final LevelService levelService;

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
