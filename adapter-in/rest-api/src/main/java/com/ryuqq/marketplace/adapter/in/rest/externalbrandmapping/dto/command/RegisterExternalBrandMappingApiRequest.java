package com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 외부 브랜드 매핑 등록 요청 DTO. */
@Schema(description = "외부 브랜드 매핑 등록 요청")
public record RegisterExternalBrandMappingApiRequest(
        @Schema(
                        description = "외부 브랜드 코드",
                        example = "NV_BRAND_001",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "외부 브랜드 코드는 필수입니다")
                String externalBrandCode,
        @Schema(
                        description = "외부 브랜드명",
                        example = "나이키",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "외부 브랜드명은 필수입니다")
                String externalBrandName,
        @Schema(
                        description = "내부 브랜드 ID",
                        example = "1",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "내부 브랜드 ID는 필수입니다")
                Long internalBrandId) {}
