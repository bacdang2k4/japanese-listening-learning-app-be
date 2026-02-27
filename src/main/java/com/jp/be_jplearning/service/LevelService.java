package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.LevelRequest;
import com.jp.be_jplearning.dto.LevelResponse;

public interface LevelService {
    LevelResponse createLevel(LevelRequest request);

    PaginationResponse<LevelResponse> getAllLevels(int page, int size);

    LevelResponse getLevelById(Long levelId);

    LevelResponse updateLevel(Long levelId, LevelRequest request);

    void deleteLevel(Long levelId);
}
