package com.jp.be_jplearning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.jp.be_jplearning.entity.enums.GeneratedStatusEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "generatedquestion")
@Getter
@Setter
@NoArgsConstructor
public class GeneratedQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "generated_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private AudioTest test;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "generated_status_enum")
    private GeneratedStatusEnum status = GeneratedStatusEnum.PENDING;

    @Column(name = "ai_prompt", columnDefinition = "TEXT")
    private String aiPrompt;

    @Column(name = "ai_model")
    private String aiModel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
