package com.ryuqq.marketplace.application.legacy.product.manager;

import com.ryuqq.marketplace.application.legacy.product.port.out.command.LegacyProductStockCommandPort;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.product.id.LegacyProductId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 상품재고 Command Manager. */
@Component
public class LegacyProductStockCommandManager {

    private final LegacyProductStockCommandPort commandPort;

    public LegacyProductStockCommandManager(LegacyProductStockCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(LegacyProductId productId, int stockQuantity) {
        commandPort.persist(productId, stockQuantity);
    }

    @Transactional
    public void persistAll(List<LegacyProduct> products) {
        commandPort.persistAll(products);
    }
}
