package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LearnerQuestionResponse {
    private Long questionId;
    private String content;
    private Integer questionOrder;
    private List<LearnerAnswerOption> answers;
}
