package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LearnerAnswerOption {
    private Long answerId;
    private String content;
    private Integer answerOrder;
    private Boolean isCorrect;
}
