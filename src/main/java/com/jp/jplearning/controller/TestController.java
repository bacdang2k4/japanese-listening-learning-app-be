package com.jp.jplearning.controller;

import com.jp.jplearning.entity.Level;
import com.jp.jplearning.service.LevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test API", description = "Endpoints for checking system connectivity")
public class TestController {

    private final LevelService levelService;

    @Autowired
    public TestController(LevelService levelService) {
        this.levelService = levelService;
    }

    @Operation(summary = "Get all levels", description = "Fetches a list of levels from the database to verify connectivity.")
    @GetMapping
    public ResponseEntity<List<Level>> getLevels() {
        return ResponseEntity.ok(levelService.getAllLevels());
    }
}
