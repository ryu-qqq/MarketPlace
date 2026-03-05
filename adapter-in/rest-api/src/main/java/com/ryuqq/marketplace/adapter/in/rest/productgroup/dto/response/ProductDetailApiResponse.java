package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 상품(SKU) 상세 API 응답 DTO. */
@Schema(description = "상품(SKU) 상세 응답")
public record ProductDetailApiResponse(
        @Schema(description = "상품 ID", example = "1") Long id,
        @Schema(description = "SKU 코드", example = "SKU-001") String skuCode,
        @Schema(description = "정가", example = "100000") int regularPrice,
        @Schema(description = "현재가", example = "89000") int currentPrice,
        @Schema(description = "할인율", example = "11") int discountRate,
        @Schema(description = "재고수량", example = "100") int stockQuantity,
        @Schema(description = "상태 (ACTIVE, INACTIVE, SOLD_OUT)", example = "ACTIVE") String status,
        @Schema(description = "정렬 순서", example = "0") int sortOrder,
        @Schema(description = "옵션 매핑 목록") List<ResolvedProductOptionApiResponse> options,
        @Schema(description = "생성일시 (ISO 8601)", example = "2026-01-15T10:30:00Z") String createdAt,
        @Schema(description = "수정일시 (ISO 8601)", example = "2026-01-20T14:00:00Z")
                String updatedAt) {}
