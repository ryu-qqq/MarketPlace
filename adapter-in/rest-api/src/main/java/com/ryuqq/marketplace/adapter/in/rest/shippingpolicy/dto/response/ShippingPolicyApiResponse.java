package com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ShippingPolicyApiResponse - 배송정책 API 응답.
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
 * @param sellerId 셀러 ID
 * @param policyName 정책명
 * @param defaultPolicy 기본 정책 여부
 * @param active 활성화 상태
 * @param shippingFeeType 배송비 유형 코드
 * @param shippingFeeTypeDisplayName 배송비 유형 표시명
 * @param baseFee 기본 배송비
 * @param freeThreshold 무료배송 기준금액
 * @param jejuExtraFee 제주 추가배송비
 * @param islandExtraFee 도서산간 추가배송비
 * @param returnFee 반품 배송비
 * @param exchangeFee 교환 배송비
 * @param leadTimeMinDays 최소 배송일
 * @param leadTimeMaxDays 최대 배송일
 * @param leadTimeCutoffTime 당일 출고 마감시간 (HH:mm)
 * @param createdAt 생성일시 (ISO 8601)
 * @param updatedAt 수정일시 (ISO 8601)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "배송정책 응답")
public record ShippingPolicyApiResponse(
        @Schema(description = "정책 ID", example = "1") Long policyId,
        @Schema(description = "셀러 ID", example = "1") Long sellerId,
        @Schema(description = "정책명", example = "기본 배송정책") String policyName,
        @Schema(description = "기본 정책 여부", example = "true") boolean defaultPolicy,
        @Schema(description = "활성화 상태", example = "true") boolean active,
        @Schema(
                        description = "배송비 유형 코드 (FREE, PAID, CONDITIONAL_FREE, QUANTITY_BASED)",
                        example = "CONDITIONAL_FREE")
                String shippingFeeType,
        @Schema(description = "배송비 유형 표시명", example = "조건부 무료배송") String shippingFeeTypeDisplayName,
        @Schema(description = "기본 배송비", example = "3000") Long baseFee,
        @Schema(description = "무료배송 기준금액", example = "50000") Long freeThreshold,
        @Schema(description = "제주 추가배송비", example = "3000") Long jejuExtraFee,
        @Schema(description = "도서산간 추가배송비", example = "5000") Long islandExtraFee,
        @Schema(description = "반품 배송비", example = "3000") Long returnFee,
        @Schema(description = "교환 배송비", example = "6000") Long exchangeFee,
        @Schema(description = "최소 배송일", example = "1") int leadTimeMinDays,
        @Schema(description = "최대 배송일", example = "3") int leadTimeMaxDays,
        @Schema(description = "당일 출고 마감시간", example = "14:00") String leadTimeCutoffTime,
        @Schema(description = "생성일시 (ISO 8601)", example = "2025-01-26T10:30:00+09:00")
                String createdAt,
        @Schema(description = "수정일시 (ISO 8601)", example = "2025-01-26T10:30:00+09:00")
                String updatedAt) {}
