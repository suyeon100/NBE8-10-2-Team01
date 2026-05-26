package com.plog.domain.ai.writing.dto;

/**
 * AI 글쓰기 보조 후속 질문 응답 DTO입니다.
 *
 * @param answer 사용자의 후속 질문에 대한 답변
 * @author suyeon
 * @since 2026-05-26
 */
public record WritingAssistFollowUpRes(
        String answer
) {
}
