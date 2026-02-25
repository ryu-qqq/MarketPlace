package com.ryuqq.marketplace.adapter.in.scheduler.inboundproduct;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.ConvertPendingInboundProductsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * InboundProduct 비동기 변환 스케줄러.
 *
 * <p>PENDING_CONVERSION 상태의 인바운드 상품을 주기적으로 조회하여 ProductGroup으로 변환합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.inbound-conversion",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class InboundConversionScheduler {

    private static final Logger log = LoggerFactory.getLogger(InboundConversionScheduler.class);

    private final ConvertPendingInboundProductsUseCase convertPendingUseCase;

    public InboundConversionScheduler(ConvertPendingInboundProductsUseCase convertPendingUseCase) {
        this.convertPendingUseCase = convertPendingUseCase;
    }

    @Scheduled(
            cron = "${scheduler.jobs.inbound-conversion.cron:0 */5 * * * *}",
            zone = "${scheduler.jobs.inbound-conversion.timezone:Asia/Seoul}")
    @SchedulerJob("InboundProduct-ConvertPending")
    public int convertPendingProducts() {
        log.info("InboundProduct PENDING_CONVERSION 변환 스케줄러 시작");
        int convertedCount = convertPendingUseCase.execute();
        log.info("InboundProduct PENDING_CONVERSION 변환 완료: convertedCount={}", convertedCount);
        return convertedCount;
    }
}
