package com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * RegisterCommonCodeApiRequest - 공통 코드 등록 API 요청.
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
@Schema(description = "공통 코드 등록 요청")
public record RegisterCommonCodeApiRequest(
        @Schema(
                        description = "공통 코드 타입 ID",
                        example = "1",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "공통 코드 타입 ID는 필수입니다")
                Long commonCodeTypeId,
        @Schema(description = "코드", example = "CARD", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "코드는 필수입니다")
                @Size(max = 50, message = "코드는 50자 이하여야 합니다")
                String code,
        @Schema(description = "표시명", example = "신용카드", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "표시명은 필수입니다")
                @Size(max = 100, message = "표시명은 100자 이하여야 합니다")
                String displayName,
        @Schema(description = "표시 순서", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
                @Min(value = 0, message = "표시 순서는 0 이상이어야 합니다")
                int displayOrder) {}
