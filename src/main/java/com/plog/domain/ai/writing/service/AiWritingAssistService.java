package com.plog.domain.ai.writing.service;

import com.plog.domain.ai.writing.dto.WritingAssistFollowUpReq;
import com.plog.domain.ai.writing.dto.WritingAssistFollowUpRes;
import com.plog.domain.ai.writing.dto.WritingAssistReq;
import com.plog.domain.ai.writing.dto.WritingAssistRes;

/**
 * AI 글쓰기 보조 비즈니스 로직을 정의하는 인터페이스입니다.
 *
 * @author suyeon
 * @since 2026-05-26
 */
public interface AiWritingAssistService {

    /**
     * 작성 중인 기술 글 초안을 분석하여 글의 방향성, 목차, 보완 포인트를 제안합니다.
     *
     * @param req 글쓰기 보조 요청 정보
     * @return AI 글쓰기 보조 응답
     */
    WritingAssistRes assist(WritingAssistReq req);

    /**
     * AI 제안에 대한 사용자의 후속 질문에 답변합니다.
     *
     * @param req 후속 질문 요청 정보
     * @return 후속 질문에 대한 AI 답변
     */
    WritingAssistFollowUpRes followUp(WritingAssistFollowUpReq req);
}
