package com.ryuqq.marketplace.adapter.in.rest.shop.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Shop 등록 API 요청 DTO. */
@Schema(description = "Shop 등록 요청")
public record RegisterShopApiRequest(
        @Schema(description = "판매채널 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull
                Long salesChannelId,
        @Schema(description = "외부몰명", example = "쿠팡", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank
                String shopName,
        @Schema(
                        description = "계정 ID",
                        example = "coupang_account_01",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank
                String accountId) {}
