package com.ryuqq.marketplace.application.outboundseller.service.command;

import com.ryuqq.marketplace.application.outboundseller.dto.command.RecoverTimeoutOutboundSellerCommand;
import com.ryuqq.marketplace.application.outboundseller.manager.OutboundSellerOutboxCommandManager;
import com.ryuqq.marketplace.application.outboundseller.manager.OutboundSellerOutboxReadManager;
import com.ryuqq.marketplace.application.outboundseller.port.in.command.RecoverTimeoutOutboundSellerUseCase;
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
public class RecoverTimeoutOutboundSellerService implements RecoverTimeoutOutboundSellerUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RecoverTimeoutOutboundSellerService.class);

    private final OutboundSellerOutboxReadManager readManager;
    private final OutboundSellerOutboxCommandManager commandManager;
    private final Clock clock;

    public RecoverTimeoutOutboundSellerService(
            OutboundSellerOutboxReadManager readManager,
            OutboundSellerOutboxCommandManager commandManager,
            Clock clock) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.clock = clock;
    }

    @Override
    public void execute(RecoverTimeoutOutboundSellerCommand command) {
        Instant timeoutThreshold = clock.instant().minusSeconds(command.timeoutSeconds());
        List<OutboundSellerOutbox> timeoutOutboxes =
                readManager.findProcessingTimeout(timeoutThreshold, command.batchSize());

        if (timeoutOutboxes.isEmpty()) {
            return;
        }

        log.info("Outbound seller timeout outbox 복구 시작. count={}", timeoutOutboxes.size());
        Instant now = clock.instant();

        for (OutboundSellerOutbox outbox : timeoutOutboxes) {
            try {
                outbox.recoverFromTimeout(now);
                commandManager.persist(outbox);
            } catch (Exception e) {
                log.error("Outbound seller outbox 타임아웃 복구 실패. outboxId={}", outbox.idValue(), e);
            }
        }
    }
}
