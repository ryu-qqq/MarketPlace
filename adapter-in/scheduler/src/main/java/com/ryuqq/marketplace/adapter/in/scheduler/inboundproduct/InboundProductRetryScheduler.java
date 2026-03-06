package com.ryuqq.marketplace.adapter.in.scheduler.inboundproduct;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.RetryPendingMappingUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * PENDING_MAPPING 상태의 인바운드 상품을 주기적으로 재처리하는 스케줄러.
 *
 * <p>매핑 테이블이 추가된 후에도 크롤러 재수신 없이 PENDING_MAPPING → MAPPED 전이를 가능하게 합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.inbound-product-retry",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class InboundProductRetryScheduler {

    private final RetryPendingMappingUseCase retryPendingMappingUseCase;
    private final SchedulerProperties.InboundProductRetry config;

    public InboundProductRetryScheduler(
            RetryPendingMappingUseCase retryPendingMappingUseCase,
            SchedulerProperties schedulerProperties) {
        this.retryPendingMappingUseCase = retryPendingMappingUseCase;
        this.config = schedulerProperties.jobs().inboundProductRetry();
    }

    @Scheduled(
            cron = "${scheduler.jobs.inbound-product-retry.cron}",
            zone = "${scheduler.jobs.inbound-product-retry.timezone}")
    @SchedulerJob("InboundProduct-RetryMapping")
    public SchedulerBatchProcessingResult retryPendingMapping() {
        return retryPendingMappingUseCase.execute(config.batchSize());
    }
}
