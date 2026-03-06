package com.jp.be_jplearning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.jp.be_jplearning.entity.enums.GenerationStatusEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_generation_log")
@Getter
@Setter
@NoArgsConstructor
public class AIGenerationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "generation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id")
    private AudioTest test;

    @Column(name = "prompt", columnDefinition = "TEXT")
    private String prompt;

    @Column(name = "model")
    private String model;

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "generation_status_enum")
    private GenerationStatusEnum status;

    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;
}
