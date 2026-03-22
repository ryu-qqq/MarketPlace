package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response;

/** 세토프 ProductStatus 호환 응답 DTO. */
public record LegacyProductStatusResponse(String soldOutYn, String displayYn) {

    public static LegacyProductStatusResponse of(boolean soldOut, boolean display) {
        return new LegacyProductStatusResponse(soldOut ? "Y" : "N", display ? "Y" : "N");
    }
}
