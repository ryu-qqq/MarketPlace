package com.ryuqq.marketplace.application.saleschannelbrand;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.query.SalesChannelBrandSearchParams;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandResult;
import java.time.Instant;
import java.util.List;

/**
 * SalesChannelBrand Application Query 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SalesChannelBrandQueryFixtures {

    private SalesChannelBrandQueryFixtures() {}

    // ===== Search Params Fixtures =====

    public static SalesChannelBrandSearchParams searchParams() {
        return SalesChannelBrandSearchParams.of(
                null, null, null, null, defaultCommonSearchParams());
    }

    public static SalesChannelBrandSearchParams searchParams(int page, int size) {
        return SalesChannelBrandSearchParams.of(
                null, null, null, null, commonSearchParams(page, size));
    }

    public static SalesChannelBrandSearchParams searchParams(List<Long> salesChannelIds) {
        return SalesChannelBrandSearchParams.of(
                salesChannelIds, null, null, null, defaultCommonSearchParams());
    }

    public static SalesChannelBrandSearchParams searchParams(
            List<Long> salesChannelIds, List<String> statuses) {
        return SalesChannelBrandSearchParams.of(
                salesChannelIds, statuses, null, null, defaultCommonSearchParams());
    }

    public static SalesChannelBrandSearchParams searchParams(
            String searchField, String searchWord) {
        return SalesChannelBrandSearchParams.of(
                null, null, searchField, searchWord, defaultCommonSearchParams());
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", page, size);
    }

    // ===== Result Fixtures =====

    public static SalesChannelBrandResult salesChannelBrandResult(Long id) {
        Instant now = Instant.now();
        return new SalesChannelBrandResult(id, 1L, "BRAND-001", "테스트 브랜드", "ACTIVE", now, now);
    }

    public static SalesChannelBrandResult salesChannelBrandResult(
            Long id, String externalBrandCode) {
        Instant now = Instant.now();
        return new SalesChannelBrandResult(
                id, 1L, externalBrandCode, "테스트 브랜드", "ACTIVE", now, now);
    }
}
