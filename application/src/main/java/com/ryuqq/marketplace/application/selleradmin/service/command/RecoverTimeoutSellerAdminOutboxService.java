package com.ryuqq.marketplace.application.selleradmin.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.selleradmin.dto.command.RecoverTimeoutSellerAdminOutboxCommand;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminAuthOutboxCommandManager;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminAuthOutboxReadManager;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.RecoverTimeoutSellerAdminOutboxUseCase;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RecoverTimeoutSellerAdminOutboxService - 타임아웃 셀러 관리자 인증 Outbox 복구 서비스.
 *
 * <p>PROCESSING 상태에서 타임아웃된 좀비 Outbox를 PENDING으로 복구합니다. 재처리는 다음 주기의
 * ProcessPendingSellerAdminOutboxService에서 수행됩니다.
 */
@Service
public class RecoverTimeoutSellerAdminOutboxService
        implements RecoverTimeoutSellerAdminOutboxUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RecoverTimeoutSellerAdminOutboxService.class);

    private final SellerAdminAuthOutboxReadManager outboxReadManager;
    private final SellerAdminAuthOutboxCommandManager outboxCommandManager;

    public RecoverTimeoutSellerAdminOutboxService(
            SellerAdminAuthOutboxReadManager outboxReadManager,
            SellerAdminAuthOutboxCommandManager outboxCommandManager) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    @Override
    @Transactional
    public SchedulerBatchProcessingResult execute(RecoverTimeoutSellerAdminOutboxCommand command) {
        List<SellerAdminAuthOutbox> outboxes =
                outboxReadManager.findProcessingTimeoutOutboxes(
                        command.timeoutThreshold(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;
        Instant now = Instant.now();

        for (SellerAdminAuthOutbox outbox : outboxes) {
            try {
                outbox.recoverFromTimeout(now);
                outboxCommandManager.persist(outbox);
                successCount++;
            } catch (Exception e) {
                log.error(
                        "셀러 관리자 인증 Outbox 복구 실패: outboxId={}, sellerAdminId={}, error={}",
                        outbox.idValue(),
                        outbox.sellerAdminIdValue(),
                        e.getMessage(),
                        e);
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
