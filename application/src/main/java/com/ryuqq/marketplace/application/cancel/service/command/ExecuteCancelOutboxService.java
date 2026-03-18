package com.ryuqq.marketplace.application.cancel.service.command;

import com.ryuqq.marketplace.application.cancel.dto.command.ExecuteCancelOutboxCommand;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxCommandManager;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxReadManager;
import com.ryuqq.marketplace.application.cancel.port.in.command.ExecuteCancelOutboxUseCase;
import com.ryuqq.marketplace.application.cancel.port.out.client.CancelClaimSyncStrategy;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 취소 Outbox 실행 서비스.
 *
 * <p>SQS Consumer에서 수신한 취소 Outbox를 처리합니다. Strategy 패턴으로 외부 API를 호출하고, 결과에 따라 Outbox 상태를 업데이트합니다.
 *
 * <p><strong>트랜잭션 전략</strong>: {@code @Transactional} 없음. 외부 API 호출이 포함되므로 상태 변경마다 별도 트랜잭션(Manager
 * 레벨)으로 커밋합니다. 낙관적 락 충돌 방지를 위해 re-read 패턴을 적용합니다.
 */
@Service
@ConditionalOnProperty(prefix = "sqs.queues", name = "cancel-outbox")
public class ExecuteCancelOutboxService implements ExecuteCancelOutboxUseCase {

    private static final Logger log = LoggerFactory.getLogger(ExecuteCancelOutboxService.class);

    private final CancelOutboxReadManager outboxReadManager;
    private final CancelOutboxCommandManager outboxCommandManager;
    private final CancelClaimSyncStrategy claimSyncStrategy;

    public ExecuteCancelOutboxService(
            CancelOutboxReadManager outboxReadManager,
            CancelOutboxCommandManager outboxCommandManager,
            CancelClaimSyncStrategy claimSyncStrategy) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
        this.claimSyncStrategy = claimSyncStrategy;
    }

    @Override
    public void execute(ExecuteCancelOutboxCommand command) {
        CancelOutbox outbox = outboxReadManager.getById(command.outboxId());

        try {
            OutboxSyncResult result = claimSyncStrategy.execute(outbox);

            if (result.isSuccess()) {
                handleSuccess(outbox);
            } else {
                handleFailure(outbox, result);
            }

        } catch (ExternalServiceUnavailableException e) {
            log.warn(
                    "취소 Outbox 외부 서비스 일시 장애 (deferRetry): outboxId={}, error={}",
                    command.outboxId(),
                    e.getMessage());
            handleDeferRetry(outbox);
        } catch (Exception e) {
            log.error(
                    "취소 Outbox 처리 실패: outboxId={}, error={}",
                    command.outboxId(),
                    e.getMessage(),
                    e);
            persistFailureWithReRead(outbox.idValue(), true, "실행 중 예외: " + e.getMessage());
        }
    }

    private void handleSuccess(CancelOutbox outbox) {
        CancelOutbox fresh = outboxReadManager.getById(outbox.idValue());
        fresh.complete(Instant.now());
        outboxCommandManager.persist(fresh);
    }

    private void handleFailure(CancelOutbox outbox, OutboxSyncResult result) {
        persistFailureWithReRead(outbox.idValue(), result.retryable(), result.errorMessage());
    }

    private void handleDeferRetry(CancelOutbox outbox) {
        try {
            CancelOutbox fresh = outboxReadManager.getById(outbox.idValue());
            fresh.recoverFromTimeout(Instant.now());
            outboxCommandManager.persist(fresh);
        } catch (Exception e) {
            log.warn("취소 Outbox deferRetry 실패: outboxId={}", outbox.idValue());
        }
    }

    private void persistFailureWithReRead(Long outboxId, boolean retryable, String errorMessage) {
        try {
            CancelOutbox fresh = outboxReadManager.getById(outboxId);
            fresh.recordFailure(retryable, errorMessage, Instant.now());
            outboxCommandManager.persist(fresh);
        } catch (Exception e) {
            log.warn(
                    "취소 Outbox re-read 실패, 상태 변경 건너뜀: outboxId={}, error={}",
                    outboxId,
                    e.getMessage());
        }
    }
}
