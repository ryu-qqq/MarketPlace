package com.ryuqq.marketplace.application.notice;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.notice.dto.query.NoticeCategorySearchParams;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryPageResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeFieldResult;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import java.util.List;

/**
 * Notice Query 테스트 Fixtures.
 *
 * <p>Notice 관련 Query 파라미터 및 Result 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class NoticeQueryFixtures {

    private NoticeQueryFixtures() {}

    // ===== SearchParams Fixtures =====

    public static NoticeCategorySearchParams searchParams() {
        return NoticeCategorySearchParams.of(null, null, null, defaultCommonSearchParams());
    }

    public static NoticeCategorySearchParams searchParams(int page, int size) {
        return NoticeCategorySearchParams.of(null, null, null, commonSearchParams(page, size));
    }

    public static NoticeCategorySearchParams searchParams(Boolean active) {
        return NoticeCategorySearchParams.of(active, null, null, defaultCommonSearchParams());
    }

    public static NoticeCategorySearchParams searchParams(String searchField, String searchWord) {
        return NoticeCategorySearchParams.of(
                null, searchField, searchWord, defaultCommonSearchParams());
    }

    public static NoticeCategorySearchParams searchParams(
            Boolean active, String searchField, String searchWord, int page, int size) {
        return NoticeCategorySearchParams.of(
                active, searchField, searchWord, commonSearchParams(page, size));
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
    }

    // ===== Result Fixtures =====

    public static NoticeCategoryResult noticeCategoryResult(Long id) {
        Instant now = CommonVoFixtures.now();
        return new NoticeCategoryResult(
                id, "CLOTHING", "의류", "Clothing", "CLOTHING", true, List.of(), now);
    }

    public static NoticeCategoryResult noticeCategoryResult(Long id, String code) {
        Instant now = CommonVoFixtures.now();
        return new NoticeCategoryResult(
                id, code, "테스트 카테고리", "Test Category", "CLOTHING", true, List.of(), now);
    }

    public static NoticeCategoryResult noticeCategoryResult(Long id, boolean active) {
        Instant now = CommonVoFixtures.now();
        return new NoticeCategoryResult(
                id, "CLOTHING", "의류", "Clothing", "CLOTHING", active, List.of(), now);
    }

    public static NoticeCategoryResult noticeCategoryResultWithFields(Long id) {
        Instant now = CommonVoFixtures.now();
        List<NoticeFieldResult> fields =
                List.of(
                        new NoticeFieldResult(1L, "MATERIAL", "소재", true, 1),
                        new NoticeFieldResult(2L, "ORIGIN", "원산지", true, 2));
        return new NoticeCategoryResult(
                id, "CLOTHING", "의류", "Clothing", "CLOTHING", true, fields, now);
    }

    // ===== PageResult Fixtures =====

    public static NoticeCategoryPageResult noticeCategoryPageResult() {
        List<NoticeCategoryResult> results =
                List.of(noticeCategoryResult(1L), noticeCategoryResult(2L));
        return NoticeCategoryPageResult.of(results, 0, 20, 2L);
    }

    public static NoticeCategoryPageResult noticeCategoryPageResult(
            int page, int size, long totalCount) {
        List<NoticeCategoryResult> results =
                List.of(noticeCategoryResult(1L), noticeCategoryResult(2L));
        return NoticeCategoryPageResult.of(results, page, size, totalCount);
    }

    public static NoticeCategoryPageResult emptyPageResult() {
        return NoticeCategoryPageResult.of(List.of(), 0, 20, 0L);
    }

    // ===== Field Result Fixtures =====

    public static NoticeFieldResult noticeFieldResult(Long id) {
        return new NoticeFieldResult(id, "FIELD_" + id, "필드 " + id, true, id.intValue());
    }

    public static NoticeFieldResult optionalNoticeFieldResult(Long id) {
        return new NoticeFieldResult(id, "OPTIONAL_" + id, "선택 필드 " + id, false, id.intValue());
    }
}
