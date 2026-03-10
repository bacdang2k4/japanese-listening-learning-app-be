package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.common.ResourceNotFoundException;
import com.jp.be_jplearning.common.SortUtils;
import com.jp.be_jplearning.dto.TopicRequest;
import com.jp.be_jplearning.dto.TopicResponse;
import com.jp.be_jplearning.entity.Level;
import com.jp.be_jplearning.entity.Topic;
import com.jp.be_jplearning.repository.LevelRepository;
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
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final LevelRepository levelRepository;

    private static final Set<String> ALLOWED_SORT = Set.of("id", "topicName", "topicOrder", "createdAt");

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<TopicResponse> getTopics(int page, int size, Long levelId, String keyword,
            String sortStr) {
        Pageable pageable = PageRequest.of(page, size, SortUtils.parseSort(sortStr, ALLOWED_SORT, "createdAt"));

        Page<Topic> topicPage = topicRepository.searchTopics(levelId, keyword, pageable);

        List<TopicResponse> content = topicPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PaginationResponse.<TopicResponse>builder()
                .content(content)
                .page(topicPage.getNumber())
                .size(topicPage.getSize())
                .totalElements(topicPage.getTotalElements())
                .totalPages(topicPage.getTotalPages())
                .last(topicPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TopicResponse getTopicById(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + topicId));
        return mapToResponse(topic);
    }

    @Override
    @Transactional
    public TopicResponse createTopic(TopicRequest request) {
        Level level = levelRepository.findById(request.getLevelId())
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + request.getLevelId()));

        Topic topic = new Topic();
        topic.setTopicName(request.getTopicName());
        topic.setLevel(level);
        topic.setTopicOrder(request.getTopicOrder() != null ? request.getTopicOrder() : 0);
        topic.setCreatedAt(LocalDateTime.now());

        Topic savedTopic = topicRepository.save(topic);
        return mapToResponse(savedTopic);
    }

    @Override
    @Transactional
    public TopicResponse updateTopic(Long topicId, TopicRequest request) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + topicId));

        if (!topic.getLevel().getId().equals(request.getLevelId())) {
            Level level = levelRepository.findById(request.getLevelId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Level not found with id: " + request.getLevelId()));
            topic.setLevel(level);
        }

        topic.setTopicName(request.getTopicName());
        if (request.getTopicOrder() != null) {
            topic.setTopicOrder(request.getTopicOrder());
        }

        Topic updatedTopic = topicRepository.save(topic);
        return mapToResponse(updatedTopic);
    }

    @Override
    @Transactional
    public void deleteTopic(Long topicId) {
        if (!topicRepository.existsById(topicId)) {
            throw new ResourceNotFoundException("Topic not found with id: " + topicId);
        }
        topicRepository.deleteById(topicId);
    }

    private TopicResponse mapToResponse(Topic topic) {
        return TopicResponse.builder()
                .id(topic.getId())
                .topicName(topic.getTopicName())
                .levelId(topic.getLevel() != null ? topic.getLevel().getId() : null)
                .levelName(topic.getLevel() != null ? topic.getLevel().getLevelName() : null)
                .topicOrder(topic.getTopicOrder())
                .createdAt(topic.getCreatedAt())
                .build();
    }
}
