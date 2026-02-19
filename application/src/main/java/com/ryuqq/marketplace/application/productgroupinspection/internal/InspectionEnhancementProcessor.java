package com.ryuqq.marketplace.application.productgroupinspection.internal;

import com.ryuqq.marketplace.application.productgroupinspection.dto.response.CanonicalOptionEnhancementResult;
import com.ryuqq.marketplace.application.productgroupinspection.dto.response.NoticeCompletionEnhancementResult;
import com.ryuqq.marketplace.application.productgroupinspection.manager.CanonicalOptionEnhancementManager;
import com.ryuqq.marketplace.application.productgroupinspection.manager.InspectionVerificationPublishManager;
import com.ryuqq.marketplace.application.productgroupinspection.manager.NoticeCompletionEnhancementManager;
import com.ryuqq.marketplace.application.productgroupinspection.manager.ProductGroupInspectionOutboxCommandManager;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Enhancement 단계 처리기.
 *
 * <p>LLM을 활용하여 캐노니컬 옵션 매핑 및 고시정보를 보강하고 Verification 큐로 라우팅합니다.
 */
@Component
public class InspectionEnhancementProcessor {

    private static final Logger log = LoggerFactory.getLogger(InspectionEnhancementProcessor.class);

    private final ProductGroupInspectionOutboxCommandManager outboxCommandManager;
    private final CanonicalOptionEnhancementManager optionEnhancementManager;
    private final NoticeCompletionEnhancementManager noticeEnhancementManager;
    private final InspectionVerificationPublishManager verificationPublishManager;

    public InspectionEnhancementProcessor(
            ProductGroupInspectionOutboxCommandManager outboxCommandManager,
            CanonicalOptionEnhancementManager optionEnhancementManager,
            NoticeCompletionEnhancementManager noticeEnhancementManager,
            InspectionVerificationPublishManager verificationPublishManager) {
        this.outboxCommandManager = outboxCommandManager;
        this.optionEnhancementManager = optionEnhancementManager;
        this.noticeEnhancementManager = noticeEnhancementManager;
        this.verificationPublishManager = verificationPublishManager;
    }

    @Transactional
    public void process(ProductGroupInspectionOutbox outbox, String messageBody) {
        Instant now = Instant.now();
        Long productGroupId = outbox.productGroupId();

        try {
            CanonicalOptionEnhancementResult optionResult =
                    optionEnhancementManager.enhance(productGroupId);
            log.info(
                    "옵션 매핑 보강 완료: productGroupId={}, enhancedCount={}",
                    productGroupId,
                    optionResult.enhancedCount());

            NoticeCompletionEnhancementResult noticeResult =
                    noticeEnhancementManager.enhance(productGroupId);
            log.info(
                    "고시정보 보완 완료: productGroupId={}, enhancedCount={}",
                    productGroupId,
                    noticeResult.enhancedCount());

            outbox.startVerifying(now);
            outboxCommandManager.persist(outbox);
            verificationPublishManager.publish(messageBody);

            log.info(
                    "Enhancement 완료 → Verification: outboxId={}, productGroupId={}",
                    outbox.idValue(),
                    productGroupId);

        } catch (Exception e) {
            log.error(
                    "Enhancement 실패: outboxId={}, productGroupId={}, error={}",
                    outbox.idValue(),
                    productGroupId,
                    e.getMessage(),
                    e);
            outbox.recordFailure(true, "Enhancement 실패: " + e.getMessage(), Instant.now());
            outboxCommandManager.persist(outbox);
        }
    }
}
