package com.ryuqq.marketplace.application.selleradmin.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.selleradmin.dto.command.RecoverTimeoutEmailOutboxCommand;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminEmailOutboxCommandManager;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminEmailOutboxReadManager;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.RecoverTimeoutEmailOutboxUseCase;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RecoverTimeoutEmailOutboxService - 타임아웃 셀러 관리자 이메일 Outbox 복구 서비스.
 *
 * <p>PROCESSING 상태에서 타임아웃된 좀비 Outbox를 PENDING으로 복구합니다. 재처리는 다음 주기의
 * ProcessPendingEmailOutboxService에서 수행됩니다.
 */
@Service
public class RecoverTimeoutEmailOutboxService implements RecoverTimeoutEmailOutboxUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RecoverTimeoutEmailOutboxService.class);

    private final SellerAdminEmailOutboxReadManager outboxReadManager;
    private final SellerAdminEmailOutboxCommandManager outboxCommandManager;

    public RecoverTimeoutEmailOutboxService(
            SellerAdminEmailOutboxReadManager outboxReadManager,
            SellerAdminEmailOutboxCommandManager outboxCommandManager) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    @Override
    @Transactional
    public SchedulerBatchProcessingResult execute(RecoverTimeoutEmailOutboxCommand command) {
        List<SellerAdminEmailOutbox> outboxes =
                outboxReadManager.findProcessingTimeoutOutboxes(
                        command.timeoutThreshold(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;
        Instant now = Instant.now();

        for (SellerAdminEmailOutbox outbox : outboxes) {
            try {
                outbox.recoverFromTimeout(now);
                outboxCommandManager.persist(outbox);
                successCount++;
            } catch (Exception e) {
                log.error(
                        "셀러 관리자 이메일 Outbox 복구 실패: outboxId={}, sellerId={}, error={}",
                        outbox.idValue(),
                        outbox.sellerIdValue(),
                        e.getMessage(),
                        e);
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
