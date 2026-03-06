package com.ryuqq.marketplace.adapter.in.scheduler.inboundorder;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.RetryInboundOrderMappingUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * PENDING_MAPPING 상태의 InboundOrder를 주기적으로 재처리하는 스케줄러.
 *
 * <p>상품 매핑이 완료되지 않은 주문들을 재시도하여 MAPPED → CONVERTED 전이를 수행합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.inbound-order-retry",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class InboundOrderRetryScheduler {

    private final RetryInboundOrderMappingUseCase retryInboundOrderMappingUseCase;
    private final SchedulerProperties.InboundOrderRetry config;

    public InboundOrderRetryScheduler(
            RetryInboundOrderMappingUseCase retryInboundOrderMappingUseCase,
            SchedulerProperties schedulerProperties) {
        this.retryInboundOrderMappingUseCase = retryInboundOrderMappingUseCase;
        this.config = schedulerProperties.jobs().inboundOrderRetry();
    }

    @Scheduled(
            cron = "${scheduler.jobs.inbound-order-retry.cron}",
            zone = "${scheduler.jobs.inbound-order-retry.timezone}")
    @SchedulerJob("InboundOrder-RetryMapping")
    public SchedulerBatchProcessingResult retryMapping() {
        return retryInboundOrderMappingUseCase.execute(config.batchSize());
    }
}
