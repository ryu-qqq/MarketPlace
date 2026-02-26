package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** 인바운드 상품 재고 수정 API Request. */
@Schema(description = "인바운드 상품 재고 수정 요청")
public record UpdateInboundProductStockApiRequest(
        @Schema(description = "재고 수정 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "재고 수정 목록은 필수입니다")
                @Valid
                List<StockEntry> stocks) {

    @Schema(description = "개별 상품 재고 수정")
    public record StockEntry(
            @Schema(
                            description = "상품 ID",
                            example = "1",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    long productId,
            @Schema(
                            description = "재고 수량",
                            example = "100",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
                    int stockQuantity) {}
}
