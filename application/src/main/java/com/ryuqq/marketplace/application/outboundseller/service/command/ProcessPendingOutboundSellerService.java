package com.ryuqq.marketplace.application.outboundseller.service.command;

import com.ryuqq.marketplace.application.outboundseller.dto.command.ProcessPendingOutboundSellerCommand;
import com.ryuqq.marketplace.application.outboundseller.internal.OutboundSellerOutboxProcessor;
import com.ryuqq.marketplace.application.outboundseller.manager.OutboundSellerOutboxReadManager;
import com.ryuqq.marketplace.application.outboundseller.port.in.command.ProcessPendingOutboundSellerUseCase;
import com.ryuqq.marketplace.domain.outboundseller.aggregate.OutboundSellerOutbox;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "setof-commerce", name = "base-url")
public class ProcessPendingOutboundSellerService implements ProcessPendingOutboundSellerUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(ProcessPendingOutboundSellerService.class);

    private final OutboundSellerOutboxReadManager readManager;
    private final OutboundSellerOutboxProcessor processor;
    private final Clock clock;

    public ProcessPendingOutboundSellerService(
            OutboundSellerOutboxReadManager readManager,
            OutboundSellerOutboxProcessor processor,
            Clock clock) {
        this.readManager = readManager;
        this.processor = processor;
        this.clock = clock;
    }

    @Override
    public void execute(ProcessPendingOutboundSellerCommand command) {
        Instant beforeTime = clock.instant().minusSeconds(command.delaySeconds());
        List<OutboundSellerOutbox> pendingOutboxes =
                readManager.findPendingForRetry(beforeTime, command.batchSize());

        if (pendingOutboxes.isEmpty()) {
            return;
        }

        log.info("Outbound seller pending outbox 처리 시작. count={}", pendingOutboxes.size());

        for (OutboundSellerOutbox outbox : pendingOutboxes) {
            try {
                processor.processOutbox(outbox);
            } catch (Exception e) {
                log.error("Outbound seller outbox 처리 실패. outboxId={}", outbox.idValue(), e);
            }
        }
    }
}
