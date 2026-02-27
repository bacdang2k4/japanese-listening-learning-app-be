package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminAuthResponse {
    private String accessToken;
    private String tokenType;
    private Long adminId;
    private String username;
    private String role;
}
