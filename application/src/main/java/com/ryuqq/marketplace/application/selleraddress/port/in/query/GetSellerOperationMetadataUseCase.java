package com.ryuqq.marketplace.application.selleraddress.port.in.query;

import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerOperationMetadataResult;

/** 셀러 운영 메타데이터 조회 UseCase. */
public interface GetSellerOperationMetadataUseCase {

    SellerOperationMetadataResult execute(Long sellerId);
}
