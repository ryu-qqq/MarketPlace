package com.ryuqq.marketplace.application.saleschannelbrand;

import com.ryuqq.marketplace.application.saleschannelbrand.dto.command.RegisterSalesChannelBrandCommand;

/**
 * SalesChannelBrand Application Command 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SalesChannelBrandCommandFixtures {

    private SalesChannelBrandCommandFixtures() {}

    // ===== Command Fixtures =====

    public static RegisterSalesChannelBrandCommand registerCommand() {
        return new RegisterSalesChannelBrandCommand(1L, "BRAND-001", "테스트 브랜드");
    }

    public static RegisterSalesChannelBrandCommand registerCommand(
            Long salesChannelId, String externalBrandCode, String externalBrandName) {
        return new RegisterSalesChannelBrandCommand(
                salesChannelId, externalBrandCode, externalBrandName);
    }

    public static RegisterSalesChannelBrandCommand registerCommand(String externalBrandCode) {
        return new RegisterSalesChannelBrandCommand(1L, externalBrandCode, "테스트 브랜드");
    }
}
