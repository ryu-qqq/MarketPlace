package com.ryuqq.marketplace.adapter.in.rest.seller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * SellerIdApiResponse - 셀러 ID 응답 DTO.
 *
 * <p>생성된 셀러 ID를 반환합니다.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "셀러 ID 응답 DTO")
public record SellerIdApiResponse(@Schema(description = "셀러 ID", example = "1") Long sellerId) {

    public static SellerIdApiResponse of(Long sellerId) {
        return new SellerIdApiResponse(sellerId);
    }
}
