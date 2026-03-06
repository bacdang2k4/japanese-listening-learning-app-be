package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AudioTestResponse {
    private Long testId;
    private String testName;
    private Long topicId;
    private String topicName;
    private String transcript;
    private String audioUrl;
    private Integer duration;
    private Integer passCondition;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
