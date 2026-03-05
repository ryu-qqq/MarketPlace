package com.ryuqq.marketplace.application.setofsync.port.out.client;

import com.ryuqq.marketplace.application.setofsync.dto.response.SetofSyncResult;

public interface SetofSellerAddressSyncClient {
    SetofSyncResult createSellerAddress(Long sellerId, Long addressId);

    SetofSyncResult updateSellerAddress(Long sellerId, Long addressId);

    SetofSyncResult deleteSellerAddress(Long sellerId, Long addressId);
}
