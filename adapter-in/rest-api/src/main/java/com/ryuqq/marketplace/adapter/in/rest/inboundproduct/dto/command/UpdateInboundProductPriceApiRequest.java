package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

/** 인바운드 상품 가격 수정 API Request. */
@Schema(description = "인바운드 상품 가격 수정 요청")
public record UpdateInboundProductPriceApiRequest(
        @Schema(description = "정가", example = "50000", requiredMode = Schema.RequiredMode.REQUIRED)
                @Min(value = 0, message = "정가는 0 이상이어야 합니다")
                int regularPrice,
        @Schema(description = "판매가", example = "45000", requiredMode = Schema.RequiredMode.REQUIRED)
                @Min(value = 0, message = "판매가는 0 이상이어야 합니다")
                int currentPrice) {}
