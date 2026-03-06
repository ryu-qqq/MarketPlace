package com.ryuqq.marketplace.application.outboundproduct.port.in.query;

import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsProductSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductPageResult;

/** OMS 상품 목록 조회 UseCase. */
public interface SearchOmsProductUseCase {
    OmsProductPageResult execute(OmsProductSearchParams params);
}
