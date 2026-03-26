package com.ryuqq.marketplace.application.legacy.product.port.out.command;

import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;

/** 세토프 DB product_option 테이블 커맨드 Port. */
public interface LegacyProductOptionCommandPort {

    void persist(ProductOptionMapping mapping);
}
