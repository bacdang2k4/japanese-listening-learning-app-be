package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTestId(Long testId);

    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.test WHERE q.test.id = :testId ORDER BY q.questionOrder ASC")
    List<Question> findByTestIdOrderByQuestionOrder(@Param("testId") Long testId);
}
