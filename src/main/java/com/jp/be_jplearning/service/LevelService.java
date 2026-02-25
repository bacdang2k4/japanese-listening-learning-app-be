package com.jp.be_jplearning.service;

import com.jp.be_jplearning.dto.LevelRequest;
import com.jp.be_jplearning.dto.LevelResponse;

import java.util.List;

public interface LevelService {
    LevelResponse createLevel(LevelRequest request);

    List<LevelResponse> getAllLevels();

    LevelResponse getLevelById(Long levelId);

    LevelResponse updateLevel(Long levelId, LevelRequest request);

    void deleteLevel(Long levelId);

    List<LevelResponse> getLevelsByAdmin(Long adminId);
}
