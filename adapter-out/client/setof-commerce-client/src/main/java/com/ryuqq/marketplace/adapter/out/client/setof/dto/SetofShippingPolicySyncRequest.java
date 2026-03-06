package com.ryuqq.marketplace.adapter.out.client.setof.dto;

public record SetofShippingPolicySyncRequest(
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
        int leadTimeMinDays,
        int leadTimeMaxDays) {}
