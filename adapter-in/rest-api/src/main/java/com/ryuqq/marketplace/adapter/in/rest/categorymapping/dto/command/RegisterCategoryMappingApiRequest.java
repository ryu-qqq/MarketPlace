package com.ryuqq.marketplace.adapter.in.rest.categorymapping.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** 카테고리 매핑 등록 API 요청 DTO. */
@Schema(description = "카테고리 매핑 등록 요청")
public record RegisterCategoryMappingApiRequest(
        @Schema(description = "외부 채널 카테고리 ID", example = "1") @NotNull Long salesChannelCategoryId,
        @Schema(description = "내부 카테고리 ID", example = "100") @NotNull Long internalCategoryId) {}
