package com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * RegisterRefundPolicyApiResponse - 환불정책 등록 API 응답.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-002: DTO 불변성 보장.
 *
 * @param policyId 생성된 정책 ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "환불정책 등록 응답")
public record RegisterRefundPolicyApiResponse(
        @Schema(description = "생성된 정책 ID", example = "1") Long policyId) {}
