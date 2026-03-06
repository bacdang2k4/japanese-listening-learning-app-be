package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TopicResponse {
    private Long id;
    private String topicName;
    private Long levelId;
    private String levelName;
    private LocalDateTime createdAt;
}
