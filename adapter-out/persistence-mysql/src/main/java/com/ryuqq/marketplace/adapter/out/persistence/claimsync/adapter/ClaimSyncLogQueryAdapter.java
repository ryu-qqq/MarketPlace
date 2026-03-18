package com.ryuqq.marketplace.adapter.out.persistence.claimsync.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.claimsync.repository.ClaimSyncLogQueryDslRepository;
import com.ryuqq.marketplace.application.claimsync.port.out.query.ClaimSyncLogQueryPort;
import org.springframework.stereotype.Component;

/** 클레임 동기화 로그 Query Adapter. */
@Component
public class ClaimSyncLogQueryAdapter implements ClaimSyncLogQueryPort {

    private final ClaimSyncLogQueryDslRepository queryDslRepository;

    public ClaimSyncLogQueryAdapter(ClaimSyncLogQueryDslRepository queryDslRepository) {
        this.queryDslRepository = queryDslRepository;
    }

    @Override
    public boolean existsBySalesChannelIdAndExternalProductOrderIdAndClaimTypeAndClaimStatus(
            long salesChannelId,
            String externalProductOrderId,
            String externalClaimType,
            String externalClaimStatus) {
        return queryDslRepository.exists(
                salesChannelId, externalProductOrderId, externalClaimType, externalClaimStatus);
    }
}
