package com.ryuqq.marketplace.application.selleraddress.port.in.query;

import com.ryuqq.marketplace.application.selleraddress.dto.query.SellerAddressSearchParams;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressPageResult;

/** 셀러 주소 검색 UseCase. */
public interface SearchSellerAddressUseCase {

    SellerAddressPageResult execute(SellerAddressSearchParams params);
}
