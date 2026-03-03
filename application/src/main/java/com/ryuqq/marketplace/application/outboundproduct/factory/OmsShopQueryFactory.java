package com.ryuqq.marketplace.application.outboundproduct.factory;

import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsShopSearchParams;
import com.ryuqq.marketplace.application.shop.dto.query.ShopSearchParams;
import com.ryuqq.marketplace.application.shop.factory.ShopQueryFactory;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
import org.springframework.stereotype.Component;

/** OMS 쇼핑몰 Query Factory. OmsShopSearchParams → ShopSearchCriteria 변환. */
@Component
public class OmsShopQueryFactory {

    private final ShopQueryFactory shopQueryFactory;

    public OmsShopQueryFactory(ShopQueryFactory shopQueryFactory) {
        this.shopQueryFactory = shopQueryFactory;
    }

    public ShopSearchCriteria createCriteria(OmsShopSearchParams params) {
        ShopSearchParams shopParams = toShopSearchParams(params);
        return shopQueryFactory.createCriteria(shopParams);
    }

    private ShopSearchParams toShopSearchParams(OmsShopSearchParams params) {
        String searchField =
                (params.keyword() != null && !params.keyword().isBlank()) ? "SHOP_NAME" : null;
        return ShopSearchParams.of(
                null, null, searchField, params.keyword(), params.searchParams());
    }
}
