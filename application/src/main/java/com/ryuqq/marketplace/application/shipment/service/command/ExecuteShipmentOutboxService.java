package com.ryuqq.marketplace.application.shipment.service.command;

import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.shipment.dto.command.ExecuteShipmentOutboxCommand;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxReadManager;
import com.ryuqq.marketplace.application.shipment.port.in.command.ExecuteShipmentOutboxUseCase;
import com.ryuqq.marketplace.application.shipment.port.out.client.ShipmentSyncStrategy;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 배송 Outbox 실행 서비스.
 *
 * <p>SQS Consumer에서 수신한 배송 Outbox를 처리합니다. Strategy 패턴으로 외부 API를 호출하고, 결과에 따라 Outbox 상태를 업데이트합니다.
 *
 * <p><strong>트랜잭션 전략</strong>: {@code @Transactional} 없음. 외부 API 호출이 포함되므로 상태 변경마다 별도 트랜잭션(Manager
 * 레벨)으로 커밋합니다. 낙관적 락 충돌 방지를 위해 re-read 패턴을 적용합니다.
 */
@Service
@ConditionalOnProperty(prefix = "sqs.queues", name = "shipment-outbox")
public class ExecuteShipmentOutboxService implements ExecuteShipmentOutboxUseCase {

    private static final Logger log = LoggerFactory.getLogger(ExecuteShipmentOutboxService.class);

    private final ShipmentOutboxReadManager outboxReadManager;
    private final ShipmentOutboxCommandManager outboxCommandManager;
    private final ShipmentSyncStrategy syncStrategy;

    public ExecuteShipmentOutboxService(
            ShipmentOutboxReadManager outboxReadManager,
            ShipmentOutboxCommandManager outboxCommandManager,
            ShipmentSyncStrategy syncStrategy) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
        this.syncStrategy = syncStrategy;
    }

    @Override
    public void execute(ExecuteShipmentOutboxCommand command) {
        ShipmentOutbox outbox = outboxReadManager.getById(command.outboxId());

        try {
            OutboxSyncResult result = syncStrategy.execute(outbox);

            if (result.isSuccess()) {
                handleSuccess(outbox);
            } else {
                handleFailure(outbox, result);
            }

        } catch (ExternalServiceUnavailableException e) {
            log.warn(
                    "배송 Outbox 외부 서비스 일시 장애 (deferRetry): outboxId={}, error={}",
                    command.outboxId(),
                    e.getMessage());
            handleDeferRetry(outbox);
        } catch (Exception e) {
            log.error(
                    "배송 Outbox 처리 실패: outboxId={}, error={}",
                    command.outboxId(),
                    e.getMessage(),
                    e);
            persistFailureWithReRead(outbox.idValue(), true, "실행 중 예외: " + e.getMessage());
        }
    }

    private void handleSuccess(ShipmentOutbox outbox) {
        ShipmentOutbox fresh = outboxReadManager.getById(outbox.idValue());
        fresh.complete(Instant.now());
        outboxCommandManager.persist(fresh);
    }

    private void handleFailure(ShipmentOutbox outbox, OutboxSyncResult result) {
        persistFailureWithReRead(outbox.idValue(), result.retryable(), result.errorMessage());
    }

    private void handleDeferRetry(ShipmentOutbox outbox) {
        try {
            ShipmentOutbox fresh = outboxReadManager.getById(outbox.idValue());
            fresh.recoverFromTimeout(Instant.now());
            outboxCommandManager.persist(fresh);
        } catch (Exception e) {
            log.warn("배송 Outbox deferRetry 실패: outboxId={}", outbox.idValue());
        }
    }

    private void persistFailureWithReRead(Long outboxId, boolean retryable, String errorMessage) {
        try {
            ShipmentOutbox fresh = outboxReadManager.getById(outboxId);
            fresh.recordFailure(retryable, errorMessage, Instant.now());
            outboxCommandManager.persist(fresh);
        } catch (Exception e) {
            log.warn(
                    "배송 Outbox re-read 실패, 상태 변경 건너뜀: outboxId={}, error={}",
                    outboxId,
                    e.getMessage());
        }
    }
}
