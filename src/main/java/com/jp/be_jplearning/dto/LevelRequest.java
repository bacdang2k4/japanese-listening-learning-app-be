package com.jp.be_jplearning.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LevelRequest {
    @NotBlank(message = "must not be blank")
    private String levelName;
}
