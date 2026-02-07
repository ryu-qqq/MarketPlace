package com.ryuqq.marketplace.application.seller.dto.composite;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

/**
 * SellerPolicyCompositeResult - 셀러 정책 Composite 조회 결과.
 *
 * <p>sellerId와 함께 ShippingPolicy, RefundPolicy 목록을 포함.
 */
public record SellerPolicyCompositeResult(
        Long sellerId,
        List<ShippingPolicyInfo> shippingPolicies,
        List<RefundPolicyInfo> refundPolicies) {

    public record ShippingPolicyInfo(
            Long id,
            Long sellerId,
            String policyName,
            boolean defaultPolicy,
            boolean active,
            String shippingFeeType,
            Integer baseFee,
            Integer freeThreshold,
            Integer jejuExtraFee,
            Integer islandExtraFee,
            Integer returnFee,
            Integer exchangeFee,
            Integer leadTimeMinDays,
            Integer leadTimeMaxDays,
            LocalTime leadTimeCutoffTime,
            Instant createdAt,
            Instant updatedAt) {}

    public record RefundPolicyInfo(
            Long id,
            Long sellerId,
            String policyName,
            boolean defaultPolicy,
            boolean active,
            int returnPeriodDays,
            int exchangePeriodDays,
            String nonReturnableConditions,
            boolean partialRefundEnabled,
            boolean inspectionRequired,
            int inspectionPeriodDays,
            String additionalInfo,
            Instant createdAt,
            Instant updatedAt) {}
}
