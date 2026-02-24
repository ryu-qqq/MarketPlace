package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateDisplayStatusCommand;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품 전시 상태 변경 Coordinator.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 */
@Component
public class LegacyDisplayStatusUpdateCoordinator extends LegacyProductUpdateCoordinator {

    private final LegacyProductCommandFactory commandFactory;
    private final ProductGroupReadManager productGroupReadManager;
    private final ProductGroupCommandManager productGroupCommandManager;

    public LegacyDisplayStatusUpdateCoordinator(
            LegacyProductIdResolver idResolver,
            LegacyProductCommandFactory commandFactory,
            ProductGroupReadManager productGroupReadManager,
            ProductGroupCommandManager productGroupCommandManager) {
        super(idResolver);
        this.commandFactory = commandFactory;
        this.productGroupReadManager = productGroupReadManager;
        this.productGroupCommandManager = productGroupCommandManager;
    }

    @Transactional
    public void execute(LegacyUpdateDisplayStatusCommand command) {
        long internalId = resolveInternalId(command.setofProductGroupId());
        StatusChangeContext<ProductGroupId> context =
                commandFactory.createDisplayStatusChangeContext(internalId);

        ProductGroup pg = productGroupReadManager.getById(context.id());
        if ("Y".equalsIgnoreCase(command.displayYn())) {
            pg.activate(context.changedAt());
        } else {
            pg.deactivate(context.changedAt());
        }
        productGroupCommandManager.persist(pg);
    }
}
