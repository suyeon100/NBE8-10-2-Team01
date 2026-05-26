package com.plog.domain.ai.client.gemini;

import com.plog.domain.ai.client.LlmClient;
import com.plog.global.exception.errorCode.AiErrorCode;
import com.plog.global.exception.exceptions.AiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/**
 * Gemini generateContent API를 호출하는 LLM Client 구현체입니다.
 *
 * @author suyeon
 * @since 2026-05-26
 */
@Slf4j
@Component
public class GeminiClient implements LlmClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    /**
     * Gemini API 호출에 사용할 RestClient와 인증 정보를 초기화합니다.
     *
     * @param restClientBuilder RestClient 생성을 위한 빌더
     * @param baseUrl Gemini API 기본 URL
     * @param apiKey Gemini API Key
     * @param model 사용할 Gemini 모델명
     */
    public GeminiClient(
            RestClient.Builder restClientBuilder,
            @Value("${gemini.base-url}") String baseUrl,
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public String generate(String prompt) {
        if (!StringUtils.hasText(apiKey)) {
            throw new AiException(AiErrorCode.AI_CONFIG_MISSING,
                    "[GeminiClient#generate] GEMINI_API_KEY is blank");
        }

        try {
            GeminiGenerateRes response = restClient.post()
                    .uri("/models/{model}:generateContent", model)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(GeminiGenerateReq.from(prompt))
                    .retrieve()
                    .body(GeminiGenerateRes.class);

            String text = response != null ? response.firstText() : "";
            if (!StringUtils.hasText(text)) {
                throw new AiException(AiErrorCode.AI_RESPONSE_PARSE_FAIL,
                        "[GeminiClient#generate] empty response text");
            }

            return text;
        } catch (AiException e) {
            throw e;
        } catch (Exception e) {
            log.warn("[GeminiClient#generate] request failed. model={}", model, e);
            throw new AiException(AiErrorCode.AI_REQUEST_FAIL,
                    "[GeminiClient#generate] request failed. model=" + model);
        }
    }
}
