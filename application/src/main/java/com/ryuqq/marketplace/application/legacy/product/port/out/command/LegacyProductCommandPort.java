package com.ryuqq.marketplace.application.legacy.product.port.out.command;

import com.ryuqq.marketplace.domain.product.aggregate.Product;

/** 세토프 DB product + product_stock + product_option 테이블 커맨드 Port. */
public interface LegacyProductCommandPort {

    Long persist(Product product);

    void softDeleteByProductGroupId(long productGroupId);

    void updateStock(long productId, int stockQuantity);
}
