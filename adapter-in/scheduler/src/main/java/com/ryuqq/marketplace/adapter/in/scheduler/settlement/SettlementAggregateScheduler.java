package com.ryuqq.marketplace.adapter.in.scheduler.settlement;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.application.settlement.port.in.command.AggregateSettlementUseCase;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 정산 집계 스케줄러.
 *
 * <p>매일 03:00에 CONFIRMED Entry를 집계하여 Settlement를 생성합니다. 현재는 셀러 조회 없이 단순 집계만 수행합니다 (향후
 * SellerSettlement 연동 예정).
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
        LocalDate today = LocalDate.now();
        LocalDate periodStart = today.minusDays(7);
        LocalDate periodEnd = today.minusDays(1);

        log.info("[Settlement-Aggregate] 집계 시작: {} ~ {}", periodStart, periodEnd);

        // TODO: 향후 SellerSettlement에서 정산 대상 셀러 목록 조회 후 셀러별 집계
        // 현재는 수동 호출 또는 API를 통해 셀러별 집계를 트리거합니다.
    }
}
