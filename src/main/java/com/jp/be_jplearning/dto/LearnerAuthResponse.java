package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LearnerAuthResponse {
    private String accessToken;
    private String tokenType;
    private Long learnerId;
    private Long profileId;
    private String username;
    private String role;
}
