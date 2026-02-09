package com.ryuqq.marketplace.adapter.in.rest.shop.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** Shop ID 응답 DTO. */
@Schema(description = "Shop ID 응답")
public record ShopIdApiResponse(@Schema(description = "Shop ID", example = "1") Long shopId) {

    public static ShopIdApiResponse of(Long shopId) {
        return new ShopIdApiResponse(shopId);
    }
}
