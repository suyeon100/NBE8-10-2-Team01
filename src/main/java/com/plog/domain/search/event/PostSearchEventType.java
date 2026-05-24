package com.plog.domain.search.event;

/**
 * 게시글 검색 인덱스 동기화 작업의 종류를 나타내는 enum 클래스입니다.
 *
 * @author suyeon
 * @since 2026-05-24
 */
public enum PostSearchEventType {
    INDEX,
    DELETE
}
