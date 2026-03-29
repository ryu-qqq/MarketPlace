package com.ryuqq.marketplace.application.legacy.productcontext;

import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.domain.legacyconversion.vo.ResolvedLegacyProductIds;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.List;
import java.util.Map;

/**
 * LegacyProductContext Application 테스트 Fixtures.
 *
 * <p>레거시 PK resolve, UpdateProductsCommand, UpdateProductGroupFullCommand 관련 테스트 데이터를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacyProductContextFixtures {

    private LegacyProductContextFixtures() {}

    // ===== 기본 상수 =====
    public static final long LEGACY_PRODUCT_GROUP_ID = 100L;
    public static final long INTERNAL_PRODUCT_GROUP_ID = 200L;
    public static final long LEGACY_PRODUCT_ID_1 = 1001L;
    public static final long LEGACY_PRODUCT_ID_2 = 1002L;
    public static final long INTERNAL_PRODUCT_ID_1 = 2001L;
    public static final long INTERNAL_PRODUCT_ID_2 = 2002L;

    // ===== ResolvedLegacyProductIds Fixtures =====

    public static ResolvedLegacyProductIds resolvedLegacyProductIds() {
        Map<Long, ProductId> productIdMap =
                Map.of(
                        LEGACY_PRODUCT_ID_1, ProductId.of(INTERNAL_PRODUCT_ID_1),
                        LEGACY_PRODUCT_ID_2, ProductId.of(INTERNAL_PRODUCT_ID_2));
        return new ResolvedLegacyProductIds(
                LEGACY_PRODUCT_GROUP_ID,
                ProductGroupId.of(INTERNAL_PRODUCT_GROUP_ID),
                productIdMap);
    }

    public static ResolvedLegacyProductIds resolvedLegacyProductIdsEmpty() {
        return new ResolvedLegacyProductIds(
                LEGACY_PRODUCT_GROUP_ID, ProductGroupId.of(INTERNAL_PRODUCT_GROUP_ID), Map.of());
    }

    public static ResolvedLegacyProductIds resolvedLegacyProductIds(
            long internalProductGroupId, Map<Long, ProductId> productIdMap) {
        return new ResolvedLegacyProductIds(
                internalProductGroupId, ProductGroupId.of(internalProductGroupId), productIdMap);
    }

    // ===== UpdateProductsCommand Fixtures =====

    public static UpdateProductsCommand updateProductsCommand() {
        return new UpdateProductsCommand(
                LEGACY_PRODUCT_GROUP_ID,
                List.of(),
                List.of(
                        new UpdateProductsCommand.ProductData(
                                LEGACY_PRODUCT_ID_1, "SKU-001", 10000, 9000, 100, 0, List.of()),
                        new UpdateProductsCommand.ProductData(
                                LEGACY_PRODUCT_ID_2, "SKU-002", 10000, 9000, 50, 1, List.of())));
    }

    public static UpdateProductsCommand updateProductsCommandWithNullProductId() {
        return new UpdateProductsCommand(
                LEGACY_PRODUCT_GROUP_ID,
                List.of(),
                List.of(
                        new UpdateProductsCommand.ProductData(
                                null, "SKU-NEW", 10000, 9000, 100, 0, List.of())));
    }

    public static UpdateProductsCommand updateProductsCommandWithZeroProductId() {
        return new UpdateProductsCommand(
                LEGACY_PRODUCT_GROUP_ID,
                List.of(),
                List.of(
                        new UpdateProductsCommand.ProductData(
                                0L, "SKU-ZERO", 10000, 9000, 100, 0, List.of())));
    }

    // ===== UpdateProductGroupFullCommand Fixtures =====

    public static UpdateProductGroupFullCommand updateProductGroupFullCommand() {
        return new UpdateProductGroupFullCommand(
                LEGACY_PRODUCT_GROUP_ID,
                "수정된 상품그룹",
                100L,
                200L,
                1L,
                1L,
                "SINGLE",
                List.of(
                        new UpdateProductGroupFullCommand.ImageCommand(
                                "THUMBNAIL", "https://example.com/img.jpg", 0)),
                List.of(),
                List.of(
                        new UpdateProductGroupFullCommand.ProductCommand(
                                LEGACY_PRODUCT_ID_1, "SKU-001", 10000, 9000, 80, 0, List.of()),
                        new UpdateProductGroupFullCommand.ProductCommand(
                                null, "SKU-NEW", 10000, 9000, 30, 1, List.of())),
                new UpdateProductGroupFullCommand.DescriptionCommand("<p>설명</p>", List.of()),
                new UpdateProductGroupFullCommand.NoticeCommand(10L, List.of()));
    }

    // ===== UpdateProductStockCommand Fixtures =====

    public static List<UpdateProductStockCommand> updateStockCommands() {
        return List.of(
                new UpdateProductStockCommand(LEGACY_PRODUCT_ID_1, 50),
                new UpdateProductStockCommand(LEGACY_PRODUCT_ID_2, 30));
    }

    public static List<UpdateProductStockCommand> updateStockCommands(
            long productId, int stockQuantity) {
        return List.of(new UpdateProductStockCommand(productId, stockQuantity));
    }
}
