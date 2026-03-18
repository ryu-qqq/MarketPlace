package com.ryuqq.marketplace.application.claimsync.manager;

import com.ryuqq.marketplace.application.claimsync.port.out.query.ClaimSyncLogQueryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 클레임 동기화 로그 조회 Manager. */
@Component
public class ClaimSyncLogReadManager {

    private final ClaimSyncLogQueryPort queryPort;

    public ClaimSyncLogReadManager(ClaimSyncLogQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 동일한 (salesChannelId, externalProductOrderId, claimType, claimStatus) 조합이
     * 이미 처리되었는지 확인합니다.
     *
     * @param salesChannelId 판매채널 ID
     * @param externalProductOrderId 외부 주문상품번호
     * @param claimType 외부 클레임 유형
     * @param claimStatus 외부 클레임 상태
     * @return 이미 처리된 경우 true
     */
    @Transactional(readOnly = true)
    public boolean isAlreadyProcessed(
            long salesChannelId,
            String externalProductOrderId,
            String claimType,
            String claimStatus) {
        return queryPort.existsBySalesChannelIdAndExternalProductOrderIdAndClaimTypeAndClaimStatus(
                salesChannelId, externalProductOrderId, claimType, claimStatus);
    }
}
