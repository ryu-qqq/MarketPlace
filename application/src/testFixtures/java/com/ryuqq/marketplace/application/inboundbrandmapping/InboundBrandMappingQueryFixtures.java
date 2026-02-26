package com.ryuqq.marketplace.application.inboundbrandmapping;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.query.InboundBrandMappingSearchParams;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.response.InboundBrandMappingPageResult;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.response.InboundBrandMappingResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;

/**
 * InboundBrandMapping Application Query 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class InboundBrandMappingQueryFixtures {

    private InboundBrandMappingQueryFixtures() {}

    // ===== InboundBrandMappingSearchParams Fixtures =====

    public static InboundBrandMappingSearchParams searchParams() {
        return new InboundBrandMappingSearchParams(
                null, List.of(), null, null, defaultCommonSearchParams());
    }

    public static InboundBrandMappingSearchParams searchParams(long inboundSourceId) {
        return new InboundBrandMappingSearchParams(
                inboundSourceId, List.of(), null, null, defaultCommonSearchParams());
    }

    public static InboundBrandMappingSearchParams searchParams(int page, int size) {
        return new InboundBrandMappingSearchParams(
                null, List.of(), null, null, commonSearchParams(page, size));
    }

    public static InboundBrandMappingSearchParams searchParams(
            long inboundSourceId, List<String> statuses, String searchField, String searchWord) {
        return new InboundBrandMappingSearchParams(
                inboundSourceId, statuses, searchField, searchWord, defaultCommonSearchParams());
    }

    public static InboundBrandMappingSearchParams searchParamsWithStatusFilter(String status) {
        return new InboundBrandMappingSearchParams(
                null, List.of(status), null, null, defaultCommonSearchParams());
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", page, size);
    }

    // ===== InboundBrandMappingResult Fixtures =====

    public static InboundBrandMappingResult mappingResult(Long id) {
        return new InboundBrandMappingResult(
                id,
                1L,
                "BR001",
                "외부 브랜드 A",
                100L,
                "ACTIVE",
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    public static InboundBrandMappingResult mappingResult(
            Long id, Long inboundSourceId, String externalBrandCode, Long internalBrandId) {
        return new InboundBrandMappingResult(
                id,
                inboundSourceId,
                externalBrandCode,
                externalBrandCode + " Brand",
                internalBrandId,
                "ACTIVE",
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    // ===== InboundBrandMappingPageResult Fixtures =====

    public static InboundBrandMappingPageResult pageResult() {
        List<InboundBrandMappingResult> results = List.of(mappingResult(1L), mappingResult(2L));
        return InboundBrandMappingPageResult.of(results, 0, 20, 2);
    }

    public static InboundBrandMappingPageResult pageResult(
            List<InboundBrandMappingResult> results) {
        return InboundBrandMappingPageResult.of(results, 0, 20, results.size());
    }

    public static InboundBrandMappingPageResult emptyPageResult() {
        return InboundBrandMappingPageResult.of(List.of(), PageMeta.empty(20));
    }
}
