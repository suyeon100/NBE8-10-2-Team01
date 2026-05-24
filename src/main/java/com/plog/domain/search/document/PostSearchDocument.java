package com.plog.domain.search.document;

import com.plog.domain.hashtag.entity.PostHashTag;
import com.plog.domain.member.entity.Member;
import com.plog.domain.post.dto.PostListRes;
import com.plog.domain.post.entity.Post;
import com.plog.domain.post.entity.PostStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Elasticsearch에 저장되는 게시글 검색 전용 문서 클래스입니다.
 * <p>
 * MySQL에 저장되는 {@link Post} 엔티티를 직접 검색하지 않고, 검색에 필요한 필드만 별도 인덱스에 저장하여
 * 제목, 본문, 요약글 기반의 전문 검색을 수행합니다.
 *
 * <p><b>색인 정보:</b><br>
 * {@code posts} 인덱스를 사용하며, 애플리케이션 시작 시 Elasticsearch 서버 상태에 의해 테스트와 실행이
 * 실패하지 않도록 자동 인덱스 생성은 비활성화합니다.
 *
 * <p><b>외부 모듈:</b><br>
 * Spring Data Elasticsearch의 {@link Document}, {@link Field} 어노테이션을 사용합니다.
 *
 * @author suyeon
 * @since 2026-05-24
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Document(indexName = "posts", createIndex = false)
public class PostSearchDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Text)
    private String summary;

    @Field(type = FieldType.Keyword)
    private PostStatus status;

    @Field(type = FieldType.Integer)
    private int viewCount;

    @Field(type = FieldType.Date)
    private LocalDateTime createDate;

    @Field(type = FieldType.Date)
    private LocalDateTime modifyDate;

    @Field(type = FieldType.Keyword)
    private List<String> hashtags;

    @Field(type = FieldType.Keyword)
    private String thumbnail;

    @Field(type = FieldType.Keyword)
    private String nickname;

    @Field(type = FieldType.Keyword)
    private String profileImage;

    /**
     * JPA 게시글 엔티티를 Elasticsearch 검색 문서로 변환합니다.
     * <p>
     * 게시글 목록 응답에 필요한 작성자, 해시태그, 썸네일 정보를 함께 색인하여 검색 결과 반환 시
     * 추가 조회 없이 목록 DTO를 구성할 수 있도록 합니다.
     *
     * @param post 변환 대상 게시글 엔티티
     * @return Elasticsearch에 저장할 게시글 검색 문서
     */
    public static PostSearchDocument from(Post post) {
        Member member = post.getMember();

        return PostSearchDocument.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .summary(post.getSummary())
                .status(post.getStatus())
                .viewCount(post.getViewCount())
                .createDate(post.getCreateDate())
                .modifyDate(post.getModifyDate())
                .hashtags(post.getPostHashTags().stream()
                        .map(PostHashTag::getDisplayName)
                        .toList())
                .thumbnail(post.getThumbnail())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage() != null ? member.getProfileImage().getAccessUrl() : null)
                .build();
    }

    /**
     * Elasticsearch 검색 문서를 게시글 목록 응답 DTO로 변환합니다.
     *
     * @return 클라이언트에 반환할 게시글 목록 응답 DTO
     */
    public PostListRes toPostListRes() {
        return new PostListRes(
                id,
                title,
                summary,
                viewCount,
                createDate,
                modifyDate,
                hashtags,
                thumbnail,
                nickname,
                profileImage
        );
    }
}
