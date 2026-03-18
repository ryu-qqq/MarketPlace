package com.ryuqq.marketplace.application.claimsync.port.out.query;

/** 클레임 동기화 로그 조회 포트. */
public interface ClaimSyncLogQueryPort {

    boolean existsBySalesChannelIdAndExternalProductOrderIdAndClaimTypeAndClaimStatus(
            long salesChannelId,
            String externalProductOrderId,
            String externalClaimType,
            String externalClaimStatus);
}
