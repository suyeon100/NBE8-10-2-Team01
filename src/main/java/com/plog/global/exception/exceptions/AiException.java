package com.plog.global.exception.exceptions;

import com.plog.global.exception.errorCode.AiErrorCode;

/**
 * AI 글쓰기 보조 기능 처리 중 발생하는 예외 클래스입니다.
 *
 * @author suyeon
 * @since 2026-05-26
 */
public class AiException extends BaseException {

    public AiException(AiErrorCode errorCode) {
        super(errorCode);
    }

    public AiException(AiErrorCode errorCode, String logMessage) {
        super(errorCode, logMessage);
    }

    public AiException(AiErrorCode errorCode, String logMessage, String clientMessage) {
        super(errorCode, logMessage, clientMessage);
    }
}
