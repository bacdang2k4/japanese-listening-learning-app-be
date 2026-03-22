package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminTestDetailResponse {
    private Long resultId;
    private Long learnerId;
    private String learnerName;
    private String learnerEmail;
    private String testName;
    private String levelName;
    private String topicName;
    private Integer score;
    private Boolean isPassed;
    private Integer totalTime;
    private LocalDateTime completedAt;
    private List<QuestionResultResponse> questionResults;
}
