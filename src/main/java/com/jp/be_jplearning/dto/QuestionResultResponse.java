package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionResultResponse {
    private Long questionId;
    private String questionContent;
    private String selectedAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
}
