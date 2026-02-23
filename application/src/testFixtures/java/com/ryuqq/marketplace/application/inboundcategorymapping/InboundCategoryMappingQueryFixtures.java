package com.ryuqq.marketplace.application.inboundcategorymapping;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.query.InboundCategoryMappingSearchParams;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.response.InboundCategoryMappingPageResult;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.response.InboundCategoryMappingResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;

/**
 * InboundCategoryMapping Application Query 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class InboundCategoryMappingQueryFixtures {

    private InboundCategoryMappingQueryFixtures() {}

    // ===== InboundCategoryMappingSearchParams Fixtures =====

    public static InboundCategoryMappingSearchParams searchParams() {
        return new InboundCategoryMappingSearchParams(
                null, List.of(), null, null, defaultCommonSearchParams());
    }

    public static InboundCategoryMappingSearchParams searchParams(long inboundSourceId) {
        return new InboundCategoryMappingSearchParams(
                inboundSourceId, List.of(), null, null, defaultCommonSearchParams());
    }

    public static InboundCategoryMappingSearchParams searchParams(int page, int size) {
        return new InboundCategoryMappingSearchParams(
                null, List.of(), null, null, commonSearchParams(page, size));
    }

    public static InboundCategoryMappingSearchParams searchParams(
            long inboundSourceId, List<String> statuses, String searchField, String searchWord) {
        return new InboundCategoryMappingSearchParams(
                inboundSourceId, statuses, searchField, searchWord, defaultCommonSearchParams());
    }

    public static InboundCategoryMappingSearchParams searchParamsWithStatusFilter(String status) {
        return new InboundCategoryMappingSearchParams(
                null, List.of(status), null, null, defaultCommonSearchParams());
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", page, size);
    }

    // ===== InboundCategoryMappingResult Fixtures =====

    public static InboundCategoryMappingResult mappingResult(Long id) {
        return new InboundCategoryMappingResult(
                id,
                1L,
                "CAT_SHOES_001",
                "외부 카테고리 신발",
                100L,
                "ACTIVE",
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    public static InboundCategoryMappingResult mappingResult(
            Long id, Long inboundSourceId, String externalCategoryCode, Long internalCategoryId) {
        return new InboundCategoryMappingResult(
                id,
                inboundSourceId,
                externalCategoryCode,
                externalCategoryCode + " Category",
                internalCategoryId,
                "ACTIVE",
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    // ===== InboundCategoryMappingPageResult Fixtures =====

    public static InboundCategoryMappingPageResult pageResult() {
        List<InboundCategoryMappingResult> results = List.of(mappingResult(1L), mappingResult(2L));
        return InboundCategoryMappingPageResult.of(results, 0, 20, 2);
    }

    public static InboundCategoryMappingPageResult pageResult(
            List<InboundCategoryMappingResult> results) {
        return InboundCategoryMappingPageResult.of(results, 0, 20, results.size());
    }

    public static InboundCategoryMappingPageResult emptyPageResult() {
        return InboundCategoryMappingPageResult.of(List.of(), PageMeta.empty(20));
    }
}
