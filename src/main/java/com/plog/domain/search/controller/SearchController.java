package com.plog.domain.search.controller;

import com.plog.domain.post.dto.PostListRes;
import com.plog.domain.search.service.PostSearchService;
import com.plog.global.response.CommonResponse;
import com.plog.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 검색 관련 HTTP 요청을 처리하는 컨트롤러 클래스입니다.
 * <p>
 * Elasticsearch 기반 검색 기능을 게시글 도메인 컨트롤러와 분리하여 관리하며,
 * 향후 사용자, 해시태그, 통합 검색 등으로 확장할 수 있도록 검색 API의 진입점을 담당합니다.
 *
 * <p><b>주요 생성자:</b><br>
 * {@code SearchController(PostSearchService postSearchService)} <br>
 * 생성자 주입을 통해 게시글 검색 서비스 빈을 주입받습니다.
 *
 * <p><b>빈 관리:</b><br>
 * {@code @RestController}를 사용하여 스프링 컨테이너의 빈으로 관리되며,
 * 모든 메서드의 반환값은 JSON으로 직렬화됩니다.
 *
 * @author suyeon
 * @since 2026-05-24
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final PostSearchService postSearchService;

    /**
     * 게시글을 키워드로 검색합니다.
     * <p>
     * 제목, 본문, 요약글을 대상으로 검색하며, 검색 결과는 게시글 목록 응답과 같은 Slice 구조로 반환합니다.
     *
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 검색된 게시글 목록과 성공 메시지를 포함한 공통 응답 객체
     */
    @GetMapping("/posts")
    public ResponseEntity<Response<Slice<PostListRes>>> searchPosts(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Slice<PostListRes> posts = postSearchService.search(keyword, pageable);
        return ResponseEntity.ok(CommonResponse.success(posts, "게시글 검색 성공"));
    }
}
