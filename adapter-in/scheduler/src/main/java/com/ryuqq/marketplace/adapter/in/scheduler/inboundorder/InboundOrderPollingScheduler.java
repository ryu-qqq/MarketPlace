package com.ryuqq.marketplace.adapter.in.scheduler.inboundorder;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.inboundorder.dto.result.InboundOrderPollingResult;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.PollExternalOrdersUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 외부 판매채널의 주문을 주기적으로 폴링하여 InboundOrder로 수신하는 스케줄러.
 *
 * <p>CONNECTED 상태의 SellerSalesChannel을 조회하여 신규 주문을 가져오고, 상품 매핑 후 내부 Order로 변환합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.inbound-order-polling",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class InboundOrderPollingScheduler {

    private final PollExternalOrdersUseCase pollExternalOrdersUseCase;
    private final SchedulerProperties.InboundOrderPolling config;

    public InboundOrderPollingScheduler(
            PollExternalOrdersUseCase pollExternalOrdersUseCase,
            SchedulerProperties schedulerProperties) {
        this.pollExternalOrdersUseCase = pollExternalOrdersUseCase;
        this.config = schedulerProperties.jobs().inboundOrderPolling();
    }

    @Scheduled(
            cron = "${scheduler.jobs.inbound-order-polling.cron}",
            zone = "${scheduler.jobs.inbound-order-polling.timezone}")
    @SchedulerJob("InboundOrder-Polling")
    public SchedulerBatchProcessingResult poll() {
        InboundOrderPollingResult result =
                pollExternalOrdersUseCase.execute(config.channelCode(), config.batchSize());
        return SchedulerBatchProcessingResult.of(result.total(), result.created(), result.failed());
    }
}
