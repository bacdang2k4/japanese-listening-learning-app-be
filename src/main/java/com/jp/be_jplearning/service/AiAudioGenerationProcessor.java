package com.jp.be_jplearning.service;

import com.jp.be_jplearning.entity.AIGenerationLog;
import com.jp.be_jplearning.entity.AudioTest;
import com.jp.be_jplearning.entity.enums.GenerationStatusEnum;
import com.jp.be_jplearning.entity.enums.TestStatusEnum;
import com.jp.be_jplearning.integration.AwsPollyClient;
import com.jp.be_jplearning.integration.AwsS3Client;
import com.jp.be_jplearning.repository.AIGenerationLogRepository;
import com.jp.be_jplearning.repository.AudioTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiAudioGenerationProcessor {

    private final AwsPollyClient pollyClient;
    private final AwsS3Client s3Client;
    private final AudioTestRepository audioTestRepository;
    private final AIGenerationLogRepository logRepository;

    @Async
    @Transactional(noRollbackFor = Exception.class)
    public void generateAudioAndUpload(Long testId, String ssmlTranscript, Long logId) {
        log.info("Starting async background job for audio generation for testId: {}", testId);

        AudioTest test = audioTestRepository.findById(testId).orElse(null);
        AIGenerationLog logEntry = logRepository.findById(logId).orElse(null);

        if (test == null || logEntry == null) {
            log.error("Could not find AudioTest or AIGenerationLog - aborting async job");
            return;
        }

        try {
            // 1. Synthesize Speech with Polly
            byte[] mp3Bytes = pollyClient.synthesizeSpeech(ssmlTranscript);

            // 2. Upload to S3
            String fileName = "audiotest-" + testId + "-" + System.currentTimeMillis() + ".mp3";
            String audioUrl = s3Client.uploadAudioBytes(fileName, mp3Bytes);

            // 3. Update Status
            test.setAudioUrl(audioUrl);
            test.setStatus(TestStatusEnum.PENDING_REVIEW);
            audioTestRepository.save(test);

            log.info("Async audio generation completed successfully for testId: {}", testId);

        } catch (Exception e) {
            log.error("Async audio generation failed for testId: {}", testId, e);
            test.setStatus(TestStatusEnum.REJECTED);
            audioTestRepository.save(test);

            String errorDetail = e.getMessage();
            if (e.getCause() != null) {
                errorDetail += " | Cause: " + e.getCause().getMessage();
            }

            if (logEntry.getRawResponse() != null) {
                logEntry.setRawResponse(logEntry.getRawResponse() + "\n\n--- AUDIO GEN ERROR ---\n" + errorDetail);
            } else {
                logEntry.setRawResponse("Audio Gen Error: " + errorDetail);
            }
            logEntry.setStatus(GenerationStatusEnum.FAILED);
            logRepository.save(logEntry);
        }
    }
}
