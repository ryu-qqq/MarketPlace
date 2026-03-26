package com.ryuqq.marketplace.adapter.in.scheduler.legacyconversion;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.application.legacyconversion.port.in.command.ConvertLegacyOrdersUseCase;
import com.ryuqq.marketplace.application.legacyconversion.port.in.command.RecoverLegacyOrderConversionTimeoutUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 변환 스케줄러.
 *
 * <p>PENDING 상태의 주문 Outbox를 주기적으로 조회하여 내부 주문으로 변환하고, PROCESSING 타임아웃을 복구합니다.
 *
 * <p>기본 비활성화 (matchIfMissing=false). 명시적으로 enabled: true 설정 필요.
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.legacy-order-conversion",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class LegacyOrderConversionScheduler {

    private static final Logger log = LoggerFactory.getLogger(LegacyOrderConversionScheduler.class);

    private final ConvertLegacyOrdersUseCase convertUseCase;
    private final RecoverLegacyOrderConversionTimeoutUseCase recoverTimeoutUseCase;

    public LegacyOrderConversionScheduler(
            ConvertLegacyOrdersUseCase convertUseCase,
            RecoverLegacyOrderConversionTimeoutUseCase recoverTimeoutUseCase) {
        this.convertUseCase = convertUseCase;
        this.recoverTimeoutUseCase = recoverTimeoutUseCase;
    }

    @Scheduled(
            cron = "${scheduler.jobs.legacy-order-conversion.cron:0 */5 * * * *}",
            zone = "${scheduler.jobs.legacy-order-conversion.timezone:Asia/Seoul}")
    @SchedulerJob("LegacyOrderConversion-ConvertPending")
    public int convertPendingOutboxes() {
        log.info("레거시 주문 변환 스케줄러 시작");
        int convertedCount = convertUseCase.execute();
        log.info("레거시 주문 변환 스케줄러 완료: convertedCount={}", convertedCount);
        return convertedCount;
    }

    @Scheduled(
            cron = "${scheduler.jobs.legacy-order-conversion-timeout.cron:0 */10 * * * *}",
            zone = "${scheduler.jobs.legacy-order-conversion-timeout.timezone:Asia/Seoul}")
    @SchedulerJob("LegacyOrderConversion-RecoverTimeout")
    public int recoverTimeoutOutboxes() {
        log.info("주문 PROCESSING 타임아웃 복구 스케줄러 시작");
        int recoveredCount = recoverTimeoutUseCase.execute();
        log.info("주문 PROCESSING 타임아웃 복구 스케줄러 완료: recoveredCount={}", recoveredCount);
        return recoveredCount;
    }
}
