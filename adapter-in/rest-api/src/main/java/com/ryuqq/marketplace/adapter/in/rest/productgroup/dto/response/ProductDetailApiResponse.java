package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 상품(SKU) 상세 API 응답 DTO. */
@Schema(description = "상품(SKU) 상세 응답")
public record ProductDetailApiResponse(
        @Schema(description = "상품 ID") Long id,
        @Schema(description = "SKU 코드") String skuCode,
        @Schema(description = "정가") int regularPrice,
        @Schema(description = "현재가") int currentPrice,
        @Schema(description = "할인가", nullable = true) Integer salePrice,
        @Schema(description = "할인율") int discountRate,
        @Schema(description = "재고수량") int stockQuantity,
        @Schema(description = "상태") String status,
        @Schema(description = "정렬 순서") int sortOrder,
        @Schema(description = "옵션 매핑 목록") List<ResolvedProductOptionApiResponse> options,
        @Schema(description = "생성일시 (ISO 8601)") String createdAt,
        @Schema(description = "수정일시 (ISO 8601)") String updatedAt) {}
