package com.plog.domain.search.service;

import com.plog.domain.post.dto.PostListRes;
import com.plog.domain.post.entity.Post;
import com.plog.domain.post.entity.PostStatus;
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
            return postSearchRepository.searchByKeywordAndStatus(keyword.trim(), PostStatus.PUBLISHED.name(), pageable)
                    .map(PostSearchDocument::toPostListRes);
        } catch (Exception e) {
            log.warn("[PostSearchServiceImpl#search] failed. keyword={}, cause={}", keyword, e.getMessage());
            return new SliceImpl<>(List.of(), pageable, false);
        }
    }
}
