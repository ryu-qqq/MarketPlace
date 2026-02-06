package com.ryuqq.marketplace.application.shippingpolicy.dto.response;

import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import java.time.Instant;

/**
 * ShippingPolicyResult - 배송정책 조회 결과 DTO.
 *
 * <p>APP-DTO-001: Application Result는 record 타입 필수.
 *
 * <p>APP-DTO-002: Result는 Domain 객체에서 직접 변환.
 *
 * @param policyId 정책 ID
 * @param policyName 정책명
 * @param defaultPolicy 기본 정책 여부
 * @param active 활성화 상태
 * @param shippingFeeType 배송비 유형 코드
 * @param shippingFeeTypeDisplayName 배송비 유형 표시명
 * @param baseFee 기본 배송비
 * @param freeThreshold 무료배송 기준금액
 * @param createdAt 생성일시
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ShippingPolicyResult(
        Long policyId,
        String policyName,
        boolean defaultPolicy,
        boolean active,
        String shippingFeeType,
        String shippingFeeTypeDisplayName,
        Long baseFee,
        Long freeThreshold,
        Instant createdAt) {

    /**
     * Domain → Result 변환.
     *
     * @param domain ShippingPolicy 도메인 객체
     * @return ShippingPolicyResult
     */
    public static ShippingPolicyResult from(ShippingPolicy domain) {
        return new ShippingPolicyResult(
                domain.idValue(),
                domain.policyNameValue(),
                domain.isDefaultPolicy(),
                domain.isActive(),
                domain.shippingFeeType().name(),
                domain.shippingFeeType().displayName(),
                domain.baseFeeValue() != null ? domain.baseFeeValue().longValue() : null,
                domain.freeThresholdValue() != null
                        ? domain.freeThresholdValue().longValue()
                        : null,
                domain.createdAt());
    }
}
