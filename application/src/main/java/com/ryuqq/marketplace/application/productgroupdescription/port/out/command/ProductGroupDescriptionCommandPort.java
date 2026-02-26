package com.ryuqq.marketplace.application.productgroupdescription.port.out.command;

import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;

/** ProductGroupDescription Command Port. */
public interface ProductGroupDescriptionCommandPort {

    Long persist(ProductGroupDescription description);
}
