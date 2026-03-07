package com.jp.be_jplearning.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.*;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AwsPollyClient {

    private final PollyClient pollyClient;

    public byte[] synthesizeSpeech(String ssmlText) {
        try {
            SynthesizeSpeechRequest synthReq = SynthesizeSpeechRequest.builder()
                    .text(ssmlText)
                    .textType(TextType.SSML)
                    .voiceId(VoiceId.MIZUKI) // MIZUKI is a standard AWS Japanese female voice
                    .outputFormat(OutputFormat.MP3)
                    .build();

            log.info("Requesting Audio Synthesis from AWS Polly...");
            ResponseInputStream<SynthesizeSpeechResponse> response = pollyClient.synthesizeSpeech(synthReq);
            log.info("Successfully synthesized speech with AWS Polly.");

            return response.readAllBytes();
        } catch (PollyException | IOException e) {
            log.error("Failed to synthesize speech using Polly", e);
            throw new RuntimeException("Polly Synthesis Error: " + e.getMessage(), e);
        }
    }
}
