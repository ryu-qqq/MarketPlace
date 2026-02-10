package com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 브랜드 프리셋 등록 API 요청 DTO. */
@Schema(description = "브랜드 프리셋 등록 요청")
public record RegisterBrandPresetApiRequest(
        @Schema(description = "Shop ID", example = "1") @NotNull Long shopId,
        @Schema(description = "판매채널 브랜드 ID", example = "10") @NotNull Long salesChannelBrandId,
        @Schema(description = "프리셋 이름", example = "나이키 전송용") @NotBlank String presetName) {}
