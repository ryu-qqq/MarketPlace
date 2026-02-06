package com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * RefundPolicyApiResponse - 환불정책 API 응답.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-002: DTO 불변성 보장.
 *
 * <p>API-DTO-004: createdAt 필수.
 *
 * <p>API-DTO-005: 날짜 String 변환 필수 (Instant 타입 사용 금지).
 *
 * <p>Response DTO는 Domain 객체 의존 금지 → Result만 의존해야 하며, 변환은 Mapper에서 수행합니다.
 *
 * @param policyId 정책 ID
 * @param policyName 정책명
 * @param defaultPolicy 기본 정책 여부
 * @param active 활성화 상태
 * @param returnPeriodDays 반품 가능 기간
 * @param exchangePeriodDays 교환 가능 기간
 * @param nonReturnableConditions 반품 불가 조건 목록
 * @param createdAt 생성일시 (ISO 8601)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "환불정책 응답")
public record RefundPolicyApiResponse(
        @Schema(description = "정책 ID", example = "1") Long policyId,
        @Schema(description = "정책명", example = "기본 환불정책") String policyName,
        @Schema(description = "기본 정책 여부", example = "true") boolean defaultPolicy,
        @Schema(description = "활성화 상태", example = "true") boolean active,
        @Schema(description = "반품 가능 기간 (일)", example = "7") int returnPeriodDays,
        @Schema(description = "교환 가능 기간 (일)", example = "7") int exchangePeriodDays,
        @Schema(description = "반품 불가 조건 목록")
                List<NonReturnableConditionApiResponse> nonReturnableConditions,
        @Schema(description = "생성일시 (ISO 8601)", example = "2025-01-26T10:30:00+09:00")
                String createdAt) {}
