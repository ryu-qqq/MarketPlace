package com.ryuqq.marketplace.application.inboundorder.service.command;

import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.claimsync.dto.result.ClaimSyncResult;
import com.ryuqq.marketplace.application.claimsync.internal.ClaimSyncCoordinator;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.ReceiveClaimWebhookUseCase;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 클레임 웹훅 수신 서비스.
 *
 * <p>기존 ClaimSyncCoordinator를 재사용하여 취소/반품/철회를 처리합니다.
 */
@Service
public class ReceiveClaimWebhookService implements ReceiveClaimWebhookUseCase {

    private final ClaimSyncCoordinator coordinator;

    public ReceiveClaimWebhookService(ClaimSyncCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public ClaimSyncResult execute(List<ExternalClaimPayload> payloads, long salesChannelId) {
        return coordinator.syncAll(payloads, salesChannelId);
    }
}
