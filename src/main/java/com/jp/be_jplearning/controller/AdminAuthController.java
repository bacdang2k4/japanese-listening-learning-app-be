package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.dto.AdminAuthResponse;
import com.jp.be_jplearning.dto.LoginRequest;
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
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
@Tag(name = "Admin Authentication", description = "Endpoints for Admin login")
public class AdminAuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login Admin", security = {})
    public ResponseEntity<ApiResponse<AdminAuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AdminAuthResponse response = authService.loginAdmin(request);
        return ResponseEntity.ok(ApiResponse.<AdminAuthResponse>builder()
                .success(true)
                .message("Admin logged in successfully")
                .data(response)
                .build());
    }
}
