package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long questionId);

    void deleteByQuestionId(Long questionId);

    @Query("SELECT a FROM Answer a WHERE a.question.id IN :questionIds AND a.isCorrect = true")
    List<Answer> findCorrectAnswersByQuestionIds(@Param("questionIds") List<Long> questionIds);

    @Query("SELECT a FROM Answer a WHERE a.question.id IN :questionIds ORDER BY a.answerOrder ASC")
    List<Answer> findByQuestionIds(@Param("questionIds") List<Long> questionIds);
}
