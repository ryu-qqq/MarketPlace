package com.ryuqq.marketplace.adapter.out.client.setof.dto;

import java.util.List;

public record SetofRefundPolicySyncRequest(
        Long id,
        Long sellerId,
        String policyName,
        boolean defaultPolicy,
        boolean active,
        int returnPeriodDays,
        int exchangePeriodDays,
        List<String> nonReturnableConditions,
        boolean partialRefundEnabled,
        boolean inspectionRequired,
        int inspectionPeriodDays,
        String additionalInfo) {

    public SetofRefundPolicySyncRequest {
        nonReturnableConditions =
                nonReturnableConditions == null ? List.of() : List.copyOf(nonReturnableConditions);
    }
}
