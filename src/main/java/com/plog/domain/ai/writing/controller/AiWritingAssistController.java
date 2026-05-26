package com.plog.domain.ai.writing.controller;

import com.plog.domain.ai.writing.dto.WritingAssistFollowUpReq;
import com.plog.domain.ai.writing.dto.WritingAssistFollowUpRes;
import com.plog.domain.ai.writing.dto.WritingAssistReq;
import com.plog.domain.ai.writing.dto.WritingAssistRes;
import com.plog.domain.ai.writing.service.AiWritingAssistService;
import com.plog.global.response.CommonResponse;
import com.plog.global.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 글쓰기 보조 관련 HTTP 요청을 처리하는 컨트롤러 클래스입니다.
 *
 * @author suyeon
 * @since 2026-05-26
 */
@RestController
@RequestMapping("/api/ai/posts")
@RequiredArgsConstructor
public class AiWritingAssistController {

    private final AiWritingAssistService aiWritingAssistService;

    /**
     * 작성 중인 기술 글 초안을 분석하여 방향성, 목차, 보완 포인트를 제안합니다.
     *
     * @param req 글쓰기 보조 요청 정보
     * @return AI 글쓰기 보조 응답
     */
    @PostMapping("/writing-assist")
    public ResponseEntity<Response<WritingAssistRes>> assistWriting(
            @Valid @RequestBody WritingAssistReq req
    ) {
        WritingAssistRes response = aiWritingAssistService.assist(req);
        return ResponseEntity.ok(CommonResponse.success(response, "AI 글쓰기 보조 성공"));
    }

    /**
     * AI가 제안한 글쓰기 보조 내용에 대한 사용자의 후속 질문을 처리합니다.
     *
     * @param req 후속 질문 요청 정보
     * @return 후속 질문에 대한 AI 답변
     */
    @PostMapping("/writing-assist/follow-up")
    public ResponseEntity<Response<WritingAssistFollowUpRes>> followUpWritingAssist(
            @Valid @RequestBody WritingAssistFollowUpReq req
    ) {
        WritingAssistFollowUpRes response = aiWritingAssistService.followUp(req);
        return ResponseEntity.ok(CommonResponse.success(response, "AI 글쓰기 보조 후속 질문 성공"));
    }
}
