package com.jp.be_jplearning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.jp.be_jplearning.entity.enums.TestStatusEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "audiotest")
@Getter
@Setter
@NoArgsConstructor
public class AudioTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Long id;

    @Column(name = "test_name")
    private String testName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(name = "transcript", columnDefinition = "TEXT")
    private String transcript;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "pass_condition")
    private Integer passCondition = 80;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "test_status_enum")
    private TestStatusEnum status = TestStatusEnum.DRAFT;

    @Column(name = "is_ai_generated")
    private Boolean isAiGenerated = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_admin_id")
    private Admin createdByAdmin;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "test_order")
    private Integer testOrder;

    @Column(name = "plain_transcript", columnDefinition = "TEXT")
    private String plainTranscript;
}
