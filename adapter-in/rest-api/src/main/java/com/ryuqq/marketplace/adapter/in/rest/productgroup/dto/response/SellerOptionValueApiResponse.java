package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 셀러 옵션 값 API 응답 DTO. */
@Schema(description = "셀러 옵션 값 응답")
public record SellerOptionValueApiResponse(
        @Schema(description = "옵션 값 ID") Long id,
        @Schema(description = "셀러 옵션 그룹 ID") Long sellerOptionGroupId,
        @Schema(description = "옵션 값명") String optionValueName,
        @Schema(description = "표준 옵션 값 ID", nullable = true) Long canonicalOptionValueId,
        @Schema(description = "정렬 순서") int sortOrder) {}
