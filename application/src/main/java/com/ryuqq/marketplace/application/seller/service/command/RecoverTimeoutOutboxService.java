package com.ryuqq.marketplace.application.seller.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.seller.dto.command.RecoverTimeoutOutboxCommand;
import com.ryuqq.marketplace.application.seller.manager.SellerAuthOutboxCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerAuthOutboxReadManager;
import com.ryuqq.marketplace.application.seller.port.in.command.RecoverTimeoutOutboxUseCase;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RecoverTimeoutOutboxService - 타임아웃 Outbox 복구 서비스.
 *
 * <p>PROCESSING 상태에서 타임아웃된 좀비 Outbox를 PENDING으로 복구합니다. 재처리는 다음 주기의 ProcessPendingOutboxService에서
 * 수행됩니다.
 */
@Service
public class RecoverTimeoutOutboxService implements RecoverTimeoutOutboxUseCase {

    private static final Logger log = LoggerFactory.getLogger(RecoverTimeoutOutboxService.class);

    private final SellerAuthOutboxReadManager outboxReadManager;
    private final SellerAuthOutboxCommandManager outboxCommandManager;

    public RecoverTimeoutOutboxService(
            SellerAuthOutboxReadManager outboxReadManager,
            SellerAuthOutboxCommandManager outboxCommandManager) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    @Override
    @Transactional
    public SchedulerBatchProcessingResult execute(RecoverTimeoutOutboxCommand command) {
        List<SellerAuthOutbox> outboxes =
                outboxReadManager.findProcessingTimeoutOutboxes(
                        command.timeoutThreshold(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;
        Instant now = Instant.now();

        for (SellerAuthOutbox outbox : outboxes) {
            try {
                outbox.recoverFromTimeout(now);
                outboxCommandManager.persist(outbox);
                successCount++;
            } catch (Exception e) {
                log.error(
                        "Outbox 복구 실패: outboxId={}, sellerId={}, error={}",
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
