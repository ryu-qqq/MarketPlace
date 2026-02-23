package com.ryuqq.marketplace.application.legacyproduct.port.in.query;

import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;

/**
 * 레거시 상품 조회 UseCase.
 *
 * <p>세토프 PK로 상품 그룹 상세 정보를 조회합니다.
 */
public interface LegacyProductQueryUseCase {

    /**
     * 세토프 PK로 상품 그룹 상세 조회.
     *
     * @param setofProductGroupId 세토프 상품그룹 PK
     * @return 상품 그룹 상세 Composite 결과
     */
    ProductGroupDetailCompositeResult execute(long setofProductGroupId);
}
