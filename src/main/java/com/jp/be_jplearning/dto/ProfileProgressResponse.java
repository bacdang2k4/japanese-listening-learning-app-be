package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ProfileProgressResponse {
    private Long profileId;
    private String profileStatus;
    private List<LevelProgressItem> levels;

    @Data
    @Builder
    public static class LevelProgressItem {
        private Long levelId;
        private String levelName;
        private Integer levelOrder;
        private String status;
        private List<TopicProgressItem> topics;
    }

    @Data
    @Builder
    public static class TopicProgressItem {
        private Long topicId;
        private String topicName;
        private String status;
        private long testCount;
        private long passedTestCount;
    }
}
