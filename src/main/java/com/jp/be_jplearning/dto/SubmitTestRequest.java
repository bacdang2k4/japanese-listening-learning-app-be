package com.jp.be_jplearning.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SubmitTestRequest {
    @NotNull(message = "Answers list cannot be null")
    @Valid
    private List<LearnerAnswerRequest> answers;
}
