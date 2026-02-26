package com.ryuqq.marketplace.application.productintelligence.internal;

import com.ryuqq.marketplace.application.productintelligence.manager.IntelligenceOutboxCommandManager;
import com.ryuqq.marketplace.application.productintelligence.manager.IntelligencePublishManager;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Intelligence Outbox Relay 처리기.
 *
 * <p>Outbox Relay 시 다음 순서로 처리합니다:
 *
 * <ol>
 *   <li>Orchestration: ProductProfile 생성 → profileId 획득
 *   <li>SENT 전환 + profileId 할당 + persist
 *   <li>3개 Analyzer 큐로 SQS 발행
 *   <li>COMPLETED 전환 + persist
 * </ol>
 *
 * <p>실패 시 retry 카운트를 증가시키고 PENDING으로 복귀합니다 (maxRetry 초과 시 FAILED).
 */
@Component
@ConditionalOnProperty(name = "intelligence.pipeline.enabled", havingValue = "true")
public class IntelligenceRelayProcessor {

    private static final Logger log = LoggerFactory.getLogger(IntelligenceRelayProcessor.class);

    private final ProductProfileOrchestrationCoordinator profileOrchestrationManager;
    private final IntelligenceOutboxCommandManager outboxCommandManager;
    private final IntelligencePublishManager publishManager;

    public IntelligenceRelayProcessor(
            ProductProfileOrchestrationCoordinator profileOrchestrationManager,
            IntelligenceOutboxCommandManager outboxCommandManager,
            IntelligencePublishManager publishManager) {
        this.profileOrchestrationManager = profileOrchestrationManager;
        this.outboxCommandManager = outboxCommandManager;
        this.publishManager = publishManager;
    }

    /**
     * 단건 Outbox를 Orchestration + 3개 Analyzer 큐로 relay 합니다.
     *
     * @param outbox 발행 대상 Outbox
     * @return relay 성공 여부
     */
    public boolean relay(IntelligenceOutbox outbox) {
        Instant now = Instant.now();

        try {
            // 1. Orchestration: ProductProfile 생성 + profileId 획득
            Long profileId =
                    profileOrchestrationManager.createAndStartAnalyzing(outbox.productGroupId());
            outbox.assignProfile(profileId);

            // 2. SENT 전환 + persist (profileId 포함)
            outbox.markAsSent(now);
            outboxCommandManager.persist(outbox);

            // 3. SQS 3큐 발행
            publishManager.publishToAllAnalyzers(profileId, outbox.productGroupId());

            // 4. COMPLETED 전환 + persist
            outbox.complete(Instant.now());
            outboxCommandManager.persist(outbox);

            log.info(
                    "Intelligence Outbox Relay 성공: outboxId={}, productGroupId={}, profileId={}",
                    outbox.idValue(),
                    outbox.productGroupId(),
                    profileId);
            return true;

        } catch (Exception e) {
            log.error(
                    "Intelligence Outbox Relay 실패: outboxId={}, productGroupId={}, error={}",
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
