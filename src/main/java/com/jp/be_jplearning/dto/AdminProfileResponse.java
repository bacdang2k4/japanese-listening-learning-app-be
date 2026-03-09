package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminProfileResponse {
    private Long profileId;
    private Long learnerId;
    private String learnerName;
    private String learnerEmail;
    private String learnerUsername;
    private String status;
    private int completedLevels;
    private int completedTopics;
    private int totalScore;
    private LocalDateTime startDate;
}
