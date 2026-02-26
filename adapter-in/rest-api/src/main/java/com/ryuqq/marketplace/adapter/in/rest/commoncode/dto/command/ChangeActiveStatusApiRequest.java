package com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * ChangeActiveStatusApiRequest - 공통 코드 활성화 상태 변경 API 요청.
 *
 * <p>API-REQ-001: Request DTO는 record로 정의.
 *
 * <p>API-REQ-002: Validation 어노테이션 필수.
 *
 * <p>API-REQ-003: OpenAPI @Schema 어노테이션 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "공통 코드 활성화 상태 변경 요청")
public record ChangeActiveStatusApiRequest(
        @Schema(
                        description = "공통 코드 ID 목록",
                        example = "[1, 2, 3]",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty(message = "공통 코드 ID 목록은 필수입니다")
                List<Long> ids,
        @Schema(
                        description = "활성화 여부 (true: 활성화, false: 비활성화)",
                        example = "true",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "활성화 여부는 필수입니다")
                Boolean active) {}
