package com.ryuqq.marketplace.application.outboundseller.port.out.client;

import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;

public interface OutboundSellerSyncClient {
    OutboundSellerSyncResult createSeller(Long sellerId);

    OutboundSellerSyncResult updateSeller(Long sellerId);
}
