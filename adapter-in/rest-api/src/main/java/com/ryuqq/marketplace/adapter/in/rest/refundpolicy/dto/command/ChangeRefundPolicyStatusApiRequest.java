package com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 환불정책 다건 상태 변경 API 요청.
 *
 * <p>API-DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>API-DTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param policyIds 상태 변경할 정책 ID 목록
 * @param active 변경할 활성화 상태
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "환불정책 다건 상태 변경 요청")
public record ChangeRefundPolicyStatusApiRequest(
        @Schema(
                        description = "상태 변경할 정책 ID 목록",
                        example = "[1, 2, 3]",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty(message = "정책 ID 목록은 필수입니다")
                List<Long> policyIds,
        @Schema(
                        description = "변경할 활성화 상태 (true: 활성화, false: 비활성화)",
                        example = "false",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "활성화 상태는 필수입니다")
                Boolean active) {}
