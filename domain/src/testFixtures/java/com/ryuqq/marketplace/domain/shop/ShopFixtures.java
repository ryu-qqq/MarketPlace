package com.ryuqq.marketplace.domain.shop;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.aggregate.ShopUpdateData;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import java.time.Instant;

/**
 * Shop 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 Shop 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ShopFixtures {

    private ShopFixtures() {}

    // ===== ShopId Fixtures =====
    public static ShopId shopId(Long value) {
        return ShopId.of(value);
    }

    public static ShopId newShopId() {
        return ShopId.forNew();
    }

    public static ShopId defaultShopId() {
        return ShopId.of(1L);
    }

    // ===== ShopStatus Fixtures =====
    public static ShopStatus activeStatus() {
        return ShopStatus.ACTIVE;
    }

    public static ShopStatus inactiveStatus() {
        return ShopStatus.INACTIVE;
    }

    // ===== Shop Name Fixtures =====
    public static String defaultShopName() {
        return "테스트 외부몰";
    }

    public static String shopName(String value) {
        return value;
    }

    // ===== Account ID Fixtures =====
    public static String defaultAccountId() {
        return "test-account-123";
    }

    public static String accountId(String value) {
        return value;
    }

    // ===== Shop Aggregate Fixtures =====
    public static Shop newShop() {
        return Shop.forNew(defaultShopName(), defaultAccountId(), CommonVoFixtures.now());
    }

    public static Shop newShop(String shopName, String accountId) {
        return Shop.forNew(shopName, accountId, CommonVoFixtures.now());
    }

    public static Shop activeShop() {
        return Shop.reconstitute(
                ShopId.of(1L),
                defaultShopName(),
                defaultAccountId(),
                ShopStatus.ACTIVE,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Shop activeShop(Long id) {
        return Shop.reconstitute(
                ShopId.of(id),
                defaultShopName(),
                defaultAccountId(),
                ShopStatus.ACTIVE,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Shop activeShop(String shopName, String accountId) {
        return Shop.reconstitute(
                ShopId.of(1L),
                shopName,
                accountId,
                ShopStatus.ACTIVE,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Shop inactiveShop() {
        return Shop.reconstitute(
                ShopId.of(2L),
                defaultShopName(),
                defaultAccountId(),
                ShopStatus.INACTIVE,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Shop deletedShop() {
        Instant deletedAt = CommonVoFixtures.yesterday();
        return Shop.reconstitute(
                ShopId.of(3L),
                defaultShopName(),
                defaultAccountId(),
                ShopStatus.INACTIVE,
                deletedAt,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    // ===== ShopUpdateData Fixtures =====
    public static ShopUpdateData shopUpdateData() {
        return ShopUpdateData.of("수정된 외부몰명", "updated-account-456", ShopStatus.ACTIVE);
    }

    public static ShopUpdateData shopUpdateData(
            String shopName, String accountId, ShopStatus status) {
        return ShopUpdateData.of(shopName, accountId, status);
    }

    public static ShopUpdateData inactiveShopUpdateData() {
        return ShopUpdateData.of("수정된 외부몰명", "updated-account-456", ShopStatus.INACTIVE);
    }
}
