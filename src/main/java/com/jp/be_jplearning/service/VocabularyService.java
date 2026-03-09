package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.VocabularyRequest;
import com.jp.be_jplearning.dto.VocabularyResponse;

public interface VocabularyService {
    PaginationResponse<VocabularyResponse> getVocabularies(int page, int size, String keyword, String sort);

    VocabularyResponse getVocabularyById(Long vocabId);

    VocabularyResponse createVocabulary(VocabularyRequest request);

    VocabularyResponse updateVocabulary(Long vocabId, VocabularyRequest request);

    void deleteVocabulary(Long vocabId);
}
