package com.ryuqq.marketplace.application.seller.port.in.query;

import com.ryuqq.marketplace.application.seller.dto.composite.SellerFullCompositeResult;

/** 어드민용 셀러 상세 조회 UseCase. */
public interface GetSellerForAdminUseCase {

    SellerFullCompositeResult execute(Long sellerId);
}
