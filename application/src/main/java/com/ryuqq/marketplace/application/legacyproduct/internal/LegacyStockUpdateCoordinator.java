package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductReadManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductStockCommandManager;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProducts;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품 재고 수정 Coordinator.
 *
 * <p>기존 상품을 조회한 뒤, productId 기준으로 재고량을 변경하여 persist합니다.
 */
@Component
public class LegacyStockUpdateCoordinator {

    private final LegacyProductReadManager productReadManager;
    private final LegacyProductStockCommandManager stockCommandManager;

    public LegacyStockUpdateCoordinator(
            LegacyProductReadManager productReadManager,
            LegacyProductStockCommandManager stockCommandManager) {
        this.productReadManager = productReadManager;
        this.stockCommandManager = stockCommandManager;
    }

    @Transactional
    public void execute(LegacyProductGroupId groupId, Map<Long, Integer> stockUpdates) {
        LegacyProducts existingProducts = productReadManager.getByProductGroupId(groupId);

        List<LegacyProduct> changed = existingProducts.applyStockUpdates(stockUpdates);

        if (!changed.isEmpty()) {
            stockCommandManager.persistAll(changed);
        }
    }
}
