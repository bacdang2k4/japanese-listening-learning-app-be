package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AiTestResponse {
    private Long testId;
    private String testName;
    private String transcript;
    private String plainTranscript;
    private Long topicId;
    private String status;
    private String audioUrl;
    private List<QuestionResponse> questions;
}
