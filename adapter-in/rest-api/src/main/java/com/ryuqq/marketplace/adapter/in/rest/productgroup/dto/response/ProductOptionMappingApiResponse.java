package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 상품-옵션값 매핑 API 응답 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 */
@Schema(description = "상품-옵션값 매핑 응답")
public record ProductOptionMappingApiResponse(
        @Schema(description = "매핑 ID", example = "1") Long id,
        @Schema(description = "상품 ID", example = "1") Long productId,
        @Schema(description = "셀러 옵션값 ID", example = "10") Long sellerOptionValueId,
        @Schema(description = "옵션 그룹명", example = "색상") String optionGroupName,
        @Schema(description = "옵션 값명", example = "블랙") String optionValueName) {}
