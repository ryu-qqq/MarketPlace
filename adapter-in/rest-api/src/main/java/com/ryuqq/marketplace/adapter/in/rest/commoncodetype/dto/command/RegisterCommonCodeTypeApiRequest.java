package com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 공통 코드 타입 등록 API 요청. */
@Schema(description = "공통 코드 타입 등록 요청")
public record RegisterCommonCodeTypeApiRequest(
        @Schema(
                        description = "코드 (영문 대문자, 언더스코어)",
                        example = "PAYMENT_METHOD",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "코드는 필수입니다")
                @Size(max = 50, message = "코드는 50자 이하여야 합니다")
                String code,
        @Schema(description = "이름", example = "결제수단", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "이름은 필수입니다")
                @Size(max = 100, message = "이름은 100자 이하여야 합니다")
                String name,
        @Schema(description = "설명", example = "결제 시 사용 가능한 결제수단 목록")
                @Size(max = 500, message = "설명은 500자 이하여야 합니다")
                String description,
        @Schema(description = "표시 순서", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "표시 순서는 필수입니다")
                @Min(value = 0, message = "표시 순서는 0 이상이어야 합니다")
                Integer displayOrder) {}
