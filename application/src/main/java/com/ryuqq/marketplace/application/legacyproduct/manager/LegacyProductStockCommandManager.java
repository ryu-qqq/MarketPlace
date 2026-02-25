package com.ryuqq.marketplace.application.legacyproduct.manager;

import com.ryuqq.marketplace.application.legacyproduct.port.out.command.LegacyProductStockCommandPort;
import com.ryuqq.marketplace.domain.legacy.product.id.LegacyProductId;
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
}
