package com.ryuqq.marketplace.domain.order.vo;

/** 내부 상품 참조. 매핑 결과로 연결된 우리 시스템의 상품 정보입니다. 정산, 재고관리 등에 사용됩니다. */
public record InternalProductReference(
        long productGroupId, long productId, long sellerId, long brandId, String skuCode) {

    public static InternalProductReference of(
            long productGroupId, long productId, long sellerId, long brandId, String skuCode) {
        return new InternalProductReference(productGroupId, productId, sellerId, brandId, skuCode);
    }
}
