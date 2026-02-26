package com.ryuqq.marketplace.application.productgroup.port.in.query;

import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;

/** 상품 그룹 상세 조회 UseCase (ID 기반, 연관 Aggregate 포함). */
public interface GetProductGroupUseCase {
    ProductGroupDetailCompositeResult execute(Long productGroupId);
}
