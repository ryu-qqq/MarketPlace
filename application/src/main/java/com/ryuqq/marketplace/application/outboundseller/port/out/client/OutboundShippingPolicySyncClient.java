package com.ryuqq.marketplace.application.outboundseller.port.out.client;

import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;

public interface OutboundShippingPolicySyncClient {
    OutboundSellerSyncResult createShippingPolicy(Shop shop, Long sellerId, Long policyId);

    OutboundSellerSyncResult updateShippingPolicy(Shop shop, Long sellerId, Long policyId);
}
