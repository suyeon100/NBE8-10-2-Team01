package com.plog.domain.ai.writing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plog.domain.ai.client.LlmClient;
import com.plog.domain.ai.writing.dto.WritingAssistFollowUpReq;
import com.plog.domain.ai.writing.dto.WritingAssistFollowUpRes;
import com.plog.domain.ai.writing.dto.WritingAssistReq;
import com.plog.domain.ai.writing.dto.WritingAssistRes;
import com.plog.global.exception.errorCode.AiErrorCode;
import com.plog.global.exception.exceptions.AiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * {@link AiWritingAssistService} 인터페이스의 기본 구현체입니다.
 * <p>
 * 사용자가 작성 중인 기술 글을 LLM에 전달하여 글의 방향성, 목차, 보완 포인트를 제안받습니다.
 * 원문을 자동 수정하지 않고 작성자가 선택할 수 있는 제안만 반환합니다.
 *
 * @author suyeon
 * @since 2026-05-26
 */
@Service
@RequiredArgsConstructor
public class AiWritingAssistServiceImpl implements AiWritingAssistService {

    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    @Override
    public WritingAssistRes assist(WritingAssistReq req) {
        String response = llmClient.generate(createPrompt(req));
        return parseResponse(response);
    }

    @Override
    public WritingAssistFollowUpRes followUp(WritingAssistFollowUpReq req) {
        String answer = llmClient.generate(createFollowUpPrompt(req));
        return new WritingAssistFollowUpRes(answer.trim());
    }

    /**
     * 기술 블로그 초안을 분석하기 위한 LLM 프롬프트를 생성합니다.
     *
     * @param req 글쓰기 보조 요청 정보
     * @return 초안 분석용 프롬프트
     */
    private String createPrompt(WritingAssistReq req) {
        return """
                당신은 개발자를 위한 기술 블로그 글쓰기 코치입니다.
                아래 초안을 자동으로 다시 작성하지 말고, 작성자가 선택적으로 참고할 수 있는 제안만 제공하세요.

                목표:
                - 글의 핵심 메시지를 선명하게 정리합니다.
                - 독자가 이해하기 좋은 흐름을 제안합니다.
                - 기술적 근거가 부족한 부분을 짚어줍니다.
                - 과장된 표현은 줄이고, 경험 기반 서술이 되도록 돕습니다.

                응답은 반드시 JSON 객체 하나만 반환하세요. 마크다운 코드블록을 사용하지 마세요.
                JSON 형식:
                {
                  "direction": "이 글이 어떤 방향으로 가면 좋은지 한 문단으로 제안",
                  "outline": ["목차 1", "목차 2", "목차 3"],
                  "improvementPoints": ["보완 포인트 1", "보완 포인트 2", "보완 포인트 3"],
                  "readerQuestions": ["독자가 궁금해할 질문 1", "독자가 궁금해할 질문 2"],
                  "missingContext": ["추가하면 좋은 배경 설명 1", "추가하면 좋은 코드/근거 1"]
                }

                제약:
                - 원문을 통째로 다시 쓰지 마세요.
                - 제목만 보고 일반론을 말하지 말고, 초안 내용에 근거해 제안하세요.
                - 기술 블로그답게 문제 상황, 원인 분석, 해결 과정, 결과가 드러나도록 제안하세요.
                - 작성자가 직접 선택할 수 있는 조언 형태로 말하세요.

                제목: %s
                예상 독자: %s
                글 작성 목적: %s
                초안:
                %s
                """.formatted(
                req.title(),
                valueOrDefault(req.targetReader(), "기술 블로그 독자"),
                valueOrDefault(req.goal(), "기술 경험 공유"),
                req.content()
        );
    }

    /**
     * AI 제안에 대한 후속 질문 답변용 LLM 프롬프트를 생성합니다.
     *
     * @param req 후속 질문 요청 정보
     * @return 후속 질문 답변용 프롬프트
     */
    private String createFollowUpPrompt(WritingAssistFollowUpReq req) {
        return """
                당신은 개발자를 위한 기술 블로그 글쓰기 코치입니다.
                사용자의 후속 질문에 답하되, 초안을 대신 완성하지 말고 작성자가 판단할 수 있는 설명과 선택지를 제공하세요.

                답변 기준:
                - 현재 초안과 선택한 제안의 맥락에 근거해 답변하세요.
                - 필요한 경우 예시 문장이나 구성 방법을 짧게 제안하세요.
                - 과장된 표현보다 경험 기반 서술이 되도록 안내하세요.
                - 답변은 한국어로 작성하세요.

                제목: %s
                선택한 제안: %s
                후속 질문: %s
                초안:
                %s
                """.formatted(
                req.title(),
                valueOrDefault(req.selectedSuggestion(), "선택한 제안 없음"),
                req.question(),
                req.content()
        );
    }

    /**
     * LLM 응답 JSON을 글쓰기 보조 응답 DTO로 변환합니다.
     *
     * @param response LLM이 반환한 원본 응답
     * @return 글쓰기 보조 응답 DTO
     */
    private WritingAssistRes parseResponse(String response) {
        try {
            return objectMapper.readValue(stripCodeFence(response), WritingAssistRes.class);
        } catch (Exception e) {
            throw new AiException(AiErrorCode.AI_RESPONSE_PARSE_FAIL,
                    "[AiWritingAssistServiceImpl#parseResponse] failed to parse response");
        }
    }

    /**
     * LLM이 JSON 응답을 마크다운 코드블록으로 감싼 경우 코드블록 문법을 제거합니다.
     *
     * @param response LLM이 반환한 원본 응답
     * @return JSON 파싱 가능한 응답 문자열
     */
    private String stripCodeFence(String response) {
        String trimmed = response == null ? "" : response.trim();
        if (trimmed.startsWith("```json")) {
            return trimmed.substring(7, trimmed.length() - 3).trim();
        }
        if (trimmed.startsWith("```")) {
            return trimmed.substring(3, trimmed.length() - 3).trim();
        }
        return trimmed;
    }

    /**
     * 선택 입력값이 비어 있을 때 프롬프트에 사용할 기본값을 반환합니다.
     *
     * @param value 사용자 입력값
     * @param defaultValue 기본값
     * @return 비어 있지 않은 사용자 입력값 또는 기본값
     */
    private String valueOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
