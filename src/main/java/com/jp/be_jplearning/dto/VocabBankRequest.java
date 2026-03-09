package com.jp.be_jplearning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VocabBankRequest {
    @NotBlank(message = "must not be blank")
    private String title;

    private String description;

    @NotNull(message = "must not be null")
    private Long topicId;
}
