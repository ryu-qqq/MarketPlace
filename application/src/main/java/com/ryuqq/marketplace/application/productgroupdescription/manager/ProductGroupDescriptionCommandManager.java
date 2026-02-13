package com.ryuqq.marketplace.application.productgroupdescription.manager;

import com.ryuqq.marketplace.application.productgroupdescription.port.out.command.ProductGroupDescriptionCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ProductGroupDescription Command Manager. */
@Component
public class ProductGroupDescriptionCommandManager {

    private final ProductGroupDescriptionCommandPort commandPort;

    public ProductGroupDescriptionCommandManager(ProductGroupDescriptionCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ProductGroupDescription description) {
        return commandPort.persist(description);
    }
}
