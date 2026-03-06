package com.jp.be_jplearning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerRequest {
    @NotBlank(message = "Answer content must not be blank")
    private String content;

    @NotNull(message = "isCorrect flag must be specified")
    private Boolean isCorrect;
}
