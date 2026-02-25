package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LevelResponse {
    private Long id;
    private String levelName;
    private Long adminId;
    private String adminName;
    private LocalDateTime createdAt;
}
