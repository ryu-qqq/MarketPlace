package com.ryuqq.marketplace.application.externalcategorymapping;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.query.ExternalCategoryMappingSearchParams;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.response.ExternalCategoryMappingPageResult;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.response.ExternalCategoryMappingResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;

/**
 * ExternalCategoryMapping Application Query 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ExternalCategoryMappingQueryFixtures {

    private ExternalCategoryMappingQueryFixtures() {}

    // ===== ExternalCategoryMappingSearchParams Fixtures =====

    public static ExternalCategoryMappingSearchParams searchParams() {
        return new ExternalCategoryMappingSearchParams(
                null, List.of(), null, null, defaultCommonSearchParams());
    }

    public static ExternalCategoryMappingSearchParams searchParams(long externalSourceId) {
        return new ExternalCategoryMappingSearchParams(
                externalSourceId, List.of(), null, null, defaultCommonSearchParams());
    }

    public static ExternalCategoryMappingSearchParams searchParams(int page, int size) {
        return new ExternalCategoryMappingSearchParams(
                null, List.of(), null, null, commonSearchParams(page, size));
    }

    public static ExternalCategoryMappingSearchParams searchParams(
            long externalSourceId, List<String> statuses, String searchField, String searchWord) {
        return new ExternalCategoryMappingSearchParams(
                externalSourceId, statuses, searchField, searchWord, defaultCommonSearchParams());
    }

    public static ExternalCategoryMappingSearchParams searchParamsWithStatusFilter(String status) {
        return new ExternalCategoryMappingSearchParams(
                null, List.of(status), null, null, defaultCommonSearchParams());
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", page, size);
    }

    // ===== ExternalCategoryMappingResult Fixtures =====

    public static ExternalCategoryMappingResult mappingResult(Long id) {
        return new ExternalCategoryMappingResult(
                id,
                1L,
                "CAT_SHOES_001",
                "외부 카테고리 신발",
                100L,
                "ACTIVE",
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    public static ExternalCategoryMappingResult mappingResult(
            Long id, Long externalSourceId, String externalCategoryCode, Long internalCategoryId) {
        return new ExternalCategoryMappingResult(
                id,
                externalSourceId,
                externalCategoryCode,
                externalCategoryCode + " Category",
                internalCategoryId,
                "ACTIVE",
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    // ===== ExternalCategoryMappingPageResult Fixtures =====

    public static ExternalCategoryMappingPageResult pageResult() {
        List<ExternalCategoryMappingResult> results = List.of(mappingResult(1L), mappingResult(2L));
        return ExternalCategoryMappingPageResult.of(results, 0, 20, 2);
    }

    public static ExternalCategoryMappingPageResult pageResult(
            List<ExternalCategoryMappingResult> results) {
        return ExternalCategoryMappingPageResult.of(results, 0, 20, results.size());
    }

    public static ExternalCategoryMappingPageResult emptyPageResult() {
        return ExternalCategoryMappingPageResult.of(List.of(), PageMeta.empty(20));
    }
}
