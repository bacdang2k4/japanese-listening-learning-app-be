package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TestHistoryResponse {
    private Long resultId;
    private String testName;
    private Integer score;
    private Boolean isPassed;
    private LocalDateTime createdAt;
}
