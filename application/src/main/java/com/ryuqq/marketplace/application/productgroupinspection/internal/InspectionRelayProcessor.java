package com.ryuqq.marketplace.application.productgroupinspection.internal;

import com.ryuqq.marketplace.application.productgroupinspection.manager.InspectionScoringPublishManager;
import com.ryuqq.marketplace.application.productgroupinspection.manager.ProductGroupInspectionOutboxCommandManager;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Outbox Relay 처리기.
 *
 * <p>Outbox 상태를 SENT로 변경하고 Scoring 큐로 발행합니다.
 */
@Component
public class InspectionRelayProcessor {

    private static final Logger log = LoggerFactory.getLogger(InspectionRelayProcessor.class);

    private final ProductGroupInspectionOutboxCommandManager outboxCommandManager;
    private final InspectionScoringPublishManager scoringPublishManager;

    public InspectionRelayProcessor(
            ProductGroupInspectionOutboxCommandManager outboxCommandManager,
            InspectionScoringPublishManager scoringPublishManager) {
        this.outboxCommandManager = outboxCommandManager;
        this.scoringPublishManager = scoringPublishManager;
    }

    /**
     * 단건 Outbox를 SQS Scoring 큐로 relay 합니다.
     *
     * @param outbox 발행 대상 Outbox
     * @param messageBody 발행할 메시지 바디
     * @return relay 성공 여부
     */
    public boolean relay(ProductGroupInspectionOutbox outbox, String messageBody) {
        Instant now = Instant.now();

        try {
            outbox.markAsSent(now);
            outboxCommandManager.persist(outbox);
            scoringPublishManager.publish(messageBody);

            log.info(
                    "Outbox Relay 성공: outboxId={}, productGroupId={}",
                    outbox.idValue(),
                    outbox.productGroupId());
            return true;

        } catch (Exception e) {
            log.error(
                    "Outbox Relay 실패: outboxId={}, productGroupId={}, error={}",
                    outbox.idValue(),
                    outbox.productGroupId(),
                    e.getMessage(),
                    e);
            outbox.recordFailure(true, "Relay 실패: " + e.getMessage(), Instant.now());
            outboxCommandManager.persist(outbox);
            return false;
        }
    }
}
