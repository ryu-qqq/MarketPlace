package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductOptionCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductReadManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductStockCommandManager;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProducts;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProducts.ProductDiffResult;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품 옵션/상품 수정 Coordinator.
 *
 * <p>기존 상품과 새 상품 목록을 diff하여 변경분만 persist합니다. 도메인 객체 생성은 Factory에서 수행하며, Coordinator는 조회·비교·영속만
 * 처리합니다.
 */
@Component
public class LegacyOptionUpdateCoordinator {

    private final TimeProvider timeProvider;
    private final LegacyProductReadManager productReadManager;
    private final LegacyProductCommandManager productCommandManager;
    private final LegacyProductOptionCommandManager optionCommandManager;
    private final LegacyProductStockCommandManager stockCommandManager;

    public LegacyOptionUpdateCoordinator(
            TimeProvider timeProvider,
            LegacyProductReadManager productReadManager,
            LegacyProductCommandManager productCommandManager,
            LegacyProductOptionCommandManager optionCommandManager,
            LegacyProductStockCommandManager stockCommandManager) {
        this.timeProvider = timeProvider;
        this.productReadManager = productReadManager;
        this.productCommandManager = productCommandManager;
        this.optionCommandManager = optionCommandManager;
        this.stockCommandManager = stockCommandManager;
    }

    /**
     * 옵션/상품 수정 실행.
     *
     * <p>Factory에서 생성된 새 상품 목록을 받아 기존 상품과 diff 후 변경분만 persist합니다.
     */
    @Transactional
    public void execute(LegacyProductGroupId groupId, List<LegacyProduct> newProducts) {
        LegacyProducts existingProducts = productReadManager.getByProductGroupId(groupId);

        ProductDiffResult diffResult = existingProducts.diff(newProducts, timeProvider.now());

        if (diffResult.hasChanges()) {
            if (!diffResult.productsToPersist().isEmpty()) {
                productCommandManager.persistAll(diffResult.productsToPersist());
                stockCommandManager.persistAll(diffResult.productsToPersist());
            }
            if (!diffResult.optionsToPersist().isEmpty()) {
                optionCommandManager.persistAll(diffResult.optionsToPersist());
            }
        }
    }
}
