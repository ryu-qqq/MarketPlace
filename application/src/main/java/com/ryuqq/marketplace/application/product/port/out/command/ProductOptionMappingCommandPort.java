package com.ryuqq.marketplace.application.product.port.out.command;

import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;

/** ProductOptionMapping Command Port. */
public interface ProductOptionMappingCommandPort {

    void persist(ProductOptionMapping mapping);
}
