package com.ryuqq.marketplace.application.seller.port.in.query;

import com.ryuqq.marketplace.application.seller.dto.query.SellerSearchParams;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;

/**
 * 셀러 검색 UseCase (Offset 기반 페이징).
 *
 * <p>APP-UC-001: Offset 페이징은 Search{Domain}ByOffsetUseCase
 */
public interface SearchSellerByOffsetUseCase {

    SellerPageResult execute(SellerSearchParams params);
}
