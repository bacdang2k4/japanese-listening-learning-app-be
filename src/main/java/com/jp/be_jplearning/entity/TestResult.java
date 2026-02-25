package com.jp.be_jplearning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.jp.be_jplearning.entity.enums.TestModeEnum;
import com.jp.be_jplearning.entity.enums.TestResultStatusEnum;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "profile_id", referencedColumnName = "profile_id"),
            @JoinColumn(name = "topic_id", referencedColumnName = "topic_id")
    })
    private ProfileTopic profileTopic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id")
    private AudioTest test;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "mode", columnDefinition = "test_mode_enum", nullable = false)
    private TestModeEnum mode;

    @Column(name = "score")
    private Integer score = 0;

    @Column(name = "is_passed")
    private Boolean isPassed = false;

    @Column(name = "total_time")
    private Integer totalTime;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "test_result_status_enum")
    private TestResultStatusEnum status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
