package com.ryuqq.marketplace.application.legacyproduct.port.out.command;

import com.ryuqq.marketplace.domain.legacy.product.id.LegacyProductId;

/** 세토프 DB product_stock 테이블 커맨드 Port. */
public interface LegacyProductStockCommandPort {

    void persist(LegacyProductId productId, int stockQuantity);
}
