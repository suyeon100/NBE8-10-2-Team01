package com.plog.domain.search.service;

import com.plog.domain.post.dto.PostListRes;
import com.plog.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

/**
 * 게시글 검색 색인과 조회 비즈니스 로직을 정의하는 인터페이스입니다.
 * <p>
 * 게시글 생성, 수정, 삭제 시 Elasticsearch 검색 인덱스를 동기화하고,
 * 클라이언트의 키워드 검색 요청을 게시글 목록 응답 형태로 반환하는 기능을 정의합니다.
 *
 * @author suyeon
 * @since 2026-05-24
 */
public interface PostSearchService {

    /**
     * 게시글 엔티티를 Elasticsearch 검색 문서로 저장합니다.
     *
     * @param post 색인할 게시글 엔티티
     */
    void index(Post post);

    /**
     * 게시글 ID에 해당하는 Elasticsearch 검색 문서를 삭제합니다.
     *
     * @param postId 삭제할 게시글 ID
     */
    void delete(Long postId);

    /**
     * 키워드로 발행된 게시글을 검색합니다.
     * <p><b>실행 로직:</b><br>
     * 1. 전달받은 키워드로 게시글 검색 인덱스를 조회합니다. <br>
     * 2. 제목, 본문, 요약글을 대상으로 검색하며 발행된 게시글만 반환합니다. <br>
     * 3. 검색된 문서를 응답용 DTO({@link PostListRes})로 변환 후 페이징하여 반환합니다.
     *
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 검색 결과 게시글 목록 Slice
     */
    Slice<PostListRes> search(String keyword, Pageable pageable);
}
