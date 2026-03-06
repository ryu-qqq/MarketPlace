package com.ryuqq.marketplace.application.outboundproduct.port.in.query;

import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsPartnerSearchParams;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;

/** OMS 파트너(셀러) 검색 UseCase (Offset 기반 페이징). */
public interface SearchOmsPartnersByOffsetUseCase {

    SellerPageResult execute(OmsPartnerSearchParams params);
}
