package com.ryuqq.marketplace.application.shop;

import com.ryuqq.marketplace.application.shop.dto.command.RegisterShopCommand;
import com.ryuqq.marketplace.application.shop.dto.command.UpdateShopCommand;

/**
 * Shop Command 테스트 Fixtures.
 *
 * <p>Shop 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ShopCommandFixtures {

    private ShopCommandFixtures() {}

    // ===== RegisterShopCommand =====

    public static RegisterShopCommand registerCommand() {
        return new RegisterShopCommand("테스트 외부몰", "test-account-123");
    }

    public static RegisterShopCommand registerCommand(String shopName) {
        return new RegisterShopCommand(shopName, "test-account-123");
    }

    public static RegisterShopCommand registerCommand(String shopName, String accountId) {
        return new RegisterShopCommand(shopName, accountId);
    }

    // ===== UpdateShopCommand =====

    public static UpdateShopCommand updateCommand(Long shopId) {
        return new UpdateShopCommand(shopId, "수정된 외부몰", "updated-account-456", "ACTIVE");
    }

    public static UpdateShopCommand updateCommand(Long shopId, String status) {
        return new UpdateShopCommand(shopId, "수정된 외부몰", "updated-account-456", status);
    }

    public static UpdateShopCommand updateCommand(
            Long shopId, String shopName, String accountId, String status) {
        return new UpdateShopCommand(shopId, shopName, accountId, status);
    }
}
