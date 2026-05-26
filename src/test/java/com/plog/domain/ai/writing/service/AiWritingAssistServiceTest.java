package com.plog.domain.ai.writing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plog.domain.ai.client.LlmClient;
import com.plog.domain.ai.writing.dto.WritingAssistFollowUpReq;
import com.plog.domain.ai.writing.dto.WritingAssistFollowUpRes;
import com.plog.domain.ai.writing.dto.WritingAssistReq;
import com.plog.domain.ai.writing.dto.WritingAssistRes;
import com.plog.global.exception.exceptions.AiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AiWritingAssistServiceTest {

    @InjectMocks
    private AiWritingAssistServiceImpl aiWritingAssistService;

    @Mock
    private LlmClient llmClient;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("AI 글쓰기 보조 요청 시 LLM 응답을 DTO로 변환한다")
    void assistSuccess() {
        // [Given]
        WritingAssistReq req = new WritingAssistReq(
                "Elasticsearch 색인 정합성 개선기",
                "DB 트랜잭션과 Elasticsearch 색인 문제를 다룬 초안",
                "주니어 백엔드 개발자",
                "문제 해결 과정을 공유"
        );
        String llmResponse = """
                {
                  "direction": "문제 상황에서 개선 결과까지 순서대로 전개하세요.",
                  "outline": ["문제 상황", "원인 분석", "개선 결과"],
                  "improvementPoints": ["변경 전후 코드 비교", "트랜잭션 롤백 예시"],
                  "readerQuestions": ["왜 이벤트 기반으로 바꿨나요?", "롤백 시 어떤 문제가 있었나요?"],
                  "missingContext": ["변경 전 코드", "테스트 또는 재현 시나리오"]
                }
                """;

        given(llmClient.generate(org.mockito.ArgumentMatchers.anyString())).willReturn(llmResponse);

        // [When]
        WritingAssistRes result = aiWritingAssistService.assist(req);

        // [Then]
        assertThat(result.direction()).contains("문제 상황");
        assertThat(result.outline()).containsExactly("문제 상황", "원인 분석", "개선 결과");
        assertThat(result.improvementPoints()).contains("변경 전후 코드 비교");
        assertThat(result.readerQuestions()).contains("왜 이벤트 기반으로 바꿨나요?");
        assertThat(result.missingContext()).contains("변경 전 코드");

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(llmClient).generate(promptCaptor.capture());
        assertThat(promptCaptor.getValue()).contains("자동으로 다시 작성하지 말고");
        assertThat(promptCaptor.getValue()).contains("독자가 궁금해할 질문");
        assertThat(promptCaptor.getValue()).contains("추가하면 좋은 배경 설명");
        assertThat(promptCaptor.getValue()).contains(req.title());
    }

    @Test
    @DisplayName("LLM 응답이 JSON 형식이 아니면 AiException이 발생한다")
    void assistFailInvalidResponse() {
        // [Given]
        WritingAssistReq req = new WritingAssistReq("제목", "본문", null, null);
        given(llmClient.generate(org.mockito.ArgumentMatchers.anyString())).willReturn("not json");

        // [When & Then]
        assertThatThrownBy(() -> aiWritingAssistService.assist(req))
                .isInstanceOf(AiException.class);
    }

    @Test
    @DisplayName("AI 글쓰기 보조 후속 질문 시 LLM 답변을 반환한다")
    void followUpSuccess() {
        // [Given]
        WritingAssistFollowUpReq req = new WritingAssistFollowUpReq(
                "Elasticsearch 색인 정합성 개선기",
                "DB 트랜잭션과 Elasticsearch 색인 문제를 다룬 초안",
                "변경 전후 코드 비교를 추가하세요.",
                "변경 전후 코드는 어떤 흐름으로 보여주면 좋을까?"
        );

        given(llmClient.generate(org.mockito.ArgumentMatchers.anyString()))
                .willReturn("문제 상황, 변경 전 코드, 변경 후 코드, 결과 순서로 보여주는 것이 좋습니다.");

        // [When]
        WritingAssistFollowUpRes result = aiWritingAssistService.followUp(req);

        // [Then]
        assertThat(result.answer()).contains("변경 전 코드");

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(llmClient).generate(promptCaptor.capture());
        assertThat(promptCaptor.getValue()).contains(req.selectedSuggestion());
        assertThat(promptCaptor.getValue()).contains(req.question());
    }
}
