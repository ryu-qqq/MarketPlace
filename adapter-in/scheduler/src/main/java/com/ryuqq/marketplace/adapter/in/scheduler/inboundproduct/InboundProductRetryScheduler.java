package com.ryuqq.marketplace.adapter.in.scheduler.inboundproduct;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.RetryConvertFailedUseCase;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.RetryPendingMappingUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * InboundProduct 재처리 스케줄러.
 *
 * <p>PENDING_MAPPING 상태 재매핑 및 CONVERT_FAILED 상태 변환 재시도를 주기적으로 수행합니다.
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
    private final RetryConvertFailedUseCase retryConvertFailedUseCase;

    public InboundProductRetryScheduler(
            RetryPendingMappingUseCase retryPendingMappingUseCase,
            RetryConvertFailedUseCase retryConvertFailedUseCase) {
        this.retryPendingMappingUseCase = retryPendingMappingUseCase;
        this.retryConvertFailedUseCase = retryConvertFailedUseCase;
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

    @Scheduled(
            cron = "${scheduler.jobs.inbound-product-convert-retry.cron:0 */10 * * * *}",
            zone = "${scheduler.jobs.inbound-product-convert-retry.timezone:Asia/Seoul}")
    @SchedulerJob("InboundProduct-RetryConvertFailed")
    public int retryConvertFailed() {
        log.info("InboundProduct CONVERT_FAILED 재처리 스케줄러 시작");
        int processedCount = retryConvertFailedUseCase.execute();
        log.info("InboundProduct CONVERT_FAILED 재처리 완료: processedCount={}", processedCount);
        return processedCount;
    }
}
