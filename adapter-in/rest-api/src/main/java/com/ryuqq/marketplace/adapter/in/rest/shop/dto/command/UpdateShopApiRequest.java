package com.ryuqq.marketplace.adapter.in.rest.shop.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Shop 수정 API 요청 DTO. */
@Schema(description = "Shop 수정 요청")
public record UpdateShopApiRequest(
        @Schema(description = "외부몰명", example = "쿠팡") @NotBlank String shopName,
        @Schema(description = "계정 ID", example = "coupang_account_01") @NotBlank String accountId,
        @Schema(description = "상태 (ACTIVE, INACTIVE)", example = "ACTIVE")
                @NotBlank
                @Pattern(regexp = "ACTIVE|INACTIVE", message = "상태는 ACTIVE 또는 INACTIVE만 허용됩니다")
                String status) {}
