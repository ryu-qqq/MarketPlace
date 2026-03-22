package com.ryuqq.marketplace.application.legacy.productgroupdescription.port.out.query;

import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import java.util.Optional;

/** 레거시 상품그룹 상세설명 Read Port. */
public interface LegacyProductGroupDescriptionReadPort {

    Optional<ProductGroupDescription> findByProductGroupId(long productGroupId);
}
