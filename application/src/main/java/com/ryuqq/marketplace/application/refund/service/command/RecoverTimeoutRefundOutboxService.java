package com.ryuqq.marketplace.application.refund.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.command.RecoverTimeoutRefundOutboxCommand;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxReadManager;
import com.ryuqq.marketplace.application.refund.port.in.command.RecoverTimeoutRefundOutboxUseCase;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 타임아웃 환불 아웃박스 복구 서비스. */
@Service
public class RecoverTimeoutRefundOutboxService implements RecoverTimeoutRefundOutboxUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RecoverTimeoutRefundOutboxService.class);

    private final RefundOutboxReadManager outboxReadManager;
    private final RefundOutboxCommandManager outboxCommandManager;
    private final RefundCommandFactory commandFactory;

    public RecoverTimeoutRefundOutboxService(
            RefundOutboxReadManager outboxReadManager,
            RefundOutboxCommandManager outboxCommandManager,
            RefundCommandFactory commandFactory) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
        this.commandFactory = commandFactory;
    }

    @Override
    public SchedulerBatchProcessingResult execute(RecoverTimeoutRefundOutboxCommand command) {
        Instant timeoutThreshold =
                commandFactory.calculateTimeoutThreshold(command.timeoutSeconds());
        List<RefundOutbox> outboxes =
                outboxReadManager.findProcessingTimeoutOutboxes(
                        timeoutThreshold, command.batchSize());

        int total = outboxes.size();
        int success = 0;
        int failed = 0;

        for (RefundOutbox outbox : outboxes) {
            try {
                StatusChangeContext<Long> ctx =
                        commandFactory.createOutboxChangeContext(outbox.idValue());
                outbox.recoverFromTimeout(ctx.changedAt());
                outboxCommandManager.persist(outbox);
                success++;
            } catch (Exception e) {
                log.warn(
                        "환불 Outbox 타임아웃 복구 실패: outboxId={}, error={}",
                        outbox.idValue(),
                        e.getMessage());
                failed++;
            }
        }

        return new SchedulerBatchProcessingResult(total, success, failed);
    }
}
