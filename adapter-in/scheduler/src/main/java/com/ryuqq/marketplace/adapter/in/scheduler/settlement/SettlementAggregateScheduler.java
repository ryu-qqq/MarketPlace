package com.ryuqq.marketplace.adapter.in.scheduler.settlement;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.application.settlement.port.in.command.AggregateSettlementUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 정산 집계 스케줄러.
 *
 * <p>매일 03:00에 CONFIRMED Entry가 존재하는 셀러별로 주간 집계를 수행하여 Settlement를 생성합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.settlement-aggregate",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class SettlementAggregateScheduler {

    private static final Logger log = LoggerFactory.getLogger(SettlementAggregateScheduler.class);

    private final AggregateSettlementUseCase aggregateSettlementUseCase;

    public SettlementAggregateScheduler(AggregateSettlementUseCase aggregateSettlementUseCase) {
        this.aggregateSettlementUseCase = aggregateSettlementUseCase;
    }

    @Scheduled(cron = "${scheduler.jobs.settlement-aggregate.cron:0 0 3 * * *}")
    @SchedulerJob("Settlement-Aggregate")
    public void aggregate() {
        log.info("[Settlement-Aggregate] 집계 시작");
        aggregateSettlementUseCase.executeAll();
        log.info("[Settlement-Aggregate] 집계 완료");
    }
}
