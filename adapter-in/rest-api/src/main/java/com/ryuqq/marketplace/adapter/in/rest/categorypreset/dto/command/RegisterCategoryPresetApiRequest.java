package com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 카테고리 프리셋 등록 API 요청 DTO. */
@Schema(description = "카테고리 프리셋 등록 요청")
public record RegisterCategoryPresetApiRequest(
        @Schema(description = "Shop ID", example = "1") @NotNull Long shopId,
        @Schema(description = "프리셋 이름", example = "식품 - 과자류 전송용") @NotBlank String presetName,
        @Schema(description = "외부 카테고리 코드", example = "50000123") @NotBlank String categoryCode) {}
