package com.ryuqq.marketplace.application.shop;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.shop.dto.query.ShopSearchParams;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
import com.ryuqq.marketplace.application.shop.dto.response.ShopResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;

/**
 * Shop Query 테스트 Fixtures.
 *
 * <p>Shop 관련 Query 파라미터 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ShopQueryFixtures {

    private ShopQueryFixtures() {}

    // ===== ShopSearchParams =====

    public static ShopSearchParams searchParams() {
        return ShopSearchParams.of(null, null, null, null, defaultCommonSearchParams());
    }

    public static ShopSearchParams searchParams(int page, int size) {
        return ShopSearchParams.of(null, null, null, null, commonSearchParams(page, size));
    }

    public static ShopSearchParams searchParams(List<String> statuses) {
        return ShopSearchParams.of(null, statuses, null, null, defaultCommonSearchParams());
    }

    public static ShopSearchParams searchParams(String searchField, String searchWord) {
        return ShopSearchParams.of(
                null, null, searchField, searchWord, defaultCommonSearchParams());
    }

    public static ShopSearchParams searchParams(
            List<String> statuses, String searchField, String searchWord, int page, int size) {
        return ShopSearchParams.of(
                null, statuses, searchField, searchWord, commonSearchParams(page, size));
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
    }

    // ===== ShopResult =====

    public static ShopResult shopResult(Long shopId) {
        Instant now = Instant.now();
        return new ShopResult(shopId, 1L, "테스트 외부몰", "test-account", "ACTIVE", now, now);
    }

    public static ShopResult shopResult(Long shopId, String shopName) {
        Instant now = Instant.now();
        return new ShopResult(shopId, 1L, shopName, "test-account", "ACTIVE", now, now);
    }

    public static ShopResult shopResult(
            Long shopId, String shopName, String accountId, String status) {
        Instant now = Instant.now();
        return new ShopResult(shopId, 1L, shopName, accountId, status, now, now);
    }

    // ===== ShopPageResult =====

    public static ShopPageResult shopPageResult() {
        List<ShopResult> results = List.of(shopResult(1L), shopResult(2L));
        PageMeta pageMeta = PageMeta.of(0, 20, 2);
        return ShopPageResult.of(results, pageMeta);
    }

    public static ShopPageResult shopPageResult(int page, int size, long totalElements) {
        List<ShopResult> results = List.of(shopResult(1L));
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return ShopPageResult.of(results, pageMeta);
    }

    public static ShopPageResult emptyShopPageResult() {
        return ShopPageResult.of(List.of(), PageMeta.empty(20));
    }
}
