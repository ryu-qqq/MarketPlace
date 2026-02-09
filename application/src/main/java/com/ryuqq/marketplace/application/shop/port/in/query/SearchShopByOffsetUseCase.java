package com.ryuqq.marketplace.application.shop.port.in.query;

import com.ryuqq.marketplace.application.shop.dto.query.ShopSearchParams;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;

/** Shop 검색 UseCase (Offset 기반 페이징). */
public interface SearchShopByOffsetUseCase {
    ShopPageResult execute(ShopSearchParams params);
}
