package com.jp.be_jplearning.common;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    @Builder.Default
    private String timestamp = Instant.now().toString();
}
