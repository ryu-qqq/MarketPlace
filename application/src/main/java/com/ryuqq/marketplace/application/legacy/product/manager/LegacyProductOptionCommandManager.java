package com.ryuqq.marketplace.application.legacy.product.manager;

import com.ryuqq.marketplace.application.legacy.product.port.out.command.LegacyProductOptionCommandPort;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 상품옵션매핑 Command Manager. */
@Component
public class LegacyProductOptionCommandManager {

    private final LegacyProductOptionCommandPort commandPort;

    public LegacyProductOptionCommandManager(LegacyProductOptionCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(ProductOptionMapping mapping) {
        commandPort.persist(mapping);
    }
}
