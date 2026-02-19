package com.ryuqq.marketplace.application.externalsource;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.externalsource.dto.query.ExternalSourceSearchParams;
import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourcePageResult;
import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourceResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;

/**
 * ExternalSource Application Query 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ExternalSourceQueryFixtures {

    private ExternalSourceQueryFixtures() {}

    // ===== ExternalSourceSearchParams Fixtures =====

    public static ExternalSourceSearchParams searchParams() {
        return new ExternalSourceSearchParams(
                List.of(), List.of(), null, null, defaultCommonSearchParams());
    }

    public static ExternalSourceSearchParams searchParams(int page, int size) {
        return new ExternalSourceSearchParams(
                List.of(), List.of(), null, null, commonSearchParams(page, size));
    }

    public static ExternalSourceSearchParams searchParams(
            List<String> types, List<String> statuses, String searchField, String searchWord) {
        return new ExternalSourceSearchParams(
                types, statuses, searchField, searchWord, defaultCommonSearchParams());
    }

    public static ExternalSourceSearchParams searchParamsWithTypeFilter(String type) {
        return new ExternalSourceSearchParams(
                List.of(type), List.of(), null, null, defaultCommonSearchParams());
    }

    public static ExternalSourceSearchParams searchParamsWithStatusFilter(String status) {
        return new ExternalSourceSearchParams(
                List.of(), List.of(status), null, null, defaultCommonSearchParams());
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", page, size);
    }

    // ===== ExternalSourceResult Fixtures =====

    public static ExternalSourceResult sourceResult(Long id) {
        return new ExternalSourceResult(
                id,
                "SETOF",
                "세토프 레거시",
                "LEGACY",
                "ACTIVE",
                "레거시 Setof 상품 데이터 소스",
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    public static ExternalSourceResult sourceResult(
            Long id, String code, String name, String type) {
        return new ExternalSourceResult(
                id,
                code,
                name,
                type,
                "ACTIVE",
                null,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    // ===== ExternalSourcePageResult Fixtures =====

    public static ExternalSourcePageResult pageResult() {
        List<ExternalSourceResult> results = List.of(sourceResult(1L), sourceResult(2L));
        return ExternalSourcePageResult.of(results, 0, 20, 2);
    }

    public static ExternalSourcePageResult pageResult(List<ExternalSourceResult> results) {
        return ExternalSourcePageResult.of(results, 0, 20, results.size());
    }

    public static ExternalSourcePageResult emptyPageResult() {
        return ExternalSourcePageResult.of(List.of(), PageMeta.empty(20));
    }
}
