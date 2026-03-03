package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** 상품 외부몰 전송 요청. */
@Schema(description = "상품 외부몰 전송 요청")
public record SyncProductsApiRequest(
        @Schema(
                        description = "전송 대상 상품그룹 ID 목록",
                        example = "[1, 2, 3]",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty
                List<Long> productIds,
        @Schema(
                        description = "프리셋 ID 목록",
                        example = "[10, 20]",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty
                List<Long> shopId) {}
