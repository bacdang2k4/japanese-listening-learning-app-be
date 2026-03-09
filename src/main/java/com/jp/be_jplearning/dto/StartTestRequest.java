package com.jp.be_jplearning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class StartTestRequest {
    @NotNull(message = "Profile ID is required")
    private Long profileId;

    @NotBlank(message = "Mode is required")
    @Pattern(regexp = "(?i)PRACTICE|EXAM", message = "Mode must be PRACTICE or EXAM")
    private String mode;
}

