package com.ryuqq.marketplace.application.productgroup.manager;

import com.ryuqq.marketplace.application.productgroup.port.out.command.ProductGroupCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ProductGroup Command Manager. */
@Component
public class ProductGroupCommandManager {

    private final ProductGroupCommandPort commandPort;

    public ProductGroupCommandManager(ProductGroupCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ProductGroup productGroup) {
        return commandPort.persist(productGroup);
    }
}
