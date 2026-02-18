package com.ryuqq.marketplace.application.product.port.out.command;

import com.ryuqq.marketplace.domain.product.aggregate.Product;

/** Product Command Port. */
public interface ProductCommandPort {

    Long persist(Product product);
}
