package com.ryuqq.marketplace.application.selleradmin.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ProcessPendingSellerAdminOutboxCommand;
import com.ryuqq.marketplace.application.selleradmin.internal.SellerAdminAuthOutboxProcessor;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminAuthOutboxReadManager;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ProcessPendingSellerAdminOutboxUseCase;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * ProcessPendingSellerAdminOutboxService - 대기 중인 셀러 관리자 인증 Outbox 처리 서비스.
 *
 * <p>각 Outbox 처리는 SellerAdminAuthOutboxProcessor에서 수행됩니다.
 *
 * <p><strong>조건부 활성화</strong>: SellerAdminAuthOutboxProcessor가 존재할 때만 활성화됩니다.
 */
@Service
@ConditionalOnBean(SellerAdminAuthOutboxProcessor.class)
public class ProcessPendingSellerAdminOutboxService
        implements ProcessPendingSellerAdminOutboxUseCase {

    private final SellerAdminAuthOutboxReadManager outboxReadManager;
    private final SellerAdminAuthOutboxProcessor outboxProcessor;

    public ProcessPendingSellerAdminOutboxService(
            SellerAdminAuthOutboxReadManager outboxReadManager,
            SellerAdminAuthOutboxProcessor outboxProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.outboxProcessor = outboxProcessor;
    }

    @Override
    public SchedulerBatchProcessingResult execute(ProcessPendingSellerAdminOutboxCommand command) {
        List<SellerAdminAuthOutbox> outboxes =
                outboxReadManager.findPendingOutboxesForRetry(
                        command.beforeTime(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (SellerAdminAuthOutbox outbox : outboxes) {
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
