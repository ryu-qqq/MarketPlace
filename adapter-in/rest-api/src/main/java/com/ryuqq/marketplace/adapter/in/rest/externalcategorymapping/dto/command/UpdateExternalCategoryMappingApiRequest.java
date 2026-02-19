package com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;

/** 외부 카테고리 매핑 수정 요청 DTO. */
@Schema(description = "외부 카테고리 매핑 수정 요청")
public record UpdateExternalCategoryMappingApiRequest(
        @Schema(description = "외부 카테고리명", example = "남성의류") String externalCategoryName,
        @Schema(description = "내부 카테고리 ID", example = "1") Long internalCategoryId,
        @Schema(description = "상태", example = "ACTIVE") String status) {}
