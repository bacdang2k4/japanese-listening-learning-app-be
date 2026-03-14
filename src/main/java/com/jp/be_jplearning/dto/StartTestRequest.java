package com.jp.be_jplearning.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartTestRequest {
    @NotNull(message = "Profile ID is required")
    private Long profileId;
}

