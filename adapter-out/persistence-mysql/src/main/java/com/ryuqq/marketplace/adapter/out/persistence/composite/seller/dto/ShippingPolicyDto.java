package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto;

import java.time.Instant;
import java.time.LocalTime;

/**
 * ShippingPolicyDto - 배송 정책 DTO.
 *
 * <p>SellerPolicyCompositeDto 내부에서 사용.
 */
public record ShippingPolicyDto(
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
