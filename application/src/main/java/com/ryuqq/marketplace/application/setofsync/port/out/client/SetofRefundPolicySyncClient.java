package com.ryuqq.marketplace.application.setofsync.port.out.client;

import com.ryuqq.marketplace.application.setofsync.dto.response.SetofSyncResult;

public interface SetofRefundPolicySyncClient {
    SetofSyncResult createRefundPolicy(Long sellerId, Long policyId);

    SetofSyncResult updateRefundPolicy(Long sellerId, Long policyId);
}
