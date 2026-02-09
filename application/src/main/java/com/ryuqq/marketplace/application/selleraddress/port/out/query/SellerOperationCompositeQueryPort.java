package com.ryuqq.marketplace.application.selleraddress.port.out.query;

import com.ryuqq.marketplace.application.selleraddress.dto.composite.SellerOperationCompositeResult;
import com.ryuqq.marketplace.domain.seller.id.SellerId;

/**
 * SellerOperationCompositeQueryPort - 셀러 운영 메타데이터 Composite 조회 Port.
 *
 * <p>주소, 배송정책, 환불정책의 메타데이터를 크로스 도메인 조인으로 조회합니다.
 */
public interface SellerOperationCompositeQueryPort {

    /**
     * 셀러 운영 메타데이터 조회.
     *
     * @param sellerId 셀러 ID
     * @return 운영 메타데이터 Composite 결과
     */
    SellerOperationCompositeResult findOperationMetadataBySellerId(SellerId sellerId);
}
