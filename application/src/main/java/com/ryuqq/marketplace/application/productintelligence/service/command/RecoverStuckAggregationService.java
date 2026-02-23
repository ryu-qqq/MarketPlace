package com.ryuqq.marketplace.application.productintelligence.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.productintelligence.dto.command.RecoverStuckAggregationCommand;
import com.ryuqq.marketplace.application.productintelligence.manager.IntelligencePublishManager;
import com.ryuqq.marketplace.application.productintelligence.manager.ProductProfileReadManager;
import com.ryuqq.marketplace.application.productintelligence.port.in.command.RecoverStuckAggregationUseCase;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * RecoverStuckAggregationService - stuck Aggregation 프로파일 복구 서비스.
 *
 * <p>ANALYZING 상태에서 모든 분석이 완료되었지만 Aggregation 큐 발행이 누락된 프로파일을 조회하여 Aggregation 큐를 재발행합니다.
 *
 * <p>발생 원인: Analyzer가 persist 성공 후 SQS 발행 전에 실패한 경우.
 */
@Service
@ConditionalOnProperty(name = "intelligence.pipeline.enabled", havingValue = "true")
public class RecoverStuckAggregationService implements RecoverStuckAggregationUseCase {

    private static final Logger log = LoggerFactory.getLogger(RecoverStuckAggregationService.class);

    private final ProductProfileReadManager profileReadManager;
    private final IntelligencePublishManager publishManager;

    public RecoverStuckAggregationService(
            ProductProfileReadManager profileReadManager,
            IntelligencePublishManager publishManager) {
        this.profileReadManager = profileReadManager;
        this.publishManager = publishManager;
    }

    @Override
    public SchedulerBatchProcessingResult execute(RecoverStuckAggregationCommand command) {
        List<ProductProfile> stuckProfiles =
                profileReadManager.findStuckAnalyzingProfiles(
                        command.stuckThreshold(), command.batchSize());

        int total = stuckProfiles.size();
        int successCount = 0;
        int failedCount = 0;

        for (ProductProfile profile : stuckProfiles) {
            try {
                publishManager.publishToAggregation(profile.idValue(), profile.productGroupId());
                successCount++;
                log.info(
                        "Stuck Aggregation 복구 성공: profileId={}, productGroupId={}",
                        profile.idValue(),
                        profile.productGroupId());
            } catch (Exception e) {
                log.error(
                        "Stuck Aggregation 복구 실패: profileId={}, productGroupId={}, error={}",
                        profile.idValue(),
                        profile.productGroupId(),
                        e.getMessage(),
                        e);
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
