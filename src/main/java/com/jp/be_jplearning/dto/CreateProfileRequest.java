package com.jp.be_jplearning.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateProfileRequest {
    private String name;

    @NotNull(message = "Level ID is required")
    private Long levelId;
}
