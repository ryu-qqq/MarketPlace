package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdatePriceCommand;
import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품 가격 수정 Coordinator.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 */
@Component
public class LegacyPriceUpdateCoordinator extends LegacyProductUpdateCoordinator {

    private final LegacyProductCommandFactory commandFactory;
    private final ProductReadManager productReadManager;
    private final ProductCommandManager productCommandManager;

    public LegacyPriceUpdateCoordinator(
            LegacyProductIdResolver idResolver,
            LegacyProductCommandFactory commandFactory,
            ProductReadManager productReadManager,
            ProductCommandManager productCommandManager) {
        super(idResolver);
        this.commandFactory = commandFactory;
        this.productReadManager = productReadManager;
        this.productCommandManager = productCommandManager;
    }

    @Transactional
    public void execute(LegacyUpdatePriceCommand command) {
        long internalId = resolveInternalId(command.setofProductGroupId());
        StatusChangeContext<ProductGroupId> context =
                commandFactory.createPriceUpdateContext(internalId);

        List<Product> products = productReadManager.findByProductGroupId(context.id());
        for (Product product : products) {
            product.updatePrice(
                    Money.of(command.regularPrice()),
                    Money.of(command.currentPrice()),
                    context.changedAt());
        }
        productCommandManager.persistAll(products);
    }
}
