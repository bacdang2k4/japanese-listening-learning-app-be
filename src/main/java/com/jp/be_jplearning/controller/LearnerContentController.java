package com.jp.be_jplearning.controller;

import com.jp.be_jplearning.common.ApiResponse;
import com.jp.be_jplearning.dto.LevelResponse;
import com.jp.be_jplearning.dto.TopicResponse;
import com.jp.be_jplearning.dto.VocabularyResponse;
import com.jp.be_jplearning.entity.Level;
import com.jp.be_jplearning.entity.Topic;
import com.jp.be_jplearning.entity.VocabBank;
import com.jp.be_jplearning.entity.VocabBankVocabulary;
import com.jp.be_jplearning.repository.LevelRepository;
import com.jp.be_jplearning.repository.TopicRepository;
import com.jp.be_jplearning.repository.VocabBankRepository;
import com.jp.be_jplearning.repository.VocabBankVocabularyRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Learner Content", description = "Learner-facing endpoints for levels, topics, and vocabularies")
@RequiredArgsConstructor
public class LearnerContentController {

    private final LevelRepository levelRepository;
    private final TopicRepository topicRepository;
    private final VocabBankRepository vocabBankRepository;
    private final VocabBankVocabularyRepository vocabBankVocabularyRepository;

    @GetMapping("/levels")
    @Operation(summary = "Get All Levels (ordered)")
    public ResponseEntity<ApiResponse<List<LevelResponse>>> getLevels() {
        List<Level> levels = levelRepository.findAllByOrderByLevelOrderAsc();
        List<LevelResponse> response = levels.stream()
                .map(l -> LevelResponse.builder()
                        .id(l.getId())
                        .levelName(l.getLevelName())
                        .adminId(l.getAdmin() != null ? l.getAdmin().getId() : null)
                        .adminName(l.getAdmin() != null ? l.getAdmin().getUsername() : null)
                        .createdAt(l.getCreatedAt())
                        .build())
                .toList();
        return ResponseEntity.ok(ApiResponse.<List<LevelResponse>>builder()
                .success(true)
                .message("Levels retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/levels/{levelId}/topics")
    @Operation(summary = "Get Topics By Level")
    public ResponseEntity<ApiResponse<List<TopicResponse>>> getTopicsByLevel(@PathVariable Long levelId) {
        List<Topic> topics = topicRepository.findByLevelId(levelId);
        List<TopicResponse> response = topics.stream()
                .map(t -> TopicResponse.builder()
                        .id(t.getId())
                        .topicName(t.getTopicName())
                        .levelId(t.getLevel().getId())
                        .levelName(t.getLevel().getLevelName())
                        .createdAt(t.getCreatedAt())
                        .build())
                .toList();
        return ResponseEntity.ok(ApiResponse.<List<TopicResponse>>builder()
                .success(true)
                .message("Topics retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/topics/{topicId}/vocabularies")
    @Operation(summary = "Get Vocabularies For Topic (via VocabBanks)")
    public ResponseEntity<ApiResponse<List<VocabularyResponse>>> getVocabulariesByTopic(@PathVariable Long topicId) {
        List<VocabBank> banks = vocabBankRepository.findByTopicId(topicId);
        List<Long> bankIds = banks.stream().map(VocabBank::getId).toList();

        Set<Long> seen = new LinkedHashSet<>();
        List<VocabularyResponse> response = new ArrayList<>();

        if (!bankIds.isEmpty()) {
            List<VocabBankVocabulary> entries = vocabBankVocabularyRepository
                    .findByVocabBankIdsWithVocabulary(bankIds);
            for (VocabBankVocabulary entry : entries) {
                var vocab = entry.getVocabulary();
                if (seen.add(vocab.getId())) {
                    response.add(VocabularyResponse.builder()
                            .id(vocab.getId())
                            .word(vocab.getWord())
                            .kana(vocab.getKana())
                            .romaji(vocab.getRomaji())
                            .meaning(vocab.getMeaning())
                            .exampleSentence(vocab.getExampleSentence())
                            .createdAt(vocab.getCreatedAt())
                            .build());
                }
            }
        }

        return ResponseEntity.ok(ApiResponse.<List<VocabularyResponse>>builder()
                .success(true)
                .message("Vocabularies retrieved successfully")
                .data(response)
                .build());
    }
}
