package com.ryuqq.marketplace.application.outboundproduct.port.in.query;

import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsShopSearchParams;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;

/** OMS 쇼핑몰 검색 UseCase (Offset 기반 페이징). */
public interface SearchOmsShopsByOffsetUseCase {

    ShopPageResult execute(OmsShopSearchParams params);
}
