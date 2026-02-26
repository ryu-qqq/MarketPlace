package com.ryuqq.marketplace.application.saleschannelcategory;

import com.ryuqq.marketplace.application.saleschannelcategory.dto.command.RegisterSalesChannelCategoryCommand;

/**
 * SalesChannelCategory Application Command 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SalesChannelCategoryCommandFixtures {

    private SalesChannelCategoryCommandFixtures() {}

    // ===== Command Fixtures =====

    public static RegisterSalesChannelCategoryCommand registerCommand() {
        return new RegisterSalesChannelCategoryCommand(
                1L, "CAT001", "테스트 카테고리", null, 1, "/CAT001", 1, false, "테스트 카테고리");
    }

    public static RegisterSalesChannelCategoryCommand registerCommand(
            Long salesChannelId, String externalCategoryCode, String externalCategoryName) {
        return new RegisterSalesChannelCategoryCommand(
                salesChannelId,
                externalCategoryCode,
                externalCategoryName,
                null,
                1,
                "/" + externalCategoryCode,
                1,
                false,
                externalCategoryName);
    }

    public static RegisterSalesChannelCategoryCommand registerChildCommand(Long parentId) {
        return new RegisterSalesChannelCategoryCommand(
                1L,
                "CAT002",
                "하위 카테고리",
                parentId,
                2,
                "/CAT001/CAT002",
                2,
                false,
                "테스트 카테고리 > 하위 카테고리");
    }

    public static RegisterSalesChannelCategoryCommand registerLeafCommand() {
        return new RegisterSalesChannelCategoryCommand(
                1L,
                "CAT003",
                "말단 카테고리",
                100L,
                3,
                "/CAT001/CAT002/CAT003",
                1,
                true,
                "상위 > 중간 > 말단 카테고리");
    }
}
