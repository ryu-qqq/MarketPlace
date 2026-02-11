package com.ryuqq.marketplace.application.saleschannelcategory;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.query.SalesChannelCategorySearchParams;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryPageResult;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;

/**
 * SalesChannelCategory Application Query 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SalesChannelCategoryQueryFixtures {

    private SalesChannelCategoryQueryFixtures() {}

    // ===== Search Params Fixtures =====

    public static SalesChannelCategorySearchParams searchParams() {
        return SalesChannelCategorySearchParams.of(
                null, null, null, null, defaultCommonSearchParams());
    }

    public static SalesChannelCategorySearchParams searchParams(List<Long> salesChannelIds) {
        return SalesChannelCategorySearchParams.of(
                salesChannelIds, null, null, null, defaultCommonSearchParams());
    }

    public static SalesChannelCategorySearchParams searchParams(int page, int size) {
        return SalesChannelCategorySearchParams.of(
                null, null, null, null, commonSearchParams(page, size));
    }

    public static SalesChannelCategorySearchParams searchParams(
            List<Long> salesChannelIds,
            List<String> statuses,
            String searchField,
            String searchWord) {
        return SalesChannelCategorySearchParams.of(
                salesChannelIds, statuses, searchField, searchWord, defaultCommonSearchParams());
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", page, size);
    }

    // ===== Result Fixtures =====

    public static SalesChannelCategoryResult salesChannelCategoryResult(Long id) {
        Instant now = Instant.now();
        return new SalesChannelCategoryResult(
                id,
                1L,
                "CAT" + id,
                "테스트 카테고리 " + id,
                null,
                1,
                "/CAT" + id,
                1,
                false,
                "ACTIVE",
                now,
                now);
    }

    public static SalesChannelCategoryResult salesChannelCategoryResult(
            Long id,
            Long salesChannelId,
            String externalCategoryCode,
            String externalCategoryName) {
        Instant now = Instant.now();
        return new SalesChannelCategoryResult(
                id,
                salesChannelId,
                externalCategoryCode,
                externalCategoryName,
                null,
                1,
                "/" + externalCategoryCode,
                1,
                false,
                "ACTIVE",
                now,
                now);
    }

    public static SalesChannelCategoryPageResult salesChannelCategoryPageResult() {
        List<SalesChannelCategoryResult> results =
                List.of(salesChannelCategoryResult(1L), salesChannelCategoryResult(2L));
        PageMeta pageMeta = PageMeta.of(0, 20, 2L);
        return SalesChannelCategoryPageResult.of(results, pageMeta);
    }

    public static SalesChannelCategoryPageResult emptySalesChannelCategoryPageResult() {
        return SalesChannelCategoryPageResult.empty(20);
    }
}
