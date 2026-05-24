package com.plog.domain.search.service;

import com.plog.domain.post.dto.PostListRes;
import com.plog.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

/**
 * 게시글 검색 색인과 조회 비즈니스 로직을 정의하는 인터페이스입니다.
 *
 * @author suyeon
 * @since 2026-05-24
 */
public interface PostSearchService {

    void index(Post post);

    void delete(Long postId);

    Slice<PostListRes> search(String keyword, Pageable pageable);
}
