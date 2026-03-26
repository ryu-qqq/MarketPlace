package com.ryuqq.marketplace.application.outboundseller.port.out.client;

import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;

public interface OutboundRefundPolicySyncClient {
    OutboundSellerSyncResult createRefundPolicy(Shop shop, Long sellerId, Long policyId);

    OutboundSellerSyncResult updateRefundPolicy(Shop shop, Long sellerId, Long policyId);
}
