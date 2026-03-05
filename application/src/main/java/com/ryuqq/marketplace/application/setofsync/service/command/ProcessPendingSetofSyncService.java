package com.ryuqq.marketplace.application.setofsync.service.command;

import com.ryuqq.marketplace.application.setofsync.dto.command.ProcessPendingSetofSyncCommand;
import com.ryuqq.marketplace.application.setofsync.internal.SetofSyncOutboxProcessor;
import com.ryuqq.marketplace.application.setofsync.manager.SetofSyncOutboxReadManager;
import com.ryuqq.marketplace.application.setofsync.port.in.command.ProcessPendingSetofSyncUseCase;
import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class ProcessPendingSetofSyncService implements ProcessPendingSetofSyncUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessPendingSetofSyncService.class);

    private final SetofSyncOutboxReadManager readManager;
    private final SetofSyncOutboxProcessor processor;
    private final Clock clock;

    public ProcessPendingSetofSyncService(
            SetofSyncOutboxReadManager readManager,
            SetofSyncOutboxProcessor processor,
            Clock clock) {
        this.readManager = readManager;
        this.processor = processor;
        this.clock = clock;
    }

    @Override
    public void execute(ProcessPendingSetofSyncCommand command) {
        Instant beforeTime = clock.instant().minusSeconds(command.delaySeconds());
        List<SetofSyncOutbox> pendingOutboxes =
                readManager.findPendingForRetry(beforeTime, command.batchSize());

        if (pendingOutboxes.isEmpty()) {
            return;
        }

        log.info("Setof sync pending outbox 처리 시작. count={}", pendingOutboxes.size());

        for (SetofSyncOutbox outbox : pendingOutboxes) {
            try {
                processor.processOutbox(outbox);
            } catch (Exception e) {
                log.error("Setof sync outbox 처리 실패. outboxId={}", outbox.idValue(), e);
            }
        }
    }
}
