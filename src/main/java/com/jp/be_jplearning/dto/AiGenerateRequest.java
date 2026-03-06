package com.jp.be_jplearning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiGenerateRequest {

    @NotNull(message = "topicId must not be null")
    private Long topicId;

    @NotBlank(message = "testName must not be blank")
    private String testName;

    private String difficulty;

    @NotNull(message = "questionCount must not be null")
    private Integer questionCount;

    @NotNull(message = "duration must not be null")
    private Integer duration;
}
