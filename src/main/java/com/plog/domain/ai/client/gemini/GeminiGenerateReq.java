package com.plog.domain.ai.client.gemini;

import java.util.List;

/**
 * Gemini generateContent API 요청 본문을 표현하는 DTO입니다.
 *
 * @param contents Gemini에 전달할 사용자 입력 목록
 * @author suyeon
 * @since 2026-05-26
 */
public record GeminiGenerateReq(
        List<Content> contents
) {
    /**
     * 단일 프롬프트를 Gemini generateContent 요청 형식으로 변환합니다.
     *
     * @param prompt LLM에 전달할 프롬프트
     * @return Gemini generateContent 요청 DTO
     */
    public static GeminiGenerateReq from(String prompt) {
        return new GeminiGenerateReq(List.of(new Content(List.of(new Part(prompt)))));
    }

    /**
     * Gemini 요청에서 하나의 대화 내용을 표현합니다.
     *
     * @param parts 대화 내용을 구성하는 텍스트 조각 목록
     */
    public record Content(List<Part> parts) {
    }

    /**
     * Gemini 요청에서 실제 텍스트 입력을 표현합니다.
     *
     * @param text LLM에 전달할 텍스트
     */
    public record Part(String text) {
    }
}
