package com.ryuqq.marketplace.application.qna.internal;

import com.ryuqq.marketplace.application.qna.manager.QnaOutboxCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaOutboxReadManager;
import com.ryuqq.marketplace.application.qna.port.out.client.QnaOutboxPublishClient;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** QnA 아웃박스 릴레이 프로세서. PENDING → PROCESSING → SQS 발행. */
@Component
@ConditionalOnProperty(prefix = "sqs.queues", name = "qna-outbox")
public class QnaOutboxRelayProcessor {

    private static final Logger log = LoggerFactory.getLogger(QnaOutboxRelayProcessor.class);

    private final QnaOutboxCommandManager commandManager;
    private final QnaOutboxReadManager readManager;
    private final QnaOutboxPublishClient publishClient;

    public QnaOutboxRelayProcessor(
            QnaOutboxCommandManager commandManager,
            QnaOutboxReadManager readManager,
            QnaOutboxPublishClient publishClient) {
        this.commandManager = commandManager;
        this.readManager = readManager;
        this.publishClient = publishClient;
    }

    public void relay(QnaOutbox outbox) {
        Instant now = Instant.now();
        try {
            outbox.startProcessing(now);
            commandManager.persist(outbox);

            String messageBody = buildMessage(outbox);
            publishClient.publish(messageBody);

            log.info("QnA 아웃박스 릴레이 성공: outboxId={}, qnaId={}, type={}",
                    outbox.idValue(), outbox.qnaIdValue(), outbox.outboxType());
        } catch (Exception e) {
            log.error("QnA 아웃박스 릴레이 실패: outboxId={}", outbox.idValue(), e);
            try {
                QnaOutbox freshOutbox = readManager.getById(outbox.idValue());
                freshOutbox.recordFailure(true, e.getMessage(), Instant.now());
                commandManager.persist(freshOutbox);
            } catch (Exception inner) {
                log.error("QnA 아웃박스 실패 기록 중 오류: outboxId={}", outbox.idValue(), inner);
            }
        }
    }

    private String buildMessage(QnaOutbox outbox) {
        return "{\"outboxId\":" + outbox.idValue()
                + ",\"qnaId\":" + outbox.qnaIdValue()
                + ",\"salesChannelId\":" + outbox.salesChannelId()
                + ",\"externalQnaId\":\"" + outbox.externalQnaId() + "\""
                + ",\"outboxType\":\"" + outbox.outboxType().name() + "\""
                + "}";
    }
}
