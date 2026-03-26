package com.ryuqq.marketplace.application.legacy.productgroupdescription.port.out.command;

import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;

/** 레거시 상품 상세설명 저장 Port. */
public interface LegacyProductDescriptionCommandPort {

    Long persist(ProductGroupDescription description);
}
