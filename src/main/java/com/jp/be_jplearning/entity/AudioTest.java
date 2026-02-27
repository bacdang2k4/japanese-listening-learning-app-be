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

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "pass_condition")
    private Integer passCondition = 80;

    @Column(name = "is_ai_generated")
    private Boolean isAiGenerated = false;

    @Column(name = "ai_prompt", columnDefinition = "TEXT")
    private String aiPrompt;

    @Column(name = "ai_model")
    private String aiModel;

    @Column(name = "ai_generated_at")
    private LocalDateTime aiGeneratedAt;

    @Column(name = "ai_version")
    private Integer aiVersion = 1;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "test_status_enum")
    private TestStatusEnum status = TestStatusEnum.DRAFT;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
