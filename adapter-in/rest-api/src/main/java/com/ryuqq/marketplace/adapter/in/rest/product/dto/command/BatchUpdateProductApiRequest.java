package com.ryuqq.marketplace.adapter.in.rest.product.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 상품(SKU) 배치 가격/재고 수정 API 요청.
 *
 * <p>API-DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>API-DTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param items 수정할 상품 항목 목록 (최대 100건)
 */
@Schema(description = "상품 배치 가격/재고 수정 요청")
public record BatchUpdateProductApiRequest(
        @Schema(description = "수정할 상품 항목 목록 (최대 100건)")
                @NotEmpty(message = "수정할 상품 목록은 필수입니다")
                @Size(max = 100, message = "한 번에 최대 100건까지 수정할 수 있습니다")
                @Valid
                List<Entry> items) {

    /**
     * 개별 상품 수정 항목.
     *
     * @param productId 상품 ID
     * @param regularPrice 정가
     * @param currentPrice 판매가 (정가 이하)
     * @param stockQuantity 재고 수량 (0 이상)
     */
    @Schema(description = "상품 수정 항목")
    public record Entry(
            @Schema(description = "상품 ID", example = "101") @NotNull(message = "상품 ID는 필수입니다")
                    Long productId,
            @Schema(description = "정가", example = "50000") @NotNull(message = "정가는 필수입니다")
                    Integer regularPrice,
            @Schema(description = "판매가 (정가 이하)", example = "45000") @NotNull(message = "판매가는 필수입니다")
                    Integer currentPrice,
            @Schema(description = "재고 수량 (0 이상)", example = "50")
                    @NotNull(message = "재고 수량은 필수입니다")
                    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
                    Integer stockQuantity) {}
}
