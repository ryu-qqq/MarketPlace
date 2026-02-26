package com.ryuqq.marketplace.application.seller.port.in.query;

import com.ryuqq.marketplace.application.seller.dto.composite.SellerCompositeResult;

/** 고객용 셀러 조회 UseCase. */
public interface GetSellerForCustomerUseCase {

    SellerCompositeResult execute(Long sellerId);
}
