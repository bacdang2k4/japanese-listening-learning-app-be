package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StartTestResponse {
    private Long resultId;
    private Long profileId;
    private Long testId;
    private String testName;
    private String audioUrl;
    private Integer duration;
    private Integer passCondition;
    private Integer totalQuestions;
    private String mode;
    private String status;
    private LocalDateTime startedAt;
}
