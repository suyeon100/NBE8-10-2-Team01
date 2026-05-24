package com.plog.domain.search.repository;

import com.plog.domain.search.document.PostSearchDocument;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 게시글 검색 문서에 대한 Elasticsearch 데이터 액세스 인터페이스입니다.
 * <p>
 * Spring Data Elasticsearch를 기반으로 기본적인 색인 저장, 삭제 기능을 제공하며,
 * 제목과 본문, 요약글을 대상으로 하는 게시글 검색 쿼리를 정의합니다.
 *
 * <p><b>상속 정보:</b><br>
 * {@code ElasticsearchRepository<PostSearchDocument, Long>}을 상속받아 표준 Elasticsearch Repository 기능을 사용합니다.
 *
 * <p><b>빈 관리:</b><br>
 * Spring Data Elasticsearch 인프라스트럭처에 의해 런타임에 구현체가 생성되고 스프링 빈으로 등록됩니다.
 *
 * @author suyeon
 * @since 2026-05-24
 */
public interface PostSearchRepository extends ElasticsearchRepository<PostSearchDocument, Long> {

    /**
     * 발행된 게시글 중 키워드가 제목, 본문, 요약글에 포함된 문서를 검색합니다.
     * <p>
     * 제목 필드에는 가중치를 부여하여 제목이 일치하는 게시글이 더 높은 점수를 받도록 합니다.
     *
     * @param keyword 검색 키워드
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
                      "status": "PUBLISHED"
                    }
                  }
                ]
              }
            }
            """)
    Page<PostSearchDocument> searchPublished(String keyword, Pageable pageable);
}
