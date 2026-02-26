package com.ryuqq.marketplace.adapter.in.scheduler.imageupload;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imageupload.dto.command.ProcessPendingImageUploadCommand;
import com.ryuqq.marketplace.application.imageupload.dto.command.RecoverTimeoutImageUploadCommand;
import com.ryuqq.marketplace.application.imageupload.port.in.command.ProcessPendingImageUploadUseCase;
import com.ryuqq.marketplace.application.imageupload.port.in.command.RecoverTimeoutImageUploadUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 이미지 업로드 Outbox 처리 스케줄러.
 *
 * <p>두 가지 작업을 수행합니다:
 *
 * <ul>
 *   <li>processPendingOutboxes: PENDING 상태의 Outbox 처리 (S3 업로드)
 *   <li>recoverTimeoutOutboxes: PROCESSING 타임아웃 Outbox 복구
 * </ul>
 *
 * <p>스케줄 주기 및 배치 크기는 환경별 설정 파일(scheduler-{profile}.yml)에서 관리됩니다.
 *
 * @see SchedulerProperties
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.image-upload-outbox.process-pending",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class ImageUploadOutboxScheduler {

    private final ProcessPendingImageUploadUseCase processPendingUseCase;
    private final RecoverTimeoutImageUploadUseCase recoverTimeoutUseCase;
    private final SchedulerProperties.ImageUploadOutbox config;

    public ImageUploadOutboxScheduler(
            ProcessPendingImageUploadUseCase processPendingUseCase,
            RecoverTimeoutImageUploadUseCase recoverTimeoutUseCase,
            SchedulerProperties schedulerProperties) {
        this.processPendingUseCase = processPendingUseCase;
        this.recoverTimeoutUseCase = recoverTimeoutUseCase;
        this.config = schedulerProperties.jobs().imageUploadOutbox();
    }

    /**
     * PENDING 상태의 이미지 업로드 Outbox를 처리합니다.
     *
     * <p>생성된 지 설정된 지연 시간 이상 경과한 PENDING Outbox를 처리합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.image-upload-outbox.process-pending.cron}",
            zone = "${scheduler.jobs.image-upload-outbox.process-pending.timezone}")
    @SchedulerJob("ImageUploadOutbox-ProcessPending")
    public SchedulerBatchProcessingResult processPendingOutboxes() {
        SchedulerProperties.ProcessPending processPending = config.processPending();
        ProcessPendingImageUploadCommand command =
                ProcessPendingImageUploadCommand.of(
                        processPending.batchSize(), processPending.delaySeconds());
        return processPendingUseCase.execute(command);
    }

    /**
     * PROCESSING 상태에서 타임아웃된 좀비 Outbox를 복구합니다.
     *
     * <p>PROCESSING 상태에서 설정된 타임아웃 시간 이상 경과한 Outbox를 PENDING으로 복구합니다. 실제 재처리는 다음
     * processPendingOutboxes 주기에서 수행됩니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.image-upload-outbox.recover-timeout.cron}",
            zone = "${scheduler.jobs.image-upload-outbox.recover-timeout.timezone}")
    @SchedulerJob("ImageUploadOutbox-RecoverTimeout")
    public SchedulerBatchProcessingResult recoverTimeoutOutboxes() {
        SchedulerProperties.RecoverTimeout recoverTimeout = config.recoverTimeout();
        RecoverTimeoutImageUploadCommand command =
                RecoverTimeoutImageUploadCommand.of(
                        recoverTimeout.batchSize(), recoverTimeout.timeoutSeconds());
        return recoverTimeoutUseCase.execute(command);
    }
}
