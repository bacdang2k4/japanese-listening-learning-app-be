package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.common.ResourceNotFoundException;
import com.jp.be_jplearning.dto.VocabBankRequest;
import com.jp.be_jplearning.dto.VocabBankResponse;
import com.jp.be_jplearning.dto.VocabBankVocabularyRequest;
import com.jp.be_jplearning.entity.Topic;
import com.jp.be_jplearning.entity.VocabBank;
import com.jp.be_jplearning.entity.VocabBankVocabulary;
import com.jp.be_jplearning.entity.VocabBankVocabularyId;
import com.jp.be_jplearning.entity.Vocabulary;
import com.jp.be_jplearning.repository.TopicRepository;
import com.jp.be_jplearning.repository.VocabBankRepository;
import com.jp.be_jplearning.repository.VocabBankVocabularyRepository;
import com.jp.be_jplearning.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabBankServiceImpl implements VocabBankService {

    private final VocabBankRepository vocabBankRepository;
    private final TopicRepository topicRepository;
    private final VocabularyRepository vocabularyRepository;
    private final VocabBankVocabularyRepository vocabBankVocabularyRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<VocabBankResponse> getVocabBanks(int page, int size, Long topicId, String keyword,
            String sortStr) {
        String[] sortParams = sortStr.split(",");
        String sortBy = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<VocabBank> bankPage = vocabBankRepository.searchVocabBanks(topicId, keyword, pageable);

        List<VocabBankResponse> content = bankPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PaginationResponse.<VocabBankResponse>builder()
                .content(content)
                .page(bankPage.getNumber())
                .size(bankPage.getSize())
                .totalElements(bankPage.getTotalElements())
                .totalPages(bankPage.getTotalPages())
                .last(bankPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public VocabBankResponse getVocabBankById(Long vocabBankId) {
        VocabBank vocabBank = vocabBankRepository.findById(vocabBankId)
                .orElseThrow(() -> new ResourceNotFoundException("VocabBank not found with id: " + vocabBankId));
        return mapToResponseWithVocabularies(vocabBank);
    }

    @Override
    @Transactional
    public VocabBankResponse createVocabBank(VocabBankRequest request) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + request.getTopicId()));

        VocabBank vocabBank = new VocabBank();
        vocabBank.setTitle(request.getTitle());
        vocabBank.setDescription(request.getDescription());
        vocabBank.setTopic(topic);
        vocabBank.setCreatedAt(LocalDateTime.now());

        VocabBank saved = vocabBankRepository.save(vocabBank);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public VocabBankResponse updateVocabBank(Long vocabBankId, VocabBankRequest request) {
        VocabBank vocabBank = vocabBankRepository.findById(vocabBankId)
                .orElseThrow(() -> new ResourceNotFoundException("VocabBank not found with id: " + vocabBankId));

        if (!vocabBank.getTopic().getId().equals(request.getTopicId())) {
            Topic topic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Topic not found with id: " + request.getTopicId()));
            vocabBank.setTopic(topic);
        }

        vocabBank.setTitle(request.getTitle());
        vocabBank.setDescription(request.getDescription());

        VocabBank updated = vocabBankRepository.save(vocabBank);
        return mapToResponseWithVocabularies(updated);
    }

    @Override
    @Transactional
    public void deleteVocabBank(Long vocabBankId) {
        if (!vocabBankRepository.existsById(vocabBankId)) {
            throw new ResourceNotFoundException("VocabBank not found with id: " + vocabBankId);
        }
        vocabBankRepository.deleteById(vocabBankId);
    }

    @Override
    @Transactional
    public VocabBankResponse addVocabulariesToBank(Long vocabBankId, List<VocabBankVocabularyRequest> requests) {
        VocabBank vocabBank = vocabBankRepository.findById(vocabBankId)
                .orElseThrow(() -> new ResourceNotFoundException("VocabBank not found with id: " + vocabBankId));

        for (VocabBankVocabularyRequest req : requests) {
            Vocabulary vocabulary = vocabularyRepository.findById(req.getVocabId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Vocabulary not found with id: " + req.getVocabId()));

            VocabBankVocabularyId compositeId = new VocabBankVocabularyId(vocabBankId, req.getVocabId());
            if (!vocabBankVocabularyRepository.existsById(compositeId)) {
                VocabBankVocabulary entry = new VocabBankVocabulary(vocabBank, vocabulary, req.getVocabOrder());
                vocabBankVocabularyRepository.save(entry);
            }
        }

        return mapToResponseWithVocabularies(vocabBank);
    }

    @Override
    @Transactional
    public void removeVocabularyFromBank(Long vocabBankId, Long vocabId) {
        VocabBankVocabularyId compositeId = new VocabBankVocabularyId(vocabBankId, vocabId);
        if (!vocabBankVocabularyRepository.existsById(compositeId)) {
            throw new ResourceNotFoundException(
                    "Vocabulary " + vocabId + " not found in VocabBank " + vocabBankId);
        }
        vocabBankVocabularyRepository.deleteById(compositeId);
    }

    @Override
    @Transactional
    public VocabBankResponse reorderVocabulariesInBank(Long vocabBankId, List<VocabBankVocabularyRequest> requests) {
        VocabBank vocabBank = vocabBankRepository.findById(vocabBankId)
                .orElseThrow(() -> new ResourceNotFoundException("VocabBank not found with id: " + vocabBankId));

        for (VocabBankVocabularyRequest req : requests) {
            VocabBankVocabularyId compositeId = new VocabBankVocabularyId(vocabBankId, req.getVocabId());
            VocabBankVocabulary entry = vocabBankVocabularyRepository.findById(compositeId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Vocabulary " + req.getVocabId() + " not found in VocabBank " + vocabBankId));
            entry.setVocabOrder(req.getVocabOrder());
            vocabBankVocabularyRepository.save(entry);
        }

        return mapToResponseWithVocabularies(vocabBank);
    }

    private VocabBankResponse mapToResponse(VocabBank vocabBank) {
        return VocabBankResponse.builder()
                .id(vocabBank.getId())
                .title(vocabBank.getTitle())
                .description(vocabBank.getDescription())
                .topicId(vocabBank.getTopic() != null ? vocabBank.getTopic().getId() : null)
                .topicName(vocabBank.getTopic() != null ? vocabBank.getTopic().getTopicName() : null)
                .createdAt(vocabBank.getCreatedAt())
                .build();
    }

    private VocabBankResponse mapToResponseWithVocabularies(VocabBank vocabBank) {
        List<VocabBankVocabulary> entries = vocabBankVocabularyRepository
                .findByVocabBankIdOrderByVocabOrderAsc(vocabBank.getId());

        List<VocabBankResponse.VocabBankVocabularyResponse> vocabularies = entries.stream()
                .map(entry -> VocabBankResponse.VocabBankVocabularyResponse.builder()
                        .vocabId(entry.getVocabulary().getId())
                        .word(entry.getVocabulary().getWord())
                        .kana(entry.getVocabulary().getKana())
                        .romaji(entry.getVocabulary().getRomaji())
                        .meaning(entry.getVocabulary().getMeaning())
                        .exampleSentence(entry.getVocabulary().getExampleSentence())
                        .vocabOrder(entry.getVocabOrder())
                        .build())
                .collect(Collectors.toList());

        return VocabBankResponse.builder()
                .id(vocabBank.getId())
                .title(vocabBank.getTitle())
                .description(vocabBank.getDescription())
                .topicId(vocabBank.getTopic() != null ? vocabBank.getTopic().getId() : null)
                .topicName(vocabBank.getTopic() != null ? vocabBank.getTopic().getTopicName() : null)
                .vocabularies(vocabularies)
                .createdAt(vocabBank.getCreatedAt())
                .build();
    }
}
