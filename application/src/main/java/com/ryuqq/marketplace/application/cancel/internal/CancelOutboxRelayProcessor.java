package com.ryuqq.marketplace.application.cancel.internal;

import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxCommandManager;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxReadManager;
import com.ryuqq.marketplace.application.cancel.port.out.client.CancelOutboxMessage;
import com.ryuqq.marketplace.application.cancel.port.out.client.CancelOutboxPublishClient;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 취소 아웃박스 단건 Relay 처리기.
 *
 * <p>PENDING → startProcessing → SQS 발행 → complete / fail
 */
@Component
@ConditionalOnProperty(prefix = "sqs.queues", name = "cancel-outbox")
public class CancelOutboxRelayProcessor {

    private static final Logger log = LoggerFactory.getLogger(CancelOutboxRelayProcessor.class);

    private final CancelOutboxCommandManager outboxCommandManager;
    private final CancelOutboxReadManager outboxReadManager;
    private final CancelOutboxPublishClient publishClient;
    private final CancelCommandFactory commandFactory;

    public CancelOutboxRelayProcessor(
            CancelOutboxCommandManager outboxCommandManager,
            CancelOutboxReadManager outboxReadManager,
            CancelOutboxPublishClient publishClient,
            CancelCommandFactory commandFactory) {
        this.outboxCommandManager = outboxCommandManager;
        this.outboxReadManager = outboxReadManager;
        this.publishClient = publishClient;
        this.commandFactory = commandFactory;
    }

    /**
     * 단건 Outbox를 SQS로 발행합니다.
     *
     * <p>PROCESSING 전환 + SQS 발행만 수행합니다. COMPLETED 전환은 SQS 컨슈머에서 실제 작업 완료 후 수행합니다.
     *
     * @param outbox 처리 대상 Outbox
     * @return 성공 여부
     */
    public boolean relay(CancelOutbox outbox) {
        Instant now = commandFactory.now();
        try {
            outbox.startProcessing(now);
            outboxCommandManager.persist(outbox);

            CancelOutboxMessage message =
                    CancelOutboxMessage.of(
                            outbox.idValue(),
                            outbox.orderItemIdValue(),
                            outbox.outboxType().name());
            publishClient.publish(message);

            log.info(
                    "취소 Outbox SQS 발행 성공: outboxId={}, orderItemId={}",
                    outbox.idValue(),
                    outbox.orderItemIdValue());

            return true;
        } catch (Exception e) {
            log.error(
                    "취소 Outbox Relay 실패: outboxId={}, orderItemId={}, error={}",
                    outbox.idValue(),
                    outbox.orderItemIdValue(),
                    e.getMessage(),
                    e);
            try {
                CancelOutbox freshOutbox = outboxReadManager.getById(outbox.idValue());
                freshOutbox.recordFailure(
                        true, "Relay 실패: " + e.getMessage(), commandFactory.now());
                outboxCommandManager.persist(freshOutbox);
            } catch (Exception reReadEx) {
                log.warn(
                        "취소 Outbox re-read 실패, 상태 변경 건너뜀: outboxId={}, error={}",
                        outbox.idValue(),
                        reReadEx.getMessage());
            }
            return false;
        }
    }
}
