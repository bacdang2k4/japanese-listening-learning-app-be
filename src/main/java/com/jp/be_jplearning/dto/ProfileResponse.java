package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ProfileResponse {
    private Long profileId;
    private String status;
    private LocalDateTime startDate;
    private String currentLevelName;
    private Long currentLevelId;
}
