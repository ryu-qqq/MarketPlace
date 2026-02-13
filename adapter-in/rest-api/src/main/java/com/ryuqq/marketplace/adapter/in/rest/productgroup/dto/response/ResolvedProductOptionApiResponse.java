package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 상품에 매핑된 옵션 값의 resolved API 응답 DTO. */
@Schema(description = "옵션 매핑 응답")
public record ResolvedProductOptionApiResponse(
        @Schema(description = "옵션 그룹 ID") Long sellerOptionGroupId,
        @Schema(description = "옵션 그룹명") String optionGroupName,
        @Schema(description = "옵션 값 ID") Long sellerOptionValueId,
        @Schema(description = "옵션 값명") String optionValueName) {}
