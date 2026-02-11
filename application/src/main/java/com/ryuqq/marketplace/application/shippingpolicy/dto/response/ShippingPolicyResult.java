package com.ryuqq.marketplace.application.shippingpolicy.dto.response;

import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import java.time.Instant;
import java.time.LocalTime;

/**
 * ShippingPolicyResult - 배송정책 조회 결과 DTO.
 *
 * <p>APP-DTO-001: Application Result는 record 타입 필수.
 *
 * <p>APP-DTO-002: Result는 Domain 객체에서 직접 변환.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ShippingPolicyResult(
        Long policyId,
        Long sellerId,
        String policyName,
        boolean defaultPolicy,
        boolean active,
        String shippingFeeType,
        String shippingFeeTypeDisplayName,
        Long baseFee,
        Long freeThreshold,
        Long jejuExtraFee,
        Long islandExtraFee,
        Long returnFee,
        Long exchangeFee,
        int leadTimeMinDays,
        int leadTimeMaxDays,
        LocalTime leadTimeCutoffTime,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Domain → Result 변환.
     *
     * @param domain ShippingPolicy 도메인 객체
     * @return ShippingPolicyResult
     */
    public static ShippingPolicyResult from(ShippingPolicy domain) {
        return new ShippingPolicyResult(
                domain.idValue(),
                domain.sellerIdValue(),
                domain.policyNameValue(),
                domain.isDefaultPolicy(),
                domain.isActive(),
                domain.shippingFeeType().name(),
                domain.shippingFeeType().displayName(),
                toNullableLong(domain.baseFeeValue()),
                toNullableLong(domain.freeThresholdValue()),
                toNullableLong(domain.jejuExtraFeeValue()),
                toNullableLong(domain.islandExtraFeeValue()),
                toNullableLong(domain.returnFeeValue()),
                toNullableLong(domain.exchangeFeeValue()),
                domain.leadTimeMinDays(),
                domain.leadTimeMaxDays(),
                domain.leadTimeCutoffTime(),
                domain.createdAt(),
                domain.updatedAt());
    }

    private static Long toNullableLong(Integer value) {
        return value != null ? value.longValue() : null;
    }
}
