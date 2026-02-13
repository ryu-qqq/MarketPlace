package com.ryuqq.marketplace.application.product.port.out.command;

import com.ryuqq.marketplace.domain.product.aggregate.Product;
import java.util.List;

/** Product Command Port. */
public interface ProductCommandPort {

    Long persist(Product product);

    List<Long> persistAll(List<Product> products);
}
