package com.plog.domain.ai.writing.controller;

import com.plog.domain.ai.writing.dto.WritingAssistFollowUpReq;
import com.plog.domain.ai.writing.dto.WritingAssistFollowUpRes;
import com.plog.domain.ai.writing.dto.WritingAssistReq;
import com.plog.domain.ai.writing.dto.WritingAssistRes;
import com.plog.domain.ai.writing.service.AiWritingAssistService;
import com.plog.testUtil.SecurityTestConfig;
import com.plog.testUtil.WebMvcTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiWritingAssistController.class)
@Import(SecurityTestConfig.class)
@ActiveProfiles("test")
class AiWritingAssistControllerTest extends WebMvcTestSupport {

    @MockitoBean
    private AiWritingAssistService aiWritingAssistService;

    @Test
    @DisplayName("AI 글쓰기 보조 요청 시 방향성, 목차, 보완 포인트, 독자 질문, 누락 맥락을 반환한다")
    void assistWritingSuccess() throws Exception {
        // [Given]
        WritingAssistReq req = new WritingAssistReq(
                "Elasticsearch 색인 정합성 개선기",
                "초안 내용",
                "주니어 백엔드 개발자",
                "문제 해결 과정 공유"
        );
        WritingAssistRes res = new WritingAssistRes(
                "문제 상황부터 개선 결과까지 전개하세요.",
                List.of("문제 상황", "개선 과정"),
                List.of("변경 전후 코드 비교 추가"),
                List.of("왜 이벤트 기반으로 바꿨나요?"),
                List.of("변경 전 코드 예시")
        );

        given(aiWritingAssistService.assist(any(WritingAssistReq.class))).willReturn(res);

        // [When]
        ResultActions resultActions = mockMvc.perform(post("/api/ai/posts/writing-assist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print());

        // [Then]
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.direction").value("문제 상황부터 개선 결과까지 전개하세요."))
                .andExpect(jsonPath("$.data.outline[0]").value("문제 상황"))
                .andExpect(jsonPath("$.data.improvementPoints[0]").value("변경 전후 코드 비교 추가"))
                .andExpect(jsonPath("$.data.readerQuestions[0]").value("왜 이벤트 기반으로 바꿨나요?"))
                .andExpect(jsonPath("$.data.missingContext[0]").value("변경 전 코드 예시"))
                .andExpect(jsonPath("$.message").value("AI 글쓰기 보조 성공"));

        verify(aiWritingAssistService).assist(any(WritingAssistReq.class));
    }

    @Test
    @DisplayName("AI 글쓰기 보조 요청 시 제목이 없으면 400 Bad Request를 반환한다")
    void assistWritingFailBlankTitle() throws Exception {
        // [Given]
        WritingAssistReq req = new WritingAssistReq("", "초안 내용", null, null);

        // [When]
        ResultActions resultActions = mockMvc.perform(post("/api/ai/posts/writing-assist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print());

        // [Then]
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("AI 글쓰기 보조 후속 질문 시 답변을 반환한다")
    void followUpWritingAssistSuccess() throws Exception {
        // [Given]
        WritingAssistFollowUpReq req = new WritingAssistFollowUpReq(
                "Elasticsearch 색인 정합성 개선기",
                "초안 내용",
                "변경 전후 코드 비교 추가",
                "어떤 흐름으로 설명하면 좋을까?"
        );
        WritingAssistFollowUpRes res = new WritingAssistFollowUpRes(
                "문제 상황, 변경 전 코드, 변경 후 코드, 결과 순서로 설명하세요."
        );

        given(aiWritingAssistService.followUp(any(WritingAssistFollowUpReq.class))).willReturn(res);

        // [When]
        ResultActions resultActions = mockMvc.perform(post("/api/ai/posts/writing-assist/follow-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print());

        // [Then]
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.answer").value("문제 상황, 변경 전 코드, 변경 후 코드, 결과 순서로 설명하세요."))
                .andExpect(jsonPath("$.message").value("AI 글쓰기 보조 후속 질문 성공"));

        verify(aiWritingAssistService).followUp(any(WritingAssistFollowUpReq.class));
    }

    @Test
    @DisplayName("AI 글쓰기 보조 후속 질문 시 질문이 없으면 400 Bad Request를 반환한다")
    void followUpWritingAssistFailBlankQuestion() throws Exception {
        // [Given]
        WritingAssistFollowUpReq req = new WritingAssistFollowUpReq(
                "제목",
                "초안 내용",
                "선택한 제안",
                ""
        );

        // [When]
        ResultActions resultActions = mockMvc.perform(post("/api/ai/posts/writing-assist/follow-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print());

        // [Then]
        resultActions.andExpect(status().isBadRequest());
    }
}
