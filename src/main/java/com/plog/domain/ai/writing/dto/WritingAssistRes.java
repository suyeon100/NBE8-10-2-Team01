package com.plog.domain.ai.writing.dto;

import java.util.List;

/**
 * AI 글쓰기 보조 응답 DTO입니다.
 *
 * @param direction 글의 추천 방향성
 * @param outline 추천 목차
 * @param improvementPoints 보완하면 좋은 포인트
 * @param readerQuestions 독자가 궁금해할 만한 질문
 * @param missingContext 추가하면 좋은 배경 설명 또는 근거
 * @author suyeon
 * @since 2026-05-26
 */
public record WritingAssistRes(
        String direction,
        List<String> outline,
        List<String> improvementPoints,
        List<String> readerQuestions,
        List<String> missingContext
) {
}
