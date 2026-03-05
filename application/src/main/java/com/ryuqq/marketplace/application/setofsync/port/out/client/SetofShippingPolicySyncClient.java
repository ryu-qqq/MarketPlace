package com.ryuqq.marketplace.application.setofsync.port.out.client;

import com.ryuqq.marketplace.application.setofsync.dto.response.SetofSyncResult;

public interface SetofShippingPolicySyncClient {
    SetofSyncResult createShippingPolicy(Long sellerId, Long policyId);

    SetofSyncResult updateShippingPolicy(Long sellerId, Long policyId);
}
