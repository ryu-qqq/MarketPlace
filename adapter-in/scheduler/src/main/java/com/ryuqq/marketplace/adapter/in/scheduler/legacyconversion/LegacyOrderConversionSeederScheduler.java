package com.ryuqq.marketplace.adapter.in.scheduler.legacyconversion;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.SeedLegacyOrderConversionResult;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderSeederLockManager;
import com.ryuqq.marketplace.application.legacyconversion.port.in.command.SeedLegacyOrderConversionUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 시딩 스케줄러.
 *
 * <p>luxurydb의 모든 활성 주문에 대해 PENDING outbox 엔트리를 생성합니다. 기존 LegacyOrderConversionScheduler가 PENDING
 * 건을 처리하여 내부 주문으로 변환합니다.
 *
 * <p>기본 비활성화 (matchIfMissing=false). 명시적으로 enabled: true 설정 필요.
 *
 * <p>ECS 다중 인스턴스 환경에서 분산 락으로 동시 실행을 방지합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.legacy-order-conversion-seeder",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class LegacyOrderConversionSeederScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(LegacyOrderConversionSeederScheduler.class);

    private static final int BATCH_SIZE = 500;

    private final SeedLegacyOrderConversionUseCase seedUseCase;
    private final LegacyOrderSeederLockManager lockManager;

    private volatile long lastCursor = 0;

    public LegacyOrderConversionSeederScheduler(
            SeedLegacyOrderConversionUseCase seedUseCase,
            LegacyOrderSeederLockManager lockManager) {
        this.seedUseCase = seedUseCase;
        this.lockManager = lockManager;
    }

    @Scheduled(
            fixedDelayString = "${scheduler.jobs.legacy-order-conversion-seeder.fixed-delay:10000}")
    @SchedulerJob("LegacyOrderConversion-Seeder")
    public int seedPendingOutboxes() {
        return lockManager.executeWithLock(this::doSeed, 0);
    }

    private int doSeed() {
        log.info("레거시 주문 시딩 스케줄러 시작: cursor={}, batchSize={}", lastCursor, BATCH_SIZE);

        SeedLegacyOrderConversionResult result = seedUseCase.execute(lastCursor, BATCH_SIZE);

        if (result.completed()) {
            log.info("레거시 주문 시딩 전체 스캔 완료, 커서 초기화");
            lastCursor = 0;
        } else {
            lastCursor = result.lastCursor();
        }

        log.info(
                "레거시 주문 시딩 스케줄러 완료: scanned={}, created={}, skipped={}, nextCursor={}",
                result.scanned(),
                result.created(),
                result.skipped(),
                lastCursor);

        return result.created();
    }
}
