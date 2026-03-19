package com.ryuqq.marketplace.application.cancel.service.command;

import com.ryuqq.marketplace.application.cancel.dto.command.RecoverTimeoutCancelOutboxCommand;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxCommandManager;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxReadManager;
import com.ryuqq.marketplace.application.cancel.port.in.command.RecoverTimeoutCancelOutboxUseCase;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 타임아웃 취소 아웃박스 복구 서비스. */
@Service
public class RecoverTimeoutCancelOutboxService implements RecoverTimeoutCancelOutboxUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RecoverTimeoutCancelOutboxService.class);

    private final CancelOutboxReadManager outboxReadManager;
    private final CancelOutboxCommandManager outboxCommandManager;
    private final CancelCommandFactory commandFactory;

    public RecoverTimeoutCancelOutboxService(
            CancelOutboxReadManager outboxReadManager,
            CancelOutboxCommandManager outboxCommandManager,
            CancelCommandFactory commandFactory) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
        this.commandFactory = commandFactory;
    }

    @Override
    public SchedulerBatchProcessingResult execute(RecoverTimeoutCancelOutboxCommand command) {
        Instant timeoutThreshold =
                commandFactory.calculateTimeoutThreshold(command.timeoutSeconds());
        List<CancelOutbox> outboxes =
                outboxReadManager.findProcessingTimeoutOutboxes(
                        timeoutThreshold, command.batchSize());

        int total = outboxes.size();
        int success = 0;
        int failed = 0;

        for (CancelOutbox outbox : outboxes) {
            try {
                outbox.recoverFromTimeout(commandFactory.now());
                outboxCommandManager.persist(outbox);
                success++;
            } catch (Exception e) {
                log.warn(
                        "취소 Outbox 타임아웃 복구 실패: outboxId={}, error={}",
                        outbox.idValue(),
                        e.getMessage());
                failed++;
            }
        }

        return new SchedulerBatchProcessingResult(total, success, failed);
    }
}
