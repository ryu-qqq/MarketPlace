package com.ryuqq.marketplace.application.selleraddress.port.in.query;

import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressMetadataResult;

/** 셀러 주소 메타데이터 조회 UseCase. */
public interface GetSellerAddressMetadataUseCase {

    SellerAddressMetadataResult execute(Long sellerId);
}
