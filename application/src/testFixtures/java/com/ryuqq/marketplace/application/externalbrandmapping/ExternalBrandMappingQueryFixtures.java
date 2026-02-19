package com.ryuqq.marketplace.application.externalbrandmapping;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.query.ExternalBrandMappingSearchParams;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.response.ExternalBrandMappingPageResult;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.response.ExternalBrandMappingResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;

/**
 * ExternalBrandMapping Application Query 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ExternalBrandMappingQueryFixtures {

    private ExternalBrandMappingQueryFixtures() {}

    // ===== ExternalBrandMappingSearchParams Fixtures =====

    public static ExternalBrandMappingSearchParams searchParams() {
        return new ExternalBrandMappingSearchParams(
                null, List.of(), null, null, defaultCommonSearchParams());
    }

    public static ExternalBrandMappingSearchParams searchParams(long externalSourceId) {
        return new ExternalBrandMappingSearchParams(
                externalSourceId, List.of(), null, null, defaultCommonSearchParams());
    }

    public static ExternalBrandMappingSearchParams searchParams(int page, int size) {
        return new ExternalBrandMappingSearchParams(
                null, List.of(), null, null, commonSearchParams(page, size));
    }

    public static ExternalBrandMappingSearchParams searchParams(
            long externalSourceId, List<String> statuses, String searchField, String searchWord) {
        return new ExternalBrandMappingSearchParams(
                externalSourceId, statuses, searchField, searchWord, defaultCommonSearchParams());
    }

    public static ExternalBrandMappingSearchParams searchParamsWithStatusFilter(String status) {
        return new ExternalBrandMappingSearchParams(
                null, List.of(status), null, null, defaultCommonSearchParams());
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", page, size);
    }

    // ===== ExternalBrandMappingResult Fixtures =====

    public static ExternalBrandMappingResult mappingResult(Long id) {
        return new ExternalBrandMappingResult(
                id,
                1L,
                "BR001",
                "외부 브랜드 A",
                100L,
                "ACTIVE",
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    public static ExternalBrandMappingResult mappingResult(
            Long id, Long externalSourceId, String externalBrandCode, Long internalBrandId) {
        return new ExternalBrandMappingResult(
                id,
                externalSourceId,
                externalBrandCode,
                externalBrandCode + " Brand",
                internalBrandId,
                "ACTIVE",
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    // ===== ExternalBrandMappingPageResult Fixtures =====

    public static ExternalBrandMappingPageResult pageResult() {
        List<ExternalBrandMappingResult> results = List.of(mappingResult(1L), mappingResult(2L));
        return ExternalBrandMappingPageResult.of(results, 0, 20, 2);
    }

    public static ExternalBrandMappingPageResult pageResult(
            List<ExternalBrandMappingResult> results) {
        return ExternalBrandMappingPageResult.of(results, 0, 20, results.size());
    }

    public static ExternalBrandMappingPageResult emptyPageResult() {
        return ExternalBrandMappingPageResult.of(List.of(), PageMeta.empty(20));
    }
}
