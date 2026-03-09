package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.common.ResourceNotFoundException;
import com.jp.be_jplearning.dto.LearnerResponse;
import com.jp.be_jplearning.entity.Learner;
import com.jp.be_jplearning.entity.enums.LearnerStatusEnum;
import com.jp.be_jplearning.repository.LearnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearnerServiceImpl implements LearnerService {

    private final LearnerRepository learnerRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<LearnerResponse> getLearners(int page, int size, String keyword, String status,
            String sortStr) {
        String[] sortParams = sortStr.split(",");
        String sortBy = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        LearnerStatusEnum statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = LearnerStatusEnum.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        Page<Learner> learnerPage = learnerRepository.searchLearners(keyword, statusEnum, pageable);

        List<LearnerResponse> content = learnerPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PaginationResponse.<LearnerResponse>builder()
                .content(content)
                .page(learnerPage.getNumber())
                .size(learnerPage.getSize())
                .totalElements(learnerPage.getTotalElements())
                .totalPages(learnerPage.getTotalPages())
                .last(learnerPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public LearnerResponse getLearnerById(Long learnerId) {
        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Learner not found with id: " + learnerId));
        return mapToResponse(learner);
    }

    @Override
    @Transactional
    public LearnerResponse lockLearner(Long learnerId) {
        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Learner not found with id: " + learnerId));
        learner.setStatus(LearnerStatusEnum.LOCKED);
        return mapToResponse(learnerRepository.save(learner));
    }

    @Override
    @Transactional
    public LearnerResponse unlockLearner(Long learnerId) {
        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Learner not found with id: " + learnerId));
        learner.setStatus(LearnerStatusEnum.ACTIVE);
        return mapToResponse(learnerRepository.save(learner));
    }

    private LearnerResponse mapToResponse(Learner learner) {
        return LearnerResponse.builder()
                .id(learner.getId())
                .username(learner.getUsername())
                .email(learner.getEmail())
                .firstName(learner.getFirstName())
                .lastName(learner.getLastName())
                .avatarUrl(learner.getAvatarUrl())
                .status(learner.getStatus() != null ? learner.getStatus().name() : "ACTIVE")
                .createdAt(learner.getCreatedAt())
                .build();
    }
}
