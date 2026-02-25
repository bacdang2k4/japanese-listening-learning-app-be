package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.ResourceNotFoundException;
import com.jp.be_jplearning.dto.LevelRequest;
import com.jp.be_jplearning.dto.LevelResponse;
import com.jp.be_jplearning.entity.Admin;
import com.jp.be_jplearning.entity.Level;
import com.jp.be_jplearning.repository.AdminRepository;
import com.jp.be_jplearning.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LevelServiceImpl implements LevelService {

    private final LevelRepository levelRepository;
    private final AdminRepository adminRepository;

    @Override
    @Transactional
    public LevelResponse createLevel(LevelRequest request) {
        Admin admin = adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + request.getAdminId()));

        Level level = new Level();
        level.setLevelName(request.getLevelName());
        level.setAdmin(admin);
        level.setCreatedAt(LocalDateTime.now()); // Manually set for immediate response if needed

        Level savedLevel = levelRepository.save(level);

        return mapToResponse(savedLevel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LevelResponse> getAllLevels() {
        return levelRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LevelResponse getLevelById(Long levelId) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + levelId));
        return mapToResponse(level);
    }

    @Override
    @Transactional
    public LevelResponse updateLevel(Long levelId, LevelRequest request) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + levelId));

        if (!level.getAdmin().getId().equals(request.getAdminId())) {
            Admin newAdmin = adminRepository.findById(request.getAdminId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Admin not found with id: " + request.getAdminId()));
            level.setAdmin(newAdmin);
        }

        level.setLevelName(request.getLevelName());
        level.setUpdatedAt(LocalDateTime.now());

        Level updatedLevel = levelRepository.save(level);
        return mapToResponse(updatedLevel);
    }

    @Override
    @Transactional
    public void deleteLevel(Long levelId) {
        if (!levelRepository.existsById(levelId)) {
            throw new ResourceNotFoundException("Level not found with id: " + levelId);
        }
        levelRepository.deleteById(levelId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LevelResponse> getLevelsByAdmin(Long adminId) {
        if (!adminRepository.existsById(adminId)) {
            throw new ResourceNotFoundException("Admin not found with id: " + adminId);
        }
        return levelRepository.findByAdminId(adminId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private LevelResponse mapToResponse(Level level) {
        return LevelResponse.builder()
                .id(level.getId())
                .levelName(level.getLevelName())
                .adminId(level.getAdmin() != null ? level.getAdmin().getId() : null)
                .adminName(level.getAdmin() != null ? level.getAdmin().getName() : null)
                .createdAt(level.getCreatedAt())
                .build();
    }
}
