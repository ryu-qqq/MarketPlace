package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 상품에 매핑된 옵션 값의 resolved API 응답 DTO. */
@Schema(description = "옵션 매핑 응답")
public record ResolvedProductOptionApiResponse(
        @Schema(description = "옵션 그룹 ID", example = "1") Long sellerOptionGroupId,
        @Schema(description = "옵션 그룹명", example = "색상") String optionGroupName,
        @Schema(description = "옵션 값 ID", example = "1") Long sellerOptionValueId,
        @Schema(description = "옵션 값명", example = "블랙") String optionValueName) {}
