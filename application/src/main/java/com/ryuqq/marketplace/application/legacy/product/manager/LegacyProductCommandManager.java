package com.ryuqq.marketplace.application.legacy.product.manager;

import com.ryuqq.marketplace.application.legacy.product.port.out.command.LegacyProductCommandPort;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 상품 Command Manager. */
@Component
public class LegacyProductCommandManager {

    private final LegacyProductCommandPort commandPort;

    public LegacyProductCommandManager(LegacyProductCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(Product product) {
        return commandPort.persist(product);
    }

    @Transactional
    public void softDeleteByProductGroupId(long productGroupId) {
        commandPort.softDeleteByProductGroupId(productGroupId);
    }

    @Transactional
    public void updateStock(long productId, int stockQuantity) {
        commandPort.updateStock(productId, stockQuantity);
    }
}
