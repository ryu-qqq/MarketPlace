package com.ryuqq.marketplace.application.legacyproduct.manager;

import com.ryuqq.marketplace.application.legacyproduct.port.out.command.LegacyProductCommandPort;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import java.util.List;
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
    public Long persist(LegacyProduct product) {
        return commandPort.persist(product);
    }

    @Transactional
    public void persistAll(List<LegacyProduct> products) {
        commandPort.persistAll(products);
    }
}
