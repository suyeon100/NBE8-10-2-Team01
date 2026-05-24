package com.plog.domain.search.repository;

import com.plog.domain.search.document.PostSearchDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 게시글 검색 문서에 대한 Elasticsearch 데이터 액세스 인터페이스입니다.
 *
 * @author suyeon
 * @since 2026-05-24
 */
public interface PostSearchRepository extends ElasticsearchRepository<PostSearchDocument, Long> {

    /**
     * 특정 상태의 게시글 중 키워드가 제목, 본문, 요약글에 포함된 문서를 검색합니다.
     * <p>
     * 제목 필드에는 가중치를 부여하여 제목이 일치하는 게시글이 더 높은 점수를 받도록 합니다.
     *
     * @param keyword 검색 키워드
     * @param status 게시글 상태
     * @param pageable 페이징 정보
     * @return 검색 조건과 일치하는 게시글 검색 문서 페이지
     */
    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "multi_match": {
                      "query": "?0",
                      "fields": ["title^2", "content", "summary"]
                    }
                  }
                ],
                "filter": [
                  {
                    "term": {
                      "status": "?1"
                    }
                  }
                ]
              }
            }
            """)
    Page<PostSearchDocument> searchByKeywordAndStatus(String keyword, String status, Pageable pageable);
}
