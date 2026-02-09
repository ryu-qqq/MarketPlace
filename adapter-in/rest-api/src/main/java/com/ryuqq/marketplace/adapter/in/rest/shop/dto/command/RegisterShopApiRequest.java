package com.ryuqq.marketplace.adapter.in.rest.shop.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** Shop 등록 API 요청 DTO. */
@Schema(description = "Shop 등록 요청")
public record RegisterShopApiRequest(
        @Schema(description = "외부몰명", example = "쿠팡") @NotBlank String shopName,
        @Schema(description = "계정 ID", example = "coupang_account_01") @NotBlank
                String accountId) {}
