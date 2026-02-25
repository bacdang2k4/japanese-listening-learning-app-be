package com.jp.be_jplearning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartTestRequest {
    @NotNull(message = "Profile ID is required")
    private Long profileId;

    @NotBlank(message = "Mode is required")
    private String mode;
}
