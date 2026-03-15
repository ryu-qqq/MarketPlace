package com.ryuqq.marketplace.adapter.in.scheduler.imagevariantsync;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imagevariantsync.port.in.command.ProcessPendingImageVariantSyncUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 이미지 Variant Sync Outbox 처리 스케줄러.
 *
 * <p>PENDING 상태의 Outbox를 조회하여 세토프 Sync API로 Variant 정보를 동기화합니다.
 *
 * <p>스케줄 주기 및 배치 크기는 환경별 설정 파일(scheduler-{profile}.yml)에서 관리됩니다.
 *
 * @see SchedulerProperties
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.image-variant-sync-outbox.process-pending",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class ImageVariantSyncOutboxScheduler {

    private final ProcessPendingImageVariantSyncUseCase processPendingUseCase;
    private final SchedulerProperties.ImageVariantSyncOutbox config;

    public ImageVariantSyncOutboxScheduler(
            ProcessPendingImageVariantSyncUseCase processPendingUseCase,
            SchedulerProperties schedulerProperties) {
        this.processPendingUseCase = processPendingUseCase;
        this.config = schedulerProperties.jobs().imageVariantSyncOutbox();
    }

    /**
     * PENDING 상태의 이미지 Variant Sync Outbox를 처리합니다.
     *
     * <p>PENDING Outbox를 조회하여 세토프 Sync API로 Variant 정보를 동기화합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.image-variant-sync-outbox.process-pending.cron}",
            zone = "${scheduler.jobs.image-variant-sync-outbox.process-pending.timezone}")
    @SchedulerJob("ImageVariantSyncOutbox-ProcessPending")
    public SchedulerBatchProcessingResult processPending() {
        SchedulerProperties.ImageVariantSyncProcessPending processPending = config.processPending();
        return processPendingUseCase.execute(processPending.batchSize());
    }
}
