package com.ryuqq.marketplace.adapter.in.rest.brandmapping.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** 브랜드 매핑 등록 API 요청 DTO. */
@Schema(description = "브랜드 매핑 등록 요청")
public record RegisterBrandMappingApiRequest(
        @Schema(description = "외부 채널 브랜드 ID", example = "1") @NotNull Long salesChannelBrandId,
        @Schema(description = "내부 브랜드 ID", example = "100") @NotNull Long internalBrandId) {}
