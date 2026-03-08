package com.jp.be_jplearning.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Gemini AI Client integration.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AiClient {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.api.key}")
    private String geminiApiKey;
    @Value("${ai.model}")
    private String aiModel;
    
    public String generateListeningTest(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + aiModel + ":generateContent?key="
                + geminiApiKey;

        try {
            // Construct the request body for Gemini API
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt
                                            + "\n\nCRITICAL: You MUST return ONLY valid JSON matching the exact schema requested, without any markdown formatting like ```json. Do not include any other text explanations.")))),
                    "generationConfig", Map.of(
                            "responseMimeType", "application/json"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            log.info("Sending request to Gemini API...");
            String response = restTemplate.postForObject(url, requestEntity, String.class);

            // Parse Gemini response to extract the text
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode textNode = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text");

            String jsonOutput = textNode.asText();

            // Sometimes Gemini wraps JSON in markdown blocks even when told not to. Clean
            // it up just in case.
            if (jsonOutput.startsWith("```json")) {
                jsonOutput = jsonOutput.substring(7);
            }
            if (jsonOutput.startsWith("```")) {
                jsonOutput = jsonOutput.substring(3);
            }
            if (jsonOutput.endsWith("```")) {
                jsonOutput = jsonOutput.substring(0, jsonOutput.length() - 3);
            }

            return jsonOutput.trim();

        } catch (org.springframework.web.client.RestClientResponseException e) {
            String errorBody = e.getResponseBodyAsString();
            log.error("Gemini API Error Response: {}", errorBody, e);
            throw new RuntimeException("Gemini API failed: " + errorBody, e);
        } catch (Exception e) {
            log.error("Failed to generate AI content from Gemini", e);
            throw new RuntimeException("Failed to generate AI content from Gemini: " + e.getMessage(), e);
        }
    }
}
