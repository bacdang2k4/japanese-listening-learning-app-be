package com.jp.be_jplearning.dto;

import lombok.Data;

@Data
public class LearnerAnswerRequest {
    private Long questionId;
    private Long selectedAnswerId; // Optional because learner might not answer
}
