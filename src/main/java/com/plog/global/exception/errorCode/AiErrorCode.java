package com.plog.global.exception.errorCode;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * AI 글쓰기 보조 기능에서 발생하는 예외에 대한 상수 값을 정의합니다.
 *
 * @author suyeon
 * @since 2026-05-26
 */
@AllArgsConstructor
public enum AiErrorCode implements ErrorCode {
    AI_CONFIG_MISSING(HttpStatus.INTERNAL_SERVER_ERROR, "AI 설정이 누락되었습니다."),
    AI_REQUEST_FAIL(HttpStatus.BAD_GATEWAY, "AI 글쓰기 보조 요청에 실패했습니다."),
    AI_RESPONSE_PARSE_FAIL(HttpStatus.BAD_GATEWAY, "AI 응답을 처리하지 못했습니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
