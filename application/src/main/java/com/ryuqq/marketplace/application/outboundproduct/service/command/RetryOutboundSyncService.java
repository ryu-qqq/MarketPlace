package com.ryuqq.marketplace.application.outboundproduct.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.outboundproduct.factory.OmsProductCommandFactory;
import com.ryuqq.marketplace.application.outboundproduct.port.in.command.RetryOutboundSyncUseCase;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxReadManager;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** FAILED 상태의 Outbox를 PENDING으로 재처리 요청하는 Service. */
@Service
public class RetryOutboundSyncService implements RetryOutboundSyncUseCase {

    private final OmsProductCommandFactory commandFactory;
    private final OutboundSyncOutboxReadManager outboxReadManager;
    private final OutboundSyncOutboxCommandManager outboxCommandManager;

    public RetryOutboundSyncService(
            OmsProductCommandFactory commandFactory,
            OutboundSyncOutboxReadManager outboxReadManager,
            OutboundSyncOutboxCommandManager outboxCommandManager) {
        this.commandFactory = commandFactory;
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    @Override
    @Transactional
    public void execute(long outboxId) {
        StatusChangeContext<Long> context = commandFactory.createRetryContext(outboxId);

        OutboundSyncOutbox outbox = outboxReadManager.getById(context.id());
        outbox.retry(context.changedAt());

        outboxCommandManager.persist(outbox);
    }
}
