package com.ryuqq.marketplace.adapter.in.rest.shop;

import com.ryuqq.marketplace.adapter.in.rest.shop.dto.command.RegisterShopApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.command.UpdateShopApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.query.SearchShopsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.response.ShopApiResponse;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
import com.ryuqq.marketplace.application.shop.dto.response.ShopResult;
import java.time.Instant;
import java.util.List;

/**
 * Shop API 테스트 Fixtures.
 *
 * <p>Shop REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ShopApiFixtures {

    private ShopApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final String DEFAULT_SHOP_NAME = "테스트몰";
    public static final String DEFAULT_ACCOUNT_ID = "test_account_01";
    public static final String DEFAULT_STATUS = "ACTIVE";

    // ===== RegisterShopApiRequest =====

    public static RegisterShopApiRequest registerRequest() {
        return new RegisterShopApiRequest(
                DEFAULT_SALES_CHANNEL_ID, DEFAULT_SHOP_NAME, DEFAULT_ACCOUNT_ID);
    }

    public static RegisterShopApiRequest registerRequest(String shopName, String accountId) {
        return new RegisterShopApiRequest(DEFAULT_SALES_CHANNEL_ID, shopName, accountId);
    }

    // ===== UpdateShopApiRequest =====

    public static UpdateShopApiRequest updateRequest() {
        return new UpdateShopApiRequest("수정된몰명", "updated_account_01", "ACTIVE");
    }

    public static UpdateShopApiRequest updateRequest(
            String shopName, String accountId, String status) {
        return new UpdateShopApiRequest(shopName, accountId, status);
    }

    // ===== SearchShopsApiRequest =====

    public static SearchShopsApiRequest searchRequest() {
        return new SearchShopsApiRequest(null, null, null, null, null, null, 0, 20);
    }

    public static SearchShopsApiRequest searchRequest(
            List<String> statuses, String searchField, String searchWord, int page, int size) {
        return new SearchShopsApiRequest(
                null, statuses, searchField, searchWord, "createdAt", "DESC", page, size);
    }

    // ===== ShopResult (Application) =====

    public static ShopResult shopResult(Long id) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new ShopResult(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_NAME,
                DEFAULT_ACCOUNT_ID,
                DEFAULT_STATUS,
                now,
                now);
    }

    public static ShopResult shopResult(Long id, String shopName, String status) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new ShopResult(
                id, DEFAULT_SALES_CHANNEL_ID, shopName, DEFAULT_ACCOUNT_ID, status, now, now);
    }

    public static List<ShopResult> shopResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> shopResult((long) i, "테스트몰_" + i, DEFAULT_STATUS))
                .toList();
    }

    public static ShopPageResult pageResult(int count, int page, int size) {
        List<ShopResult> results = shopResults(count);
        return ShopPageResult.of(results, page, size, count);
    }

    public static ShopPageResult emptyPageResult() {
        return ShopPageResult.of(List.of(), 0, 20, 0);
    }

    // ===== ShopApiResponse =====

    public static ShopApiResponse apiResponse(Long id) {
        return new ShopApiResponse(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_NAME,
                DEFAULT_ACCOUNT_ID,
                DEFAULT_STATUS,
                "2025-01-23T10:30:00+09:00",
                "2025-01-23T10:30:00+09:00");
    }

    public static ShopApiResponse apiResponse(Long id, String shopName, String status) {
        return new ShopApiResponse(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                shopName,
                DEFAULT_ACCOUNT_ID,
                status,
                "2025-01-23T10:30:00+09:00",
                "2025-01-23T10:30:00+09:00");
    }
}
