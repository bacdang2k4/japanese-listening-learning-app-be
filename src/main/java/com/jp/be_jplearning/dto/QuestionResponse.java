package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionResponse {
    private Long questionId;
    private String content;
    private Long testId;
    private List<AnswerResponse> answers;
}
