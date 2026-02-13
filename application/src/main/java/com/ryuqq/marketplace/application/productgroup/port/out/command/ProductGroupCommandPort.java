package com.ryuqq.marketplace.application.productgroup.port.out.command;

import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;

/** ProductGroup Command Port. */
public interface ProductGroupCommandPort {

    Long persist(ProductGroup productGroup);
}
