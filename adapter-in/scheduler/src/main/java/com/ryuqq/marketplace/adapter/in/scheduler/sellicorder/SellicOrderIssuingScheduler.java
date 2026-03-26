package com.ryuqq.marketplace.adapter.in.scheduler.sellicorder;

import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.legacy.sellicorder.port.in.IssueSellicOrderUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 셀릭 주문 발행 스케줄러.
 *
 * <p>셀릭 API에서 주문을 폴링하여 luxurydb에 레거시 형식으로 저장하고, LegacyOrderConversionOutbox를 생성합니다. setof-commerce의
 * 셀릭 배치를 대체합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.sellic-order-issuing",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class SellicOrderIssuingScheduler {

    private static final Logger log = LoggerFactory.getLogger(SellicOrderIssuingScheduler.class);

    private final IssueSellicOrderUseCase issueSellicOrderUseCase;
    private final SchedulerProperties.SellicOrderIssuing config;

    public SellicOrderIssuingScheduler(
            IssueSellicOrderUseCase issueSellicOrderUseCase,
            SchedulerProperties schedulerProperties) {
        this.issueSellicOrderUseCase = issueSellicOrderUseCase;
        this.config = schedulerProperties.jobs().sellicOrderIssuing();
    }

    @Scheduled(
            cron = "${scheduler.jobs.sellic-order-issuing.cron:0 */10 * * * *}",
            zone = "${scheduler.jobs.sellic-order-issuing.timezone:Asia/Seoul}")
    public void issueOrders() {
        try {
            log.info(
                    "셀릭 주문 발행 시작: salesChannelId={}, batchSize={}",
                    config.salesChannelId(),
                    config.batchSize());

            issueSellicOrderUseCase.execute(config.salesChannelId(), config.batchSize());

        } catch (Exception e) {
            log.error("셀릭 주문 발행 스케줄러 실패", e);
        }
    }
}
