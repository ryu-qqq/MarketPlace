package com.ryuqq.marketplace.application.product.manager;

import com.ryuqq.marketplace.application.product.port.out.command.ProductOptionMappingCommandPort;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ProductOptionMapping Command Manager. */
@Component
public class ProductOptionMappingCommandManager {

    private final ProductOptionMappingCommandPort commandPort;

    public ProductOptionMappingCommandManager(ProductOptionMappingCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persistAll(List<ProductOptionMapping> mappings) {
        for (ProductOptionMapping mapping : mappings) {
            commandPort.persist(mapping);
        }
    }
}
