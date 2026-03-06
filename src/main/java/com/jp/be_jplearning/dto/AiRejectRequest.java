package com.jp.be_jplearning.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class AiRejectRequest {
    @NotBlank(message = "comment must not be blank")
    private String comment;
}
