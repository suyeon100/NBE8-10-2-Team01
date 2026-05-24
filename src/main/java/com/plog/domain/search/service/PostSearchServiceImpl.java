package com.plog.domain.search.service;

import com.plog.domain.post.dto.PostListRes;
import com.plog.domain.post.entity.Post;
import com.plog.domain.search.document.PostSearchDocument;
import com.plog.domain.search.repository.PostSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link PostSearchService} 인터페이스의 기본 구현체입니다.
 * <p>
 * Spring Data Elasticsearch Repository를 통해 게시글 검색 문서를 저장, 삭제, 조회하며,
 * Elasticsearch 장애가 원본 게시글 처리 흐름을 막지 않도록 예외를 로깅한 뒤 안전한 기본 응답을 반환합니다.
 *
 * <p><b>외부 모듈:</b><br>
 * Spring Data Elasticsearch Repository를 통해 검색 인덱스에 접근합니다.
 *
 * @author suyeon
 * @since 2026-05-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostSearchServiceImpl implements PostSearchService {

    private final PostSearchRepository postSearchRepository;

    @Override
    public void index(Post post) {
        try {
            postSearchRepository.save(PostSearchDocument.from(post));
        } catch (Exception e) {
            log.warn("[PostSearchServiceImpl#index] failed. postId={}, cause={}", post.getId(), e.getMessage());
        }
    }

    @Override
    public void delete(Long postId) {
        try {
            postSearchRepository.deleteById(postId);
        } catch (Exception e) {
            log.warn("[PostSearchServiceImpl#delete] failed. postId={}, cause={}", postId, e.getMessage());
        }
    }

    @Override
    public Slice<PostListRes> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return new SliceImpl<>(List.of(), pageable, false);
        }

        try {
            return postSearchRepository.searchPublished(keyword.trim(), pageable)
                    .map(PostSearchDocument::toPostListRes);
        } catch (Exception e) {
            log.warn("[PostSearchServiceImpl#search] failed. keyword={}, cause={}", keyword, e.getMessage());
            return new SliceImpl<>(List.of(), pageable, false);
        }
    }
}
