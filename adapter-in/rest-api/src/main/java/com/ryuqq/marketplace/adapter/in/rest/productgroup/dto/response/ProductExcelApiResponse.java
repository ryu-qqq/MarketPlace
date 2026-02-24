package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 엑셀 다운로드용 상품(SKU) API 응답 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-005: 날짜 String 변환 필수.
 */
@Schema(description = "엑셀 다운로드용 상품(SKU) 응답")
public record ProductExcelApiResponse(
        @Schema(description = "상품 ID", example = "1") Long id,
        @Schema(description = "상품 그룹 ID", example = "1") Long productGroupId,
        @Schema(description = "SKU 코드", example = "SKU-001") String skuCode,
        @Schema(description = "정가", example = "30000") int regularPrice,
        @Schema(description = "현재가", example = "25000") int currentPrice,
        @Schema(description = "할인가", example = "20000") Integer salePrice,
        @Schema(description = "할인율", example = "16") int discountRate,
        @Schema(description = "재고 수량", example = "100") int stockQuantity,
        @Schema(description = "상태", example = "ACTIVE") String status,
        @Schema(description = "정렬 순서", example = "1") int sortOrder,
        @Schema(description = "옵션 매핑 목록") List<ProductOptionMappingApiResponse> optionMappings,
        @Schema(description = "생성일시 (ISO 8601)", example = "2026-01-15T10:30:00Z") String createdAt,
        @Schema(description = "수정일시 (ISO 8601)", example = "2026-01-20T14:00:00Z")
                String updatedAt) {}
