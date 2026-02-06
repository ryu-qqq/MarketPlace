package com.ryuqq.marketplace.application.seller.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.seller.dto.command.ProcessPendingOutboxCommand;
import com.ryuqq.marketplace.application.seller.internal.SellerAuthOutboxProcessor;
import com.ryuqq.marketplace.application.seller.manager.SellerAuthOutboxReadManager;
import com.ryuqq.marketplace.application.seller.port.in.command.ProcessPendingOutboxUseCase;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * ProcessPendingOutboxService - 대기 중인 Outbox 처리 서비스.
 *
 * <p>각 Outbox 처리는 SellerAuthOutboxProcessor에서 수행됩니다.
 *
 * <p><strong>조건부 활성화</strong>: SellerAuthOutboxProcessor가 존재할 때만 활성화됩니다.
 */
@Service
@ConditionalOnBean(SellerAuthOutboxProcessor.class)
public class ProcessPendingOutboxService implements ProcessPendingOutboxUseCase {

    private final SellerAuthOutboxReadManager outboxReadManager;
    private final SellerAuthOutboxProcessor outboxProcessor;

    public ProcessPendingOutboxService(
            SellerAuthOutboxReadManager outboxReadManager,
            SellerAuthOutboxProcessor outboxProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.outboxProcessor = outboxProcessor;
    }

    @Override
    public SchedulerBatchProcessingResult execute(ProcessPendingOutboxCommand command) {
        List<SellerAuthOutbox> outboxes =
                outboxReadManager.findPendingOutboxesForRetry(
                        command.beforeTime(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (SellerAuthOutbox outbox : outboxes) {
            boolean success = outboxProcessor.processOutbox(outbox);
            if (success) {
                successCount++;
            } else {
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
