package com.ryuqq.marketplace.application.inboundorder.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.result.InboundOrderPollingResult;
import com.ryuqq.marketplace.application.inboundorder.internal.InboundOrderReceiveCoordinator;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.ReceiveOrderCreatedWebhookUseCase;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 주문 생성 웹훅 수신 서비스.
 *
 * <p>기존 InboundOrderReceiveCoordinator를 재사용하여 주문을 수신합니다.
 */
@Service
public class ReceiveOrderCreatedWebhookService implements ReceiveOrderCreatedWebhookUseCase {

    private final InboundOrderReceiveCoordinator coordinator;
    private final TimeProvider timeProvider;

    public ReceiveOrderCreatedWebhookService(
            InboundOrderReceiveCoordinator coordinator, TimeProvider timeProvider) {
        this.coordinator = coordinator;
        this.timeProvider = timeProvider;
    }

    @Override
    public InboundOrderPollingResult execute(
            List<ExternalOrderPayload> payloads, long salesChannelId, long shopId) {
        return coordinator.receiveAll(payloads, salesChannelId, shopId, timeProvider.now());
    }
}
