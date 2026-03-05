package com.ryuqq.marketplace.application.outboundseller.port.out.client;

import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;

public interface OutboundSellerAddressSyncClient {
    OutboundSellerSyncResult createSellerAddress(Long sellerId, Long addressId);

    OutboundSellerSyncResult updateSellerAddress(Long sellerId, Long addressId);

    OutboundSellerSyncResult deleteSellerAddress(Long sellerId, Long addressId);
}
