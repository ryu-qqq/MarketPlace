package com.ryuqq.marketplace.application.shop.dto.response;

import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.time.Instant;

/** Shop 조회 결과 DTO. */
public record ShopResult(
        Long id,
        Long salesChannelId,
        String shopName,
        String accountId,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static ShopResult from(Shop shop) {
        return new ShopResult(
                shop.idValue(),
                shop.salesChannelId(),
                shop.shopName(),
                shop.accountId(),
                shop.status().name(),
                shop.createdAt(),
                shop.updatedAt());
    }
}
