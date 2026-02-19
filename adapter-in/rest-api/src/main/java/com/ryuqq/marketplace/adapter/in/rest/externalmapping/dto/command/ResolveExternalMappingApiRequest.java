package com.ryuqq.marketplace.adapter.in.rest.externalmapping.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** 외부 매핑 통합 조회 요청 DTO. */
@Schema(description = "외부 매핑 통합 조회 요청")
public record ResolveExternalMappingApiRequest(
        @Schema(
                        description = "외부 소스 코드",
                        example = "NAVER_COMMERCE",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "외부 소스 코드는 필수입니다")
                String externalSourceCode,
        @Schema(
                        description = "외부 브랜드 코드",
                        example = "NV_BRAND_001",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "외부 브랜드 코드는 필수입니다")
                String externalBrandCode,
        @Schema(
                        description = "외부 카테고리 코드",
                        example = "NV_CAT_001",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "외부 카테고리 코드는 필수입니다")
                String externalCategoryCode) {}
