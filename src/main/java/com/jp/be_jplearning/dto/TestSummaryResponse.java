package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestSummaryResponse {
    private Long testId;
    private String testName;
    private Integer duration;
    private Integer passCondition;
}
