package com.ryuqq.marketplace.application.productgroupinspection.internal;

import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.productgroupinspection.dto.response.InspectionVerificationResult;
import com.ryuqq.marketplace.application.productgroupinspection.manager.InspectionVerificationManager;
import com.ryuqq.marketplace.application.productgroupinspection.manager.ProductGroupInspectionOutboxCommandManager;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionResult;
import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Verification 단계 처리기.
 *
 * <p>LLM 최종 품질 검증 후 ProductGroup 상태를 전환(activate/reject)하고 Outbox를 COMPLETED 처리합니다.
 */
@Component
public class InspectionVerificationProcessor {

    private static final Logger log =
            LoggerFactory.getLogger(InspectionVerificationProcessor.class);

    private final ProductGroupInspectionOutboxCommandManager outboxCommandManager;
    private final InspectionVerificationManager verificationManager;
    private final ProductGroupReadManager productGroupReadManager;
    private final ProductGroupCommandManager productGroupCommandManager;

    public InspectionVerificationProcessor(
            ProductGroupInspectionOutboxCommandManager outboxCommandManager,
            InspectionVerificationManager verificationManager,
            ProductGroupReadManager productGroupReadManager,
            ProductGroupCommandManager productGroupCommandManager) {
        this.outboxCommandManager = outboxCommandManager;
        this.verificationManager = verificationManager;
        this.productGroupReadManager = productGroupReadManager;
        this.productGroupCommandManager = productGroupCommandManager;
    }

    @Transactional
    public void process(ProductGroupInspectionOutbox outbox) {
        Instant now = Instant.now();
        Long productGroupId = outbox.productGroupId();

        try {
            InspectionVerificationResult verificationResult =
                    verificationManager.verify(productGroupId);

            InspectionResult result =
                    InspectionResult.of(
                            Map.of(),
                            verificationResult.overallScore(),
                            verificationResult.passed(),
                            verificationResult.reasons());

            ProductGroup productGroup =
                    productGroupReadManager.getById(ProductGroupId.of(productGroupId));

            if (verificationResult.passed()) {
                productGroup.activate(now);
            } else {
                productGroup.reject(now);
            }
            productGroupCommandManager.persist(productGroup);

            outbox.complete(result, result.toString(), now);
            outboxCommandManager.persist(outbox);

            log.info(
                    "Verification 완료: outboxId={}, productGroupId={}, passed={}, score={}",
                    outbox.idValue(),
                    productGroupId,
                    verificationResult.passed(),
                    verificationResult.overallScore());

        } catch (Exception e) {
            log.error(
                    "Verification 실패: outboxId={}, productGroupId={}, error={}",
                    outbox.idValue(),
                    productGroupId,
                    e.getMessage(),
                    e);
            outbox.recordFailure(true, "Verification 실패: " + e.getMessage(), Instant.now());
            outboxCommandManager.persist(outbox);
        }
    }
}
