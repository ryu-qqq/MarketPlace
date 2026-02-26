package com.ryuqq.marketplace.adapter.in.scheduler.legacyconversion;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.application.legacyconversion.port.in.command.ConvertLegacyProductsUseCase;
import com.ryuqq.marketplace.application.legacyconversion.port.in.command.RecoverLegacyConversionTimeoutUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 레거시 변환 스케줄러.
 *
 * <p>PENDING 상태의 Outbox를 주기적으로 조회하여 내부 상품으로 변환하고, PROCESSING 타임아웃을 복구합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.legacy-conversion",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class LegacyConversionScheduler {

    private static final Logger log = LoggerFactory.getLogger(LegacyConversionScheduler.class);

    private final ConvertLegacyProductsUseCase convertUseCase;
    private final RecoverLegacyConversionTimeoutUseCase recoverTimeoutUseCase;

    public LegacyConversionScheduler(
            ConvertLegacyProductsUseCase convertUseCase,
            RecoverLegacyConversionTimeoutUseCase recoverTimeoutUseCase) {
        this.convertUseCase = convertUseCase;
        this.recoverTimeoutUseCase = recoverTimeoutUseCase;
    }

    @Scheduled(
            cron = "${scheduler.jobs.legacy-conversion.cron:0 */5 * * * *}",
            zone = "${scheduler.jobs.legacy-conversion.timezone:Asia/Seoul}")
    @SchedulerJob("LegacyConversion-ConvertPending")
    public int convertPendingOutboxes() {
        log.info("레거시 변환 스케줄러 시작");
        int convertedCount = convertUseCase.execute();
        log.info("레거시 변환 스케줄러 완료: convertedCount={}", convertedCount);
        return convertedCount;
    }

    @Scheduled(
            cron = "${scheduler.jobs.legacy-conversion-timeout.cron:0 */10 * * * *}",
            zone = "${scheduler.jobs.legacy-conversion-timeout.timezone:Asia/Seoul}")
    @SchedulerJob("LegacyConversion-RecoverTimeout")
    public int recoverTimeoutOutboxes() {
        log.info("PROCESSING 타임아웃 복구 스케줄러 시작");
        int recoveredCount = recoverTimeoutUseCase.execute();
        log.info("PROCESSING 타임아웃 복구 스케줄러 완료: recoveredCount={}", recoveredCount);
        return recoveredCount;
    }
}
