package com.ryuqq.marketplace.application.brand;

import com.ryuqq.marketplace.application.brand.dto.query.BrandSearchParams;
import com.ryuqq.marketplace.application.brand.dto.response.BrandPageResult;
import com.ryuqq.marketplace.application.brand.dto.response.BrandResult;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.domain.brand.BrandFixtures;
import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import java.util.List;

/**
 * Brand Application Query 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class BrandQueryFixtures {

    private BrandQueryFixtures() {}

    // ===== Search Params Fixtures =====
    public static BrandSearchParams searchParams() {
        return BrandSearchParams.of(null, null, null, defaultCommonSearchParams());
    }

    public static BrandSearchParams searchParams(List<String> statuses) {
        return BrandSearchParams.of(statuses, null, null, defaultCommonSearchParams());
    }

    public static BrandSearchParams searchParams(String searchWord) {
        return BrandSearchParams.of(null, null, searchWord, defaultCommonSearchParams());
    }

    public static BrandSearchParams searchParams(String searchField, String searchWord) {
        return BrandSearchParams.of(null, searchField, searchWord, defaultCommonSearchParams());
    }

    public static BrandSearchParams searchParams(int page, int size) {
        return BrandSearchParams.of(null, null, null, commonSearchParams(page, size));
    }

    public static BrandSearchParams searchParams(
            List<String> statuses, String searchField, String searchWord, int page, int size) {
        return BrandSearchParams.of(statuses, searchField, searchWord, commonSearchParams(page, size));
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
    }

    // ===== Result Fixtures =====
    public static BrandResult brandResult(Long id) {
        Brand brand = BrandFixtures.activeBrand(id);
        return BrandResult.from(brand);
    }

    public static BrandResult brandResult() {
        return brandResult(1L);
    }

    public static BrandResult inactiveBrandResult(Long id) {
        Brand brand = BrandFixtures.inactiveBrand(id);
        return BrandResult.from(brand);
    }

    public static List<BrandResult> brandResults() {
        return List.of(
                brandResult(1L),
                brandResult(2L),
                brandResult(3L)
        );
    }

    public static BrandPageResult brandPageResult() {
        List<BrandResult> results = brandResults();
        return BrandPageResult.of(results, 0, 20, 3L);
    }

    public static BrandPageResult brandPageResult(int page, int size, long totalElements) {
        List<BrandResult> results = brandResults();
        return BrandPageResult.of(results, page, size, totalElements);
    }

    public static BrandPageResult emptyBrandPageResult() {
        return BrandPageResult.empty(20);
    }
}
