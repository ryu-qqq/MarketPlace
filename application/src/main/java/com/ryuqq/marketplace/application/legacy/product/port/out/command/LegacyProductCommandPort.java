package com.ryuqq.marketplace.application.legacy.product.port.out.command;

import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import java.util.List;

/** 세토프 DB product 테이블 커맨드 Port. */
public interface LegacyProductCommandPort {

    Long persist(LegacyProduct product);

    void persistAll(List<LegacyProduct> products);
}
