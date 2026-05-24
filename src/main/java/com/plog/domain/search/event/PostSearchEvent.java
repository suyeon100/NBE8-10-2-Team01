package com.plog.domain.search.event;

/**
 * 게시글 검색 인덱스 동기화를 요청하는 이벤트입니다.
 * <p>
 * 게시글 트랜잭션 내부에서는 Elasticsearch를 직접 호출하지 않고 이 이벤트만 발행하며,
 * 실제 색인 작업은 트랜잭션 커밋 이후 이벤트 리스너에서 처리합니다.
 *
 * @param postId 동기화 대상 게시글 ID
 * @param type 동기화 작업 종류
 * @author suyeon
 * @since 2026-05-24
 */
public record PostSearchEvent(
        Long postId,
        PostSearchEventType type
) {
}
