package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private Long learnerId;
    private String username;
    private String email;
}
