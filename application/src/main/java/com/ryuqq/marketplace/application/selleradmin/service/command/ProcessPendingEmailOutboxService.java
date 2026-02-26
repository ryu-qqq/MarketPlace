package com.ryuqq.marketplace.application.selleradmin.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ProcessPendingEmailOutboxCommand;
import com.ryuqq.marketplace.application.selleradmin.internal.SellerAdminEmailOutboxProcessor;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminEmailOutboxReadManager;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ProcessPendingEmailOutboxUseCase;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * ProcessPendingEmailOutboxService - 대기 중인 셀러 관리자 이메일 Outbox 처리 서비스.
 *
 * <p>각 Outbox 처리는 SellerAdminEmailOutboxProcessor에서 수행됩니다.
 *
 * <p><strong>조건부 활성화</strong>: SellerAdminEmailOutboxProcessor가 존재할 때만 활성화됩니다.
 */
@Service
@ConditionalOnProperty(prefix = "ses", name = "sender-email")
public class ProcessPendingEmailOutboxService implements ProcessPendingEmailOutboxUseCase {

    private final SellerAdminEmailOutboxReadManager outboxReadManager;
    private final SellerAdminEmailOutboxProcessor outboxProcessor;

    public ProcessPendingEmailOutboxService(
            SellerAdminEmailOutboxReadManager outboxReadManager,
            SellerAdminEmailOutboxProcessor outboxProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.outboxProcessor = outboxProcessor;
    }

    @Override
    public SchedulerBatchProcessingResult execute(ProcessPendingEmailOutboxCommand command) {
        List<SellerAdminEmailOutbox> outboxes =
                outboxReadManager.findPendingOutboxesForRetry(
                        command.beforeTime(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (SellerAdminEmailOutbox outbox : outboxes) {
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
