package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.VocabBankRequest;
import com.jp.be_jplearning.dto.VocabBankResponse;
import com.jp.be_jplearning.dto.VocabBankVocabularyRequest;

import java.util.List;

public interface VocabBankService {
    PaginationResponse<VocabBankResponse> getVocabBanks(int page, int size, Long topicId, String keyword, String sort);

    VocabBankResponse getVocabBankById(Long vocabBankId);

    VocabBankResponse createVocabBank(VocabBankRequest request);

    VocabBankResponse updateVocabBank(Long vocabBankId, VocabBankRequest request);

    void deleteVocabBank(Long vocabBankId);

    VocabBankResponse addVocabulariesToBank(Long vocabBankId, List<VocabBankVocabularyRequest> requests);

    void removeVocabularyFromBank(Long vocabBankId, Long vocabId);

    VocabBankResponse reorderVocabulariesInBank(Long vocabBankId, List<VocabBankVocabularyRequest> requests);
}
