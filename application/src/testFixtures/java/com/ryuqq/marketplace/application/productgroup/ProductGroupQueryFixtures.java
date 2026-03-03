package com.ryuqq.marketplace.application.productgroup;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import java.util.List;

/**
 * ProductGroup Application Query 테스트 Fixtures.
 *
 * <p>ProductGroup 관련 Query 파라미터 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ProductGroupQueryFixtures {

    private ProductGroupQueryFixtures() {}

    // ===== ProductGroupSearchParams Fixtures =====

    public static ProductGroupSearchParams searchParams() {
        return ProductGroupSearchParams.of(
                null, null, null, null, null, null, null, defaultCommonSearchParams());
    }

    public static ProductGroupSearchParams searchParams(int page, int size) {
        return ProductGroupSearchParams.of(
                null, null, null, null, null, null, null, commonSearchParams(page, size));
    }

    public static ProductGroupSearchParams searchParams(List<String> statuses) {
        return ProductGroupSearchParams.of(
                statuses, null, null, null, null, null, null, defaultCommonSearchParams());
    }

    public static ProductGroupSearchParams searchParams(Long sellerId) {
        return ProductGroupSearchParams.of(
                null, List.of(sellerId), null, null, null, null, null, defaultCommonSearchParams());
    }

    public static ProductGroupSearchParams searchParamsWithSellers(List<Long> sellerIds) {
        return ProductGroupSearchParams.of(
                null, sellerIds, null, null, null, null, null, defaultCommonSearchParams());
    }

    public static ProductGroupSearchParams searchParamsWithBrands(List<Long> brandIds) {
        return ProductGroupSearchParams.of(
                null, null, brandIds, null, null, null, null, defaultCommonSearchParams());
    }

    public static ProductGroupSearchParams searchParamsWithSearchWord(String searchWord) {
        return ProductGroupSearchParams.of(
                null,
                null,
                null,
                null,
                null,
                "productGroupName",
                searchWord,
                defaultCommonSearchParams());
    }

    public static ProductGroupSearchParams searchParamsWithCategoryIds(List<Long> categoryIds) {
        return ProductGroupSearchParams.of(
                null, null, null, categoryIds, null, null, null, defaultCommonSearchParams());
    }

    public static ProductGroupSearchParams searchParamsWithProductGroupIds(
            List<Long> productGroupIds) {
        return ProductGroupSearchParams.of(
                null, null, null, null, productGroupIds, null, null, defaultCommonSearchParams());
    }

    public static ProductGroupSearchParams fullSearchParams(
            List<String> statuses,
            List<Long> sellerIds,
            List<Long> brandIds,
            List<Long> categoryIds,
            String searchField,
            String searchWord,
            int page,
            int size) {
        return ProductGroupSearchParams.of(
                statuses,
                sellerIds,
                brandIds,
                categoryIds,
                null,
                searchField,
                searchWord,
                commonSearchParams(page, size));
    }

    // ===== CommonSearchParams Fixtures =====

    public static CommonSearchParams defaultCommonSearchParams() {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", page, size);
    }

    public static CommonSearchParams commonSearchParams(
            int page, int size, String sortKey, String sortDirection) {
        return new CommonSearchParams(false, null, null, sortKey, sortDirection, page, size);
    }
}
