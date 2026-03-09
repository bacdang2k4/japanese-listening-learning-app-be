package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AiGenerationLogResponse {
    private Long id;
    private Long testId;
    private String testName;
    private String model;
    private String status;
    private LocalDateTime generatedAt;
}
