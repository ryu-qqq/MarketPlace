package com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * RejectSellerApplicationApiRequest - 셀러 입점 신청 거절 요청 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-002: Jakarta Validation 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "셀러 입점 신청 거절 요청 DTO")
public record RejectSellerApplicationApiRequest(
        @Schema(description = "거절 사유", example = "서류 미비")
                @NotBlank(message = "거절 사유는 필수입니다.")
                @Size(max = 500, message = "거절 사유는 500자 이하여야 합니다.")
                String rejectionReason) {}
