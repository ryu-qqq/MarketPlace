package com.ryuqq.marketplace.adapter.in.scheduler.legacyconversion;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.legacyconversion.dto.command.SeedLegacyConversionCommand;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.SeedLegacyConversionResult;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionSeederLockManager;
import com.ryuqq.marketplace.application.legacyconversion.port.in.command.SeedLegacyConversionUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 레거시 벌크 변환 시딩 스케줄러.
 *
 * <p>luxurydb의 모든 활성 상품에 대해 PENDING outbox 엔트리를 생성합니다. 기존 LegacyConversionScheduler가 PENDING 건을
 * 처리하여 내부 상품으로 변환합니다.
 *
 * <p>기본 비활성화 (matchIfMissing=false). 명시적으로 enabled: true 설정 필요.
 *
 * <p>ECS 다중 인스턴스 환경에서 분산 락으로 동시 실행을 방지합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.legacy-conversion-seeder",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class LegacyConversionSeederScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(LegacyConversionSeederScheduler.class);

    private final SeedLegacyConversionUseCase seedUseCase;
    private final LegacyConversionSeederLockManager lockManager;
    private final SchedulerProperties.LegacyConversionSeeder config;

    private volatile long lastCursor = 0;

    public LegacyConversionSeederScheduler(
            SeedLegacyConversionUseCase seedUseCase,
            LegacyConversionSeederLockManager lockManager,
            SchedulerProperties properties) {
        this.seedUseCase = seedUseCase;
        this.lockManager = lockManager;
        this.config = properties.jobs().legacyConversionSeeder();
    }

    @Scheduled(
            cron = "${scheduler.jobs.legacy-conversion-seeder.cron:0 */30 * * * *}",
            zone = "${scheduler.jobs.legacy-conversion-seeder.timezone:Asia/Seoul}")
    @SchedulerJob("LegacyConversion-Seeder")
    public int seedPendingOutboxes() {
        return lockManager.executeWithLock(this::doSeed, 0);
    }

    private int doSeed() {
        log.info(
                "레거시 시딩 스케줄러 시작: cursor={}, batchSize={}, maxTotal={}",
                lastCursor,
                config.batchSize(),
                config.maxTotal());

        SeedLegacyConversionCommand command =
                SeedLegacyConversionCommand.of(config.batchSize(), config.maxTotal(), lastCursor);

        SeedLegacyConversionResult result = seedUseCase.execute(command);

        if (result.completed()) {
            log.info("레거시 시딩 전체 스캔 완료, 커서 초기화");
            lastCursor = 0;
        } else {
            lastCursor = result.lastCursor();
        }

        log.info(
                "레거시 시딩 스케줄러 완료: scanned={}, created={}, skipped={}, nextCursor={}",
                result.scanned(),
                result.created(),
                result.skipped(),
                lastCursor);

        return result.created();
    }
}
