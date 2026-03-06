package com.jp.be_jplearning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TopicRequest {
    @NotBlank(message = "must not be blank")
    private String topicName;

    @NotNull(message = "must not be null")
    private Long levelId;
}
