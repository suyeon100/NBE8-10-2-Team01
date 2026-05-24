package com.plog.domain.search.controller;

import com.plog.domain.post.dto.PostListRes;
import com.plog.domain.search.service.PostSearchService;
import com.plog.testUtil.SecurityTestConfig;
import com.plog.testUtil.WebMvcTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 검색 컨트롤러의 웹 계층을 검증하는 테스트 클래스입니다.
 *
 * @author suyeon
 * @since 2026-05-24
 */
@WebMvcTest(SearchController.class)
@Import(SecurityTestConfig.class)
@ActiveProfiles("test")
class SearchControllerTest extends WebMvcTestSupport {

    @MockitoBean
    private PostSearchService postSearchService;

    @Test
    @DisplayName("게시글 검색 시 키워드와 페이징 정보를 전달하고 Slice 리스트를 반환한다")
    void searchPostsSuccess() throws Exception {
        // [Given]
        Pageable pageable = PageRequest.of(0, 10);
        PostListRes res = new PostListRes(
                1L,
                "Elasticsearch 적용",
                "검색 기능 요약",
                0,
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of("search"),
                null,
                "nickname",
                null
        );
        Slice<PostListRes> sliceResponse = new SliceImpl<>(List.of(res), pageable, false);

        given(postSearchService.search(eq("elastic"), any(Pageable.class)))
                .willReturn(sliceResponse);

        // [When]
        ResultActions resultActions = mockMvc.perform(get("/api/search/posts")
                        .param("keyword", "elastic")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // [Then]
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].title").value("Elasticsearch 적용"))
                .andExpect(jsonPath("$.message").value("게시글 검색 성공"));

        verify(postSearchService).search(eq("elastic"), any(Pageable.class));
    }
}
