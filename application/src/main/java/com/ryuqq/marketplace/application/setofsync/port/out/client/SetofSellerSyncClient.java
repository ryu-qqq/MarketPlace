package com.ryuqq.marketplace.application.setofsync.port.out.client;

import com.ryuqq.marketplace.application.setofsync.dto.response.SetofSyncResult;

public interface SetofSellerSyncClient {
    SetofSyncResult createSeller(Long sellerId);

    SetofSyncResult updateSeller(Long sellerId);
}
