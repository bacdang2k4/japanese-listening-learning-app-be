package com.jp.be_jplearning.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class QuestionRequest {
    @NotBlank(message = "Question content must not be blank")
    private String content;

    @NotNull(message = "testId must not be null")
    private Long testId;

    @NotNull(message = "List of answers must not be null")
    @Size(min = 2, message = "A question must have at least 2 answers")
    @Valid
    private List<AnswerRequest> answers;
}
