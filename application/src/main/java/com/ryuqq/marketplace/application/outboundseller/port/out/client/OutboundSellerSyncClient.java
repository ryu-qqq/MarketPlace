package com.ryuqq.marketplace.application.outboundseller.port.out.client;

import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;

public interface OutboundSellerSyncClient {
    OutboundSellerSyncResult createSeller(Shop shop, Long sellerId);

    OutboundSellerSyncResult updateSeller(Shop shop, Long sellerId);
}
