package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.common.ResourceNotFoundException;
import com.jp.be_jplearning.common.SortUtils;
import com.jp.be_jplearning.dto.VocabularyRequest;
import com.jp.be_jplearning.dto.VocabularyResponse;
import com.jp.be_jplearning.entity.Vocabulary;
import com.jp.be_jplearning.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabularyServiceImpl implements VocabularyService {

    private final VocabularyRepository vocabularyRepository;

    private static final Set<String> ALLOWED_SORT = Set.of("id", "word", "kana", "meaning", "createdAt");

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<VocabularyResponse> getVocabularies(int page, int size, String keyword, String sortStr) {
        Pageable pageable = PageRequest.of(page, size, SortUtils.parseSort(sortStr, ALLOWED_SORT, "createdAt"));

        Page<Vocabulary> vocabPage = vocabularyRepository.searchVocabularies(keyword, pageable);

        List<VocabularyResponse> content = vocabPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PaginationResponse.<VocabularyResponse>builder()
                .content(content)
                .page(vocabPage.getNumber())
                .size(vocabPage.getSize())
                .totalElements(vocabPage.getTotalElements())
                .totalPages(vocabPage.getTotalPages())
                .last(vocabPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public VocabularyResponse getVocabularyById(Long vocabId) {
        Vocabulary vocabulary = vocabularyRepository.findById(vocabId)
                .orElseThrow(() -> new ResourceNotFoundException("Vocabulary not found with id: " + vocabId));
        return mapToResponse(vocabulary);
    }

    @Override
    @Transactional
    public VocabularyResponse createVocabulary(VocabularyRequest request) {
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setWord(request.getWord());
        vocabulary.setKana(request.getKana());
        vocabulary.setRomaji(request.getRomaji());
        vocabulary.setMeaning(request.getMeaning());
        vocabulary.setExampleSentence(request.getExampleSentence());
        vocabulary.setCreatedAt(LocalDateTime.now());

        Vocabulary saved = vocabularyRepository.save(vocabulary);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public VocabularyResponse updateVocabulary(Long vocabId, VocabularyRequest request) {
        Vocabulary vocabulary = vocabularyRepository.findById(vocabId)
                .orElseThrow(() -> new ResourceNotFoundException("Vocabulary not found with id: " + vocabId));

        vocabulary.setWord(request.getWord());
        vocabulary.setKana(request.getKana());
        vocabulary.setRomaji(request.getRomaji());
        vocabulary.setMeaning(request.getMeaning());
        vocabulary.setExampleSentence(request.getExampleSentence());

        Vocabulary updated = vocabularyRepository.save(vocabulary);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteVocabulary(Long vocabId) {
        if (!vocabularyRepository.existsById(vocabId)) {
            throw new ResourceNotFoundException("Vocabulary not found with id: " + vocabId);
        }
        vocabularyRepository.deleteById(vocabId);
    }

    private VocabularyResponse mapToResponse(Vocabulary vocabulary) {
        return VocabularyResponse.builder()
                .id(vocabulary.getId())
                .word(vocabulary.getWord())
                .kana(vocabulary.getKana())
                .romaji(vocabulary.getRomaji())
                .meaning(vocabulary.getMeaning())
                .exampleSentence(vocabulary.getExampleSentence())
                .createdAt(vocabulary.getCreatedAt())
                .build();
    }
}
