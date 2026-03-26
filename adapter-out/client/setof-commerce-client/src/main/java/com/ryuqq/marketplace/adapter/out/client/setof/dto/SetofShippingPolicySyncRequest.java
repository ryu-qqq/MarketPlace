package com.ryuqq.marketplace.adapter.out.client.setof.dto;

public record SetofShippingPolicySyncRequest(
        String policyName,
        boolean defaultPolicy,
        String shippingFeeType,
        Integer baseFee,
        Integer freeThreshold,
        Integer jejuExtraFee,
        Integer islandExtraFee,
        Integer returnFee,
        Integer exchangeFee,
        LeadTimeRequest leadTime) {

    /** 리드타임 중첩 객체 — 세토프 API 스펙 대응. */
    public record LeadTimeRequest(int minDays, int maxDays, String cutoffTime) {}
}
