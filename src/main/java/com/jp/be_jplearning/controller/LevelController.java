package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.LevelResponse;
import com.jp.be_jplearning.service.LevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Level Public APIs", description = "Endpoints for fetching levels publicly")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

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
}
