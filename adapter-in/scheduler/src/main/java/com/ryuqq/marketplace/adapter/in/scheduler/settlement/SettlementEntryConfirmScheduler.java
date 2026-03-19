package com.ryuqq.marketplace.adapter.in.scheduler.settlement;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.application.settlement.entry.port.in.command.ConfirmPendingEntriesUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 정산 원장 확정 스케줄러.
 *
 * <p>매일 02:00에 eligibleAt이 도래한 PENDING Entry를 CONFIRMED로 전환합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.settlement-entry-confirm",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class SettlementEntryConfirmScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(SettlementEntryConfirmScheduler.class);
    private static final int DEFAULT_BATCH_SIZE = 500;

    private final ConfirmPendingEntriesUseCase confirmPendingEntriesUseCase;

    public SettlementEntryConfirmScheduler(
            ConfirmPendingEntriesUseCase confirmPendingEntriesUseCase) {
        this.confirmPendingEntriesUseCase = confirmPendingEntriesUseCase;
    }

    @Scheduled(cron = "${scheduler.jobs.settlement-entry-confirm.cron:0 0 2 * * *}")
    @SchedulerJob("SettlementEntry-Confirm")
    public int confirmPendingEntries() {
        int confirmed = confirmPendingEntriesUseCase.execute(DEFAULT_BATCH_SIZE);
        log.info("[SettlementEntry-Confirm] 확정 처리 완료: {}건", confirmed);
        return confirmed;
    }
}
