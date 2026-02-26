package com.ryuqq.marketplace.application.shop.assembler;

import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
import com.ryuqq.marketplace.application.shop.dto.response.ShopResult;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.util.List;
import org.springframework.stereotype.Component;

/** Shop Assembler. */
@Component
public class ShopAssembler {

    public ShopResult toResult(Shop shop) {
        return ShopResult.from(shop);
    }

    public List<ShopResult> toResults(List<Shop> shops) {
        return shops.stream().map(this::toResult).toList();
    }

    public ShopPageResult toPageResult(List<Shop> shops, int page, int size, long totalElements) {
        List<ShopResult> results = toResults(shops);
        return ShopPageResult.of(results, page, size, totalElements);
    }
}
