package com.ryuqq.marketplace.application.outboundseller.port.out.client;

import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;

public interface OutboundRefundPolicySyncClient {
    OutboundSellerSyncResult createRefundPolicy(Long sellerId, Long policyId);

    OutboundSellerSyncResult updateRefundPolicy(Long sellerId, Long policyId);
}
