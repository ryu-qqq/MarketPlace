package com.ryuqq.marketplace.adapter.in.rest.shop.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** Shop 수정 API 요청 DTO. */
@Schema(description = "Shop 수정 요청")
public record UpdateShopApiRequest(
        @Schema(description = "외부몰명", example = "쿠팡") @NotBlank String shopName,
        @Schema(description = "계정 ID", example = "coupang_account_01") @NotBlank String accountId,
        @Schema(description = "상태 (ACTIVE, INACTIVE)", example = "ACTIVE") String status) {}
