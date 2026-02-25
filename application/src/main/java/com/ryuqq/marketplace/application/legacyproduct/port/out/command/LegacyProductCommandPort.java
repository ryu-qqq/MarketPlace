package com.ryuqq.marketplace.application.legacyproduct.port.out.command;

import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;

/** 세토프 DB product 테이블 커맨드 Port. */
public interface LegacyProductCommandPort {

    Long persist(LegacyProduct product);
}
