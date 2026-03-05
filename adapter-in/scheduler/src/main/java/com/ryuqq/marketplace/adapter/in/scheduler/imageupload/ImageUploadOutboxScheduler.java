package com.ryuqq.marketplace.adapter.in.scheduler.imageupload;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imageupload.dto.command.PollProcessingImageUploadCommand;
import com.ryuqq.marketplace.application.imageupload.dto.command.ProcessPendingImageUploadCommand;
import com.ryuqq.marketplace.application.imageupload.dto.command.RecoverFailedImageUploadCommand;
import com.ryuqq.marketplace.application.imageupload.dto.command.RecoverTimeoutImageUploadCommand;
import com.ryuqq.marketplace.application.imageupload.port.in.command.PollProcessingImageUploadUseCase;
import com.ryuqq.marketplace.application.imageupload.port.in.command.ProcessPendingImageUploadUseCase;
import com.ryuqq.marketplace.application.imageupload.port.in.command.RecoverFailedImageUploadUseCase;
import com.ryuqq.marketplace.application.imageupload.port.in.command.RecoverTimeoutImageUploadUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 이미지 업로드 Outbox 처리 스케줄러.
 *
 * <p>네 가지 작업을 수행합니다:
 *
 * <ul>
 *   <li>processPendingOutboxes: PENDING Outbox → 다운로드 태스크 생성 (논블로킹)
 *   <li>pollProcessingOutboxes: PROCESSING Outbox → 다운로드 완료 확인 (논블로킹)
 *   <li>recoverTimeoutOutboxes: PROCESSING 타임아웃 Outbox 복구
 *   <li>recoverFailedOutboxes: FAILED Outbox 복구 (복구 가능한 것만)
 * </ul>
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
    private final PollProcessingImageUploadUseCase pollProcessingUseCase;
    private final RecoverTimeoutImageUploadUseCase recoverTimeoutUseCase;
    private final RecoverFailedImageUploadUseCase recoverFailedUseCase;
    private final SchedulerProperties.ImageUploadOutbox config;

    public ImageUploadOutboxScheduler(
            ProcessPendingImageUploadUseCase processPendingUseCase,
            PollProcessingImageUploadUseCase pollProcessingUseCase,
            RecoverTimeoutImageUploadUseCase recoverTimeoutUseCase,
            RecoverFailedImageUploadUseCase recoverFailedUseCase,
            SchedulerProperties schedulerProperties) {
        this.processPendingUseCase = processPendingUseCase;
        this.pollProcessingUseCase = pollProcessingUseCase;
        this.recoverTimeoutUseCase = recoverTimeoutUseCase;
        this.recoverFailedUseCase = recoverFailedUseCase;
        this.config = schedulerProperties.jobs().imageUploadOutbox();
    }

    /**
     * PENDING 상태의 이미지 업로드 Outbox를 처리합니다.
     *
     * <p>FileFlow 다운로드 태스크를 생성하고 PROCESSING 상태로 변경합니다 (논블로킹).
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
     * PROCESSING 상태의 이미지 업로드 Outbox를 폴링합니다 (콜백 fallback용).
     *
     * <p>콜백 방식으로 전환되어 기본 비활성화. enabled=true 시에만 동작합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.image-upload-outbox.poll-processing.cron}",
            zone = "${scheduler.jobs.image-upload-outbox.poll-processing.timezone}")
    @SchedulerJob("ImageUploadOutbox-PollProcessing")
    public SchedulerBatchProcessingResult pollProcessingOutboxes() {
        SchedulerProperties.PollProcessing pollProcessing = config.pollProcessing();
        if (!pollProcessing.enabled()) {
            return SchedulerBatchProcessingResult.of(0, 0, 0);
        }
        PollProcessingImageUploadCommand command =
                PollProcessingImageUploadCommand.of(pollProcessing.batchSize());
        return pollProcessingUseCase.execute(command);
    }

    /**
     * PROCESSING 상태에서 타임아웃된 좀비 Outbox를 복구합니다.
     *
     * <p>PROCESSING 상태에서 설정된 타임아웃 시간 이상 경과한 Outbox를 PENDING으로 복구합니다.
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

    /**
     * FAILED 상태의 복구 가능한 Outbox를 PENDING으로 초기화합니다.
     *
     * <p>잘못된 요청(BadRequest) 에러를 제외한 FAILED Outbox만 복구합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.image-upload-outbox.recover-failed.cron}",
            zone = "${scheduler.jobs.image-upload-outbox.recover-failed.timezone}")
    @SchedulerJob("ImageUploadOutbox-RecoverFailed")
    public SchedulerBatchProcessingResult recoverFailedOutboxes() {
        SchedulerProperties.RecoverFailed recoverFailed = config.recoverFailed();
        RecoverFailedImageUploadCommand command =
                RecoverFailedImageUploadCommand.of(
                        recoverFailed.batchSize(), recoverFailed.failedAfterSeconds());
        return recoverFailedUseCase.execute(command);
    }
}
