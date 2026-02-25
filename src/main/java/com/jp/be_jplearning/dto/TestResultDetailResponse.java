package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TestResultDetailResponse {
    private Long resultId;
    private String testName;
    private Integer score;
    private Boolean isPassed;
    private Integer totalTime;
    private List<QuestionResultResponse> questionResults;
}
