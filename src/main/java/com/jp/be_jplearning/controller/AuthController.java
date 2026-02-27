package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.dto.LearnerAuthResponse;
import com.jp.be_jplearning.dto.LoginRequest;
import com.jp.be_jplearning.dto.RegisterRequest;
import com.jp.be_jplearning.dto.RegisterResponse;
import com.jp.be_jplearning.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Learner Authentication", description = "Endpoints for Learner login and registration")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register Learner", security = {})
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.registerLearner(request);
        return ResponseEntity.ok(ApiResponse.<RegisterResponse>builder()
                .success(true)
                .message("Learner registered successfully")
                .data(response)
                .build());
    }

    @PostMapping("/login")
    @Operation(summary = "Login Learner", security = {})
    public ResponseEntity<ApiResponse<LearnerAuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        LearnerAuthResponse response = authService.loginLearner(request);
        return ResponseEntity.ok(ApiResponse.<LearnerAuthResponse>builder()
                .success(true)
                .message("Learner logged in successfully")
                .data(response)
                .build());
    }
}
