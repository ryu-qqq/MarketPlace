package com.ryuqq.marketplace.application.outboundseller.port.out.client;

import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;

public interface OutboundShippingPolicySyncClient {
    OutboundSellerSyncResult createShippingPolicy(Long sellerId, Long policyId);

    OutboundSellerSyncResult updateShippingPolicy(Long sellerId, Long policyId);
}
