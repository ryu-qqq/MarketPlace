package com.ryuqq.marketplace.application.outboundsync.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.outboundsync.port.out.client.OutboundSyncPublishClient;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * OutboundSync Outbox 단건 Relay 처리기.
 *
 * <p>PENDING → startProcessing → SQS 발행 → complete / fail
 */
@Component
@ConditionalOnProperty(prefix = "sqs.queues", name = "outbound-sync")
public class OutboundSyncRelayProcessor {

    private static final Logger log = LoggerFactory.getLogger(OutboundSyncRelayProcessor.class);

    private final OutboundSyncOutboxCommandManager outboxCommandManager;
    private final OutboundSyncPublishClient publishClient;
    private final ObjectMapper objectMapper;

    public OutboundSyncRelayProcessor(
            OutboundSyncOutboxCommandManager outboxCommandManager,
            OutboundSyncPublishClient publishClient,
            ObjectMapper objectMapper) {
        this.outboxCommandManager = outboxCommandManager;
        this.publishClient = publishClient;
        this.objectMapper = objectMapper;
    }

    /**
     * 단건 Outbox를 SQS로 발행합니다.
     *
     * <p>PROCESSING 전환 + SQS 발행만 수행합니다. COMPLETED 전환은 SQS 컨슈머(ExecuteOutboundSyncService)에서 실제 작업
     * 완료 후 수행합니다.
     *
     * @param outbox 처리 대상 Outbox
     * @return 성공 여부
     */
    public boolean relay(OutboundSyncOutbox outbox) {
        Instant now = Instant.now();
        try {
            outbox.startProcessing(now);
            outboxCommandManager.persist(outbox);

            String messageBody = buildMessageBody(outbox);
            publishClient.publish(messageBody);

            log.info(
                    "OutboundSync Outbox SQS 발행 성공: outboxId={}, productGroupId={}",
                    outbox.idValue(),
                    outbox.productGroupIdValue());

            return true;
        } catch (Exception e) {
            log.error(
                    "OutboundSync Outbox Relay 실패: outboxId={}, productGroupId={}, error={}",
                    outbox.idValue(),
                    outbox.productGroupIdValue(),
                    e.getMessage(),
                    e);
            outbox.recordFailure(true, "Relay 실패: " + e.getMessage(), Instant.now());
            outboxCommandManager.persist(outbox);
            return false;
        }
    }

    private String buildMessageBody(OutboundSyncOutbox outbox) {
        try {
            Map<String, Object> message =
                    Map.of(
                            "outboxId", outbox.idValue(),
                            "productGroupId", outbox.productGroupIdValue(),
                            "salesChannelId", outbox.salesChannelIdValue(),
                            "syncType", outbox.syncType().name());
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("SQS 메시지 직렬화 실패", e);
        }
    }
}
