package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long questionId);

    void deleteByQuestionId(Long questionId);
}
