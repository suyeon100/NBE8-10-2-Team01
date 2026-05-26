package com.plog.domain.ai.client;

/**
 * 외부 LLM API 호출을 추상화하는 인터페이스입니다.
 * <p>
 * Gemini, OpenAI 등 구체적인 공급자 구현을 서비스 계층과 분리하기 위해 사용합니다.
 *
 * @author suyeon
 * @since 2026-05-26
 */
public interface LlmClient {

    /**
     * 프롬프트를 외부 LLM에 전달하고 생성된 텍스트를 반환합니다.
     *
     * @param prompt LLM에 전달할 프롬프트
     * @return LLM이 생성한 응답 텍스트
     */
    String generate(String prompt);
}
