package com.ryuqq.marketplace.adapter.in.scheduler.inboundproduct;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.RetryPendingMappingUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * InboundProduct PENDING_MAPPING 재처리 스케줄러.
 *
 * <p>매핑 실패로 PENDING_MAPPING 상태인 InboundProduct를 주기적으로 재처리합니다. 매핑 테이블에 새로운 매핑이 등록된 후 자동으로 변환될 수 있도록
 * 합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.inbound-product-retry",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class InboundProductRetryScheduler {

    private static final Logger log = LoggerFactory.getLogger(InboundProductRetryScheduler.class);

    private final RetryPendingMappingUseCase retryPendingMappingUseCase;

    public InboundProductRetryScheduler(RetryPendingMappingUseCase retryPendingMappingUseCase) {
        this.retryPendingMappingUseCase = retryPendingMappingUseCase;
    }

    @Scheduled(
            cron = "${scheduler.jobs.inbound-product-retry.cron:0 */10 * * * *}",
            zone = "${scheduler.jobs.inbound-product-retry.timezone:Asia/Seoul}")
    @SchedulerJob("InboundProduct-RetryPendingMapping")
    public int retryPendingMappings() {
        log.info("InboundProduct PENDING_MAPPING 재처리 스케줄러 시작");
        int processedCount = retryPendingMappingUseCase.execute();
        log.info("InboundProduct PENDING_MAPPING 재처리 완료: processedCount={}", processedCount);
        return processedCount;
    }
}
