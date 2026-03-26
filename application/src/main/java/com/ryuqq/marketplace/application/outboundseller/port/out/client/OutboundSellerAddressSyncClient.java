package com.ryuqq.marketplace.application.outboundseller.port.out.client;

import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;

public interface OutboundSellerAddressSyncClient {
    OutboundSellerSyncResult createSellerAddress(Shop shop, Long sellerId, Long addressId);

    OutboundSellerSyncResult updateSellerAddress(Shop shop, Long sellerId, Long addressId);

    OutboundSellerSyncResult deleteSellerAddress(Shop shop, Long sellerId, Long addressId);
}
