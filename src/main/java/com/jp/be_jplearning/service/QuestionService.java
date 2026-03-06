package com.jp.be_jplearning.service;

import com.jp.be_jplearning.dto.QuestionRequest;
import com.jp.be_jplearning.dto.QuestionResponse;

import java.util.List;

public interface QuestionService {
    QuestionResponse createQuestion(QuestionRequest request);

    QuestionResponse updateQuestion(Long questionId, QuestionRequest request);

    void deleteQuestion(Long questionId);

    List<QuestionResponse> getQuestionsByTestId(Long testId);
}
