package com.jp.be_jplearning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AudioTestRequest {
    @NotBlank(message = "testName must not be blank")
    private String testName;

    @NotNull(message = "topicId must not be null")
    private Long topicId;

    private String transcript;

    private String audioUrl;

    @NotNull(message = "duration must not be null")
    private Integer duration;

    private Integer passCondition;
}
