package com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** 공통 코드 타입 활성화/비활성화 API 요청. */
@Schema(description = "공통 코드 타입 활성화/비활성화 요청")
public record ChangeActiveStatusApiRequest(
        @Schema(
                        description = "대상 공통 코드 타입 ID 목록",
                        example = "[1, 2, 3]",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty(message = "ID 목록은 필수입니다")
                List<Long> ids,
        @Schema(
                        description = "활성화 여부 (true: 활성화, false: 비활성화)",
                        example = "true",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "활성화 여부는 필수입니다")
                Boolean active) {}
