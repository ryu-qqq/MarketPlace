package com.ryuqq.marketplace.application.outboundproduct.port.in.query;

import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductDetailResult;

/** OMS 상품 상세 조회 UseCase. */
public interface GetOmsProductDetailUseCase {
    OmsProductDetailResult execute(Long productGroupId);
}
