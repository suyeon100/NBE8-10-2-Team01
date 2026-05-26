package com.plog.domain.ai.client.gemini;

import java.util.List;

/**
 * Gemini generateContent API 응답 본문을 표현하는 DTO입니다.
 *
 * @param candidates Gemini가 생성한 응답 후보 목록
 * @author suyeon
 * @since 2026-05-26
 */
public record GeminiGenerateRes(
        List<Candidate> candidates
) {
    /**
     * 첫 번째 응답 후보의 첫 번째 텍스트를 반환합니다.
     *
     * @return Gemini가 생성한 텍스트. 응답이 비어 있으면 빈 문자열
     */
    public String firstText() {
        if (candidates == null || candidates.isEmpty()) {
            return "";
        }

        Content content = candidates.get(0).content();
        if (content == null || content.parts() == null || content.parts().isEmpty()) {
            return "";
        }

        return content.parts().get(0).text();
    }

    /**
     * Gemini 응답 후보를 표현합니다.
     *
     * @param content 응답 후보의 본문
     */
    public record Candidate(Content content) {
    }

    /**
     * Gemini 응답 본문을 표현합니다.
     *
     * @param parts 응답 본문을 구성하는 텍스트 조각 목록
     */
    public record Content(List<Part> parts) {
    }

    /**
     * Gemini 응답의 실제 텍스트 조각을 표현합니다.
     *
     * @param text LLM이 생성한 텍스트
     */
    public record Part(String text) {
    }
}
