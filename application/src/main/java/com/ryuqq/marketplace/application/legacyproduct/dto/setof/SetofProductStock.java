package com.ryuqq.marketplace.application.legacyproduct.dto.setof;

/** 세토프 DB product_stock 테이블 데이터. */
public record SetofProductStock(Long productId, int stockQuantity) {

    public SetofProductStock withProductId(Long productId) {
        return new SetofProductStock(productId, stockQuantity);
    }
}
