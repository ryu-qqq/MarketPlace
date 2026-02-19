package com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** 외부 소스 등록 요청 DTO. */
@Schema(description = "외부 소스 등록 요청")
public record RegisterExternalSourceApiRequest(
        @Schema(
                        description = "외부 소스 코드",
                        example = "NAVER_COMMERCE",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "외부 소스 코드는 필수입니다")
                String code,
        @Schema(
                        description = "외부 소스명",
                        example = "네이버 커머스",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "외부 소스명은 필수입니다")
                String name,
        @Schema(
                        description = "외부 소스 유형",
                        example = "SALES_CHANNEL",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "외부 소스 유형은 필수입니다")
                String type,
        @Schema(description = "설명", example = "네이버 커머스 연동") String description) {}
