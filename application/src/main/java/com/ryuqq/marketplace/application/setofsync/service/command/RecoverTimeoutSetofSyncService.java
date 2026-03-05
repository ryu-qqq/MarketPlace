package com.ryuqq.marketplace.application.setofsync.service.command;

import com.ryuqq.marketplace.application.setofsync.dto.command.RecoverTimeoutSetofSyncCommand;
import com.ryuqq.marketplace.application.setofsync.manager.SetofSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.setofsync.manager.SetofSyncOutboxReadManager;
import com.ryuqq.marketplace.application.setofsync.port.in.command.RecoverTimeoutSetofSyncUseCase;
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
public class RecoverTimeoutSetofSyncService implements RecoverTimeoutSetofSyncUseCase {

    private static final Logger log = LoggerFactory.getLogger(RecoverTimeoutSetofSyncService.class);

    private final SetofSyncOutboxReadManager readManager;
    private final SetofSyncOutboxCommandManager commandManager;
    private final Clock clock;

    public RecoverTimeoutSetofSyncService(
            SetofSyncOutboxReadManager readManager,
            SetofSyncOutboxCommandManager commandManager,
            Clock clock) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.clock = clock;
    }

    @Override
    public void execute(RecoverTimeoutSetofSyncCommand command) {
        Instant timeoutThreshold = clock.instant().minusSeconds(command.timeoutSeconds());
        List<SetofSyncOutbox> timeoutOutboxes =
                readManager.findProcessingTimeout(timeoutThreshold, command.batchSize());

        if (timeoutOutboxes.isEmpty()) {
            return;
        }

        log.info("Setof sync timeout outbox 복구 시작. count={}", timeoutOutboxes.size());
        Instant now = clock.instant();

        for (SetofSyncOutbox outbox : timeoutOutboxes) {
            try {
                outbox.recoverFromTimeout(now);
                commandManager.persist(outbox);
            } catch (Exception e) {
                log.error("Setof sync outbox 타임아웃 복구 실패. outboxId={}", outbox.idValue(), e);
            }
        }
    }
}
