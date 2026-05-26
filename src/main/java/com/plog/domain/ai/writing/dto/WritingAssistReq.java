package com.plog.domain.ai.writing.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * AI 글쓰기 보조 요청 DTO입니다.
 * <p>
 * 사용자가 작성 중인 기술 글의 제목과 초안을 기반으로 글의 방향성, 목차, 보완 포인트를 제안받기 위한
 * 입력 값을 전달합니다.
 *
 * @param title 글 제목
 * @param content 글 초안
 * @param targetReader 예상 독자
 * @param goal 글 작성 목적
 * @author suyeon
 * @since 2026-05-26
 */
public record WritingAssistReq(
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        String title,

        @NotBlank(message = "본문은 필수 입력 항목입니다.")
        String content,

        String targetReader,

        String goal
) {
}
