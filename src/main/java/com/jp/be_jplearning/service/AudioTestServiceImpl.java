package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.common.ResourceNotFoundException;
import com.jp.be_jplearning.common.SortUtils;
import com.jp.be_jplearning.dto.AudioTestRequest;
import com.jp.be_jplearning.dto.AudioTestResponse;
import com.jp.be_jplearning.entity.AudioTest;
import com.jp.be_jplearning.entity.Topic;
import com.jp.be_jplearning.entity.enums.TestStatusEnum;
import com.jp.be_jplearning.repository.AudioTestRepository;
import com.jp.be_jplearning.repository.TopicRepository;
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
public class AudioTestServiceImpl implements AudioTestService {

    private final AudioTestRepository audioTestRepository;
    private final TopicRepository topicRepository;

    private static final Set<String> ALLOWED_SORT = Set.of("id", "testName", "createdAt", "status", "duration", "testOrder");

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<AudioTestResponse> getAudioTests(int page, int size, Long topicId, TestStatusEnum status,
            String keyword, String sortStr) {
        // Default sort: by testOrder ASC when filtering by topic, otherwise by createdAt DESC
        String defaultSort = (topicId != null) ? "testOrder,asc" : "createdAt,desc";
        Pageable pageable = PageRequest.of(page, size, SortUtils.parseSort(sortStr, ALLOWED_SORT, defaultSort));

        Page<AudioTest> testPage = audioTestRepository.searchAudioTests(topicId, status, keyword, pageable);

        List<AudioTestResponse> content = testPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PaginationResponse.<AudioTestResponse>builder()
                .content(content)
                .page(testPage.getNumber())
                .size(testPage.getSize())
                .totalElements(testPage.getTotalElements())
                .totalPages(testPage.getTotalPages())
                .last(testPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AudioTestResponse getAudioTestById(Long testId) {
        AudioTest test = audioTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("AudioTest not found with id: " + testId));
        return mapToResponse(test);
    }

    @Override
    @Transactional
    public AudioTestResponse createAudioTest(AudioTestRequest request) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + request.getTopicId()));

        AudioTest test = new AudioTest();
        test.setTestName(request.getTestName());
        test.setTopic(topic);
        test.setTranscript(request.getTranscript());
        test.setAudioUrl(request.getAudioUrl());
        test.setDuration(request.getDuration());
        test.setPassCondition(request.getPassCondition());
        test.setStatus(TestStatusEnum.DRAFT);
        test.setCreatedAt(LocalDateTime.now());

        // Auto-assign testOrder: if provided use it, otherwise max + 1 for the topic
        if (request.getTestOrder() != null) {
            test.setTestOrder(request.getTestOrder());
        } else {
            Integer maxOrder = audioTestRepository.findMaxTestOrderByTopicId(request.getTopicId());
            test.setTestOrder(maxOrder + 1);
        }

        // Auto-generate plain transcript from SSML if not provided
        if (request.getPlainTranscript() != null) {
            test.setPlainTranscript(request.getPlainTranscript());
        } else if (request.getTranscript() != null && !request.getTranscript().isEmpty()) {
            test.setPlainTranscript(stripSSML(request.getTranscript()));
        }

        return mapToResponse(audioTestRepository.save(test));
    }

    @Override
    @Transactional
    public AudioTestResponse updateAudioTest(Long testId, AudioTestRequest request) {
        AudioTest test = audioTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("AudioTest not found with id: " + testId));

        if (!test.getTopic().getId().equals(request.getTopicId())) {
            Topic topic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Topic not found with id: " + request.getTopicId()));
            test.setTopic(topic);
        }

        test.setTestName(request.getTestName());
        test.setTranscript(request.getTranscript());
        test.setAudioUrl(request.getAudioUrl());
        test.setDuration(request.getDuration());
        if (request.getPassCondition() != null) {
            test.setPassCondition(request.getPassCondition());
        }
        if (request.getTestOrder() != null) {
            test.setTestOrder(request.getTestOrder());
        }
        // Update plain transcript if provided or if transcript changed
        if (request.getPlainTranscript() != null) {
            test.setPlainTranscript(request.getPlainTranscript());
        } else if (request.getTranscript() != null &&
                   !request.getTranscript().equals(test.getTranscript())) {
            test.setPlainTranscript(stripSSML(request.getTranscript()));
        }
        test.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(audioTestRepository.save(test));
    }

    @Override
    @Transactional
    public void deleteAudioTest(Long testId) {
        if (!audioTestRepository.existsById(testId)) {
            throw new ResourceNotFoundException("AudioTest not found with id: " + testId);
        }
        audioTestRepository.deleteById(testId);
    }

    @Override
    @Transactional
    public void publishAudioTest(Long testId) {
        AudioTest test = audioTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("AudioTest not found with id: " + testId));
        test.setStatus(TestStatusEnum.PUBLISHED);
        test.setUpdatedAt(LocalDateTime.now());
        audioTestRepository.save(test);
    }

    @Override
    @Transactional
    public void rejectAudioTest(Long testId) {
        AudioTest test = audioTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("AudioTest not found with id: " + testId));
        test.setStatus(TestStatusEnum.REJECTED);
        test.setUpdatedAt(LocalDateTime.now());
        audioTestRepository.save(test);
    }

    private AudioTestResponse mapToResponse(AudioTest test) {
        return AudioTestResponse.builder()
                .testId(test.getId())
                .testName(test.getTestName())
                .topicId(test.getTopic() != null ? test.getTopic().getId() : null)
                .topicName(test.getTopic() != null ? test.getTopic().getTopicName() : null)
                .transcript(test.getTranscript())
                .audioUrl(test.getAudioUrl())
                .duration(test.getDuration())
                .passCondition(test.getPassCondition())
                .status(test.getStatus() != null ? test.getStatus().name() : null)
                .createdAt(test.getCreatedAt())
                .updatedAt(test.getUpdatedAt())
                .testOrder(test.getTestOrder())
                .build();
    }

    // Simple SSML to plain text converter
    private String stripSSML(String ssml) {
        if (ssml == null) return null;
        // Remove SSML tags
        String noTags = ssml.replaceAll("<[^>]*>", "");
        // Decode common HTML entities that might be in SSML
        return noTags.replaceAll("&amp;", "&")
                     .replaceAll("&lt;", "<")
                     .replaceAll("&gt;", ">")
                     .replaceAll("&quot;", "\"")
                     .replaceAll("&apos;", "'")
                     .trim();
    }
}
