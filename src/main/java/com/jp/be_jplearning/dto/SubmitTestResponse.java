package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmitTestResponse {
    private Long resultId;
    private Integer score;
    private Boolean isPassed;
    private String status;
}
