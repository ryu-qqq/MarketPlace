package com.ryuqq.marketplace.application.exchange.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxCommandManager;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxReadManager;
import com.ryuqq.marketplace.application.exchange.port.out.client.ExchangeOutboxPublishClient;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 교환 아웃박스 단건 Relay 처리기.
 *
 * <p>PENDING → startProcessing → SQS 발행 → complete / fail
 */
@Component
@ConditionalOnProperty(prefix = "sqs.queues", name = "exchange-outbox")
public class ExchangeOutboxRelayProcessor {

    private static final Logger log = LoggerFactory.getLogger(ExchangeOutboxRelayProcessor.class);

    private final ExchangeOutboxCommandManager outboxCommandManager;
    private final ExchangeOutboxReadManager outboxReadManager;
    private final ExchangeOutboxPublishClient publishClient;
    private final ObjectMapper objectMapper;
    private final ExchangeCommandFactory commandFactory;

    public ExchangeOutboxRelayProcessor(
            ExchangeOutboxCommandManager outboxCommandManager,
            ExchangeOutboxReadManager outboxReadManager,
            ExchangeOutboxPublishClient publishClient,
            ObjectMapper objectMapper,
            ExchangeCommandFactory commandFactory) {
        this.outboxCommandManager = outboxCommandManager;
        this.outboxReadManager = outboxReadManager;
        this.publishClient = publishClient;
        this.objectMapper = objectMapper;
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
    public boolean relay(ExchangeOutbox outbox) {
        StatusChangeContext<Long> ctx =
                commandFactory.createOutboxChangeContext(outbox.idValue());
        try {
            outbox.startProcessing(ctx.changedAt());
            outboxCommandManager.persist(outbox);

            String messageBody = buildMessageBody(outbox);
            publishClient.publish(messageBody);

            log.info(
                    "교환 Outbox SQS 발행 성공: outboxId={}, orderItemId={}",
                    outbox.idValue(),
                    outbox.orderItemIdValue());

            return true;
        } catch (Exception e) {
            log.error(
                    "교환 Outbox Relay 실패: outboxId={}, orderItemId={}, error={}",
                    outbox.idValue(),
                    outbox.orderItemIdValue(),
                    e.getMessage(),
                    e);
            try {
                StatusChangeContext<Long> failCtx =
                        commandFactory.createOutboxChangeContext(outbox.idValue());
                ExchangeOutbox freshOutbox = outboxReadManager.getById(failCtx.id());
                freshOutbox.recordFailure(true, "Relay 실패: " + e.getMessage(), failCtx.changedAt());
                outboxCommandManager.persist(freshOutbox);
            } catch (Exception reReadEx) {
                log.warn(
                        "교환 Outbox re-read 실패, 상태 변경 건너뜀: outboxId={}, error={}",
                        outbox.idValue(),
                        reReadEx.getMessage());
            }
            return false;
        }
    }

    private String buildMessageBody(ExchangeOutbox outbox) {
        try {
            Map<String, Object> message =
                    Map.of(
                            "outboxId", outbox.idValue(),
                            "orderItemId", outbox.orderItemIdValue(),
                            "outboxType", outbox.outboxType().name(),
                            "claimDomain", "EXCHANGE");
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("교환 Outbox SQS 메시지 직렬화 실패", e);
        }
    }
}
