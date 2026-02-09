package com.ryuqq.marketplace.adapter.in.rest.shop.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** Shop 조회 응답 DTO. */
@Schema(description = "Shop 응답")
public record ShopApiResponse(
        @Schema(description = "Shop ID", example = "1") Long id,
        @Schema(description = "외부몰명", example = "쿠팡") String shopName,
        @Schema(description = "계정 ID", example = "coupang_account_01") String accountId,
        @Schema(description = "상태", example = "ACTIVE") String status,
        @Schema(description = "생성일시", example = "2025-01-23T10:30:00+09:00") String createdAt,
        @Schema(description = "수정일시", example = "2025-01-23T10:30:00+09:00") String updatedAt) {}
