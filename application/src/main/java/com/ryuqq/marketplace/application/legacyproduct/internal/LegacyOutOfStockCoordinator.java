package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품 품절 처리 Coordinator.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 */
@Component
public class LegacyOutOfStockCoordinator extends LegacyProductUpdateCoordinator {

    private final LegacyProductCommandFactory commandFactory;
    private final ProductGroupReadManager productGroupReadManager;
    private final ProductGroupCommandManager productGroupCommandManager;

    public LegacyOutOfStockCoordinator(
            LegacyProductIdResolver idResolver,
            LegacyProductCommandFactory commandFactory,
            ProductGroupReadManager productGroupReadManager,
            ProductGroupCommandManager productGroupCommandManager) {
        super(idResolver);
        this.commandFactory = commandFactory;
        this.productGroupReadManager = productGroupReadManager;
        this.productGroupCommandManager = productGroupCommandManager;
    }

    public void execute(long setofProductGroupId) {
        long internalId = resolveInternalId(setofProductGroupId);
        StatusChangeContext<ProductGroupId> context =
                commandFactory.createOutOfStockContext(internalId);

        ProductGroup pg = productGroupReadManager.getById(context.id());
        pg.markSoldOut(context.changedAt());
        productGroupCommandManager.persist(pg);
    }
}
