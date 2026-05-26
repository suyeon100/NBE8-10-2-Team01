package com.plog.domain.ai.writing.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * AI 글쓰기 보조 후속 질문 요청 DTO입니다.
 * <p>
 * 사용자가 AI 제안에 대해 추가로 궁금한 점을 질문할 때, 현재 초안과 선택한 제안을 함께 전달합니다.
 *
 * @param title 글 제목
 * @param content 글 초안
 * @param selectedSuggestion 사용자가 선택한 AI 제안
 * @param question 사용자의 후속 질문
 * @author suyeon
 * @since 2026-05-26
 */
public record WritingAssistFollowUpReq(
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        String title,

        @NotBlank(message = "본문은 필수 입력 항목입니다.")
        String content,

        String selectedSuggestion,

        @NotBlank(message = "질문은 필수 입력 항목입니다.")
        String question
) {
}
