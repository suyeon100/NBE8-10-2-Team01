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
 *
 * @author suyeon
 * @since 2026-05-24
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final PostSearchService postSearchService;

    @GetMapping("/posts")
    public ResponseEntity<Response<Slice<PostListRes>>> searchPosts(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Slice<PostListRes> posts = postSearchService.search(keyword, pageable);
        return ResponseEntity.ok(CommonResponse.success(posts, "게시글 검색 성공"));
    }
}
