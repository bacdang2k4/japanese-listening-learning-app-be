package com.jp.be_jplearning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "testresult")
@Getter
@Setter
@NoArgsConstructor
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", unique = true)
    private TestAttempt attempt;

    @Column(name = "score")
    private Integer score;

    @Column(name = "correct_answers")
    private Integer correctAnswers;

    @Column(name = "is_passed")
    private Boolean isPassed;

    @Column(name = "total_time")
    private Integer totalTime;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
