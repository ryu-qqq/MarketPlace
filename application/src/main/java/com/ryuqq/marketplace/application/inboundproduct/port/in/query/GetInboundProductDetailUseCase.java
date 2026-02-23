package com.ryuqq.marketplace.application.inboundproduct.port.in.query;

import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductDetailResult;

/**
 * 인바운드 상품 상세 조회 UseCase.
 *
 * <p>외부 식별자(inboundSourceId + externalProductCode)로 인바운드 상품 상태 및 내부 상품 목록을 조회합니다.
 */
public interface GetInboundProductDetailUseCase {

    /**
     * 인바운드 상품 상세 조회.
     *
     * @param inboundSourceId 인바운드 소스 ID
     * @param externalProductCode 외부 상품 코드
     * @return 인바운드 상품 상세 결과 (상태, 내부 상품 그룹 ID, 상품 목록)
     */
    InboundProductDetailResult execute(long inboundSourceId, String externalProductCode);
}
