package com.plog.domain.search.event;

import com.plog.domain.post.entity.Post;
import com.plog.domain.post.entity.PostStatus;
import com.plog.domain.post.repository.PostRepository;
import com.plog.domain.search.service.PostSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 게시글 검색 인덱스 동기화 이벤트를 처리하는 리스너입니다.
 * <p>
 * DB 트랜잭션이 성공적으로 커밋된 이후 Elasticsearch 색인 작업을 비동기로 수행하여,
 * 롤백 데이터가 검색 인덱스에 반영되는 문제와 DB 커넥션 점유 시간을 줄입니다.
 *
 * @author suyeon
 * @since 2026-05-24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostSearchEventListener {

    private final PostRepository postRepository;
    private final PostSearchService postSearchService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PostSearchEvent event) {
        if (event.type() == PostSearchEventType.DELETE) {
            postSearchService.delete(event.postId());
            return;
        }

        postRepository.findByIdWithMember(event.postId())
                .ifPresentOrElse(this::indexIfPublished,
                        () -> log.warn("[PostSearchEventListener#handle] post not found. postId={}", event.postId()));
    }

    private void indexIfPublished(Post post) {
        if (post.getStatus() == PostStatus.PUBLISHED) {
            postSearchService.index(post);
            return;
        }

        postSearchService.delete(post.getId());
    }
}
