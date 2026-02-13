package com.ryuqq.marketplace.application.productgroupdescription.port.out.query;

import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.Optional;

/** ProductGroupDescription Query Port. */
public interface ProductGroupDescriptionQueryPort {

    Optional<ProductGroupDescription> findByProductGroupId(ProductGroupId productGroupId);
}
