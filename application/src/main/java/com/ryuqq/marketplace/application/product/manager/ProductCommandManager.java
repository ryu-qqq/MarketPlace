package com.ryuqq.marketplace.application.product.manager;

import com.ryuqq.marketplace.application.product.port.out.command.ProductCommandPort;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Product Command Manager. */
@Component
public class ProductCommandManager {

    private final ProductCommandPort commandPort;

    public ProductCommandManager(ProductCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(Product product) {
        return commandPort.persist(product);
    }

    @Transactional
    public List<Long> persistAll(List<Product> products) {
        return commandPort.persistAll(products);
    }
}
