package com.ryuqq.marketplace.adapter.in.rest.notice;

import com.ryuqq.marketplace.adapter.in.rest.notice.dto.query.SearchNoticeCategoriesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.notice.dto.response.NoticeCategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.notice.dto.response.NoticeFieldApiResponse;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryPageResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeFieldResult;
import java.time.Instant;
import java.util.List;

/**
 * Notice API 테스트 Fixtures.
 *
 * <p>Notice REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class NoticeApiFixtures {

    private NoticeApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_CODE = "CLOTHING";
    public static final String DEFAULT_NAME_KO = "의류";
    public static final String DEFAULT_NAME_EN = "Clothing";
    public static final String DEFAULT_TARGET_CATEGORY_GROUP = "CLOTHING";

    // ===== SearchNoticeCategoriesApiRequest =====

    public static SearchNoticeCategoriesApiRequest searchRequest() {
        return new SearchNoticeCategoriesApiRequest(null, null, null, null, null, 0, 20);
    }

    public static SearchNoticeCategoriesApiRequest searchRequest(int page, int size) {
        return new SearchNoticeCategoriesApiRequest(
                null, null, null, "createdAt", "DESC", page, size);
    }

    public static SearchNoticeCategoriesApiRequest searchRequest(
            Boolean active, String searchField, String searchWord) {
        return new SearchNoticeCategoriesApiRequest(
                active, searchField, searchWord, "createdAt", "DESC", 0, 20);
    }

    public static SearchNoticeCategoriesApiRequest searchRequest(
            Boolean active, String searchField, String searchWord, int page, int size) {
        return new SearchNoticeCategoriesApiRequest(
                active, searchField, searchWord, "createdAt", "DESC", page, size);
    }

    // ===== NoticeCategoryResult (Application) =====

    public static NoticeCategoryResult noticeCategoryResult(Long id) {
        Instant now = Instant.parse("2025-02-10T01:30:00Z");
        return new NoticeCategoryResult(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_TARGET_CATEGORY_GROUP,
                true,
                List.of(),
                now);
    }

    public static NoticeCategoryResult noticeCategoryResult(Long id, String code, boolean active) {
        Instant now = Instant.parse("2025-02-10T01:30:00Z");
        return new NoticeCategoryResult(
                id, code, "테스트 카테고리", "Test Category", "CLOTHING", active, List.of(), now);
    }

    public static NoticeCategoryResult noticeCategoryResultWithFields(Long id) {
        Instant now = Instant.parse("2025-02-10T01:30:00Z");
        List<NoticeFieldResult> fields =
                List.of(
                        new NoticeFieldResult(1L, "MATERIAL", "소재", true, 1),
                        new NoticeFieldResult(2L, "ORIGIN", "원산지", true, 2));
        return new NoticeCategoryResult(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_TARGET_CATEGORY_GROUP,
                true,
                fields,
                now);
    }

    public static List<NoticeCategoryResult> noticeCategoryResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> noticeCategoryResult((long) i, "CODE_" + i, true))
                .toList();
    }

    public static NoticeCategoryPageResult pageResult(int count, int page, int size) {
        List<NoticeCategoryResult> results = noticeCategoryResults(count);
        return NoticeCategoryPageResult.of(results, page, size, count);
    }

    public static NoticeCategoryPageResult emptyPageResult() {
        return NoticeCategoryPageResult.of(List.of(), 0, 20, 0);
    }

    // ===== NoticeFieldResult =====

    public static NoticeFieldResult noticeFieldResult(Long id) {
        return new NoticeFieldResult(id, "FIELD_" + id, "필드 " + id, true, id.intValue());
    }

    public static NoticeFieldResult optionalNoticeFieldResult(Long id) {
        return new NoticeFieldResult(id, "OPTIONAL_" + id, "선택 필드 " + id, false, id.intValue());
    }

    // ===== NoticeCategoryApiResponse =====

    public static NoticeCategoryApiResponse apiResponse(Long id) {
        return new NoticeCategoryApiResponse(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_TARGET_CATEGORY_GROUP,
                true,
                List.of(),
                "2025-02-10T10:30:00+09:00");
    }

    public static NoticeCategoryApiResponse apiResponse(Long id, String code) {
        return new NoticeCategoryApiResponse(
                id,
                code,
                "테스트 카테고리",
                "Test Category",
                "CLOTHING",
                true,
                List.of(),
                "2025-02-10T10:30:00+09:00");
    }

    public static NoticeCategoryApiResponse apiResponseWithFields(Long id) {
        List<NoticeFieldApiResponse> fields =
                List.of(
                        new NoticeFieldApiResponse(1L, "MATERIAL", "소재", true, 1),
                        new NoticeFieldApiResponse(2L, "ORIGIN", "원산지", true, 2));
        return new NoticeCategoryApiResponse(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_TARGET_CATEGORY_GROUP,
                true,
                fields,
                "2025-02-10T10:30:00+09:00");
    }

    // ===== NoticeFieldApiResponse =====

    public static NoticeFieldApiResponse fieldApiResponse(Long id) {
        return new NoticeFieldApiResponse(id, "FIELD_" + id, "필드 " + id, true, id.intValue());
    }

    public static NoticeFieldApiResponse optionalFieldApiResponse(Long id) {
        return new NoticeFieldApiResponse(
                id, "OPTIONAL_" + id, "선택 필드 " + id, false, id.intValue());
    }
}
