package com.ryuqq.marketplace.adapter.in.scheduler.imagetransform;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imagetransform.dto.command.PollProcessingImageTransformCommand;
import com.ryuqq.marketplace.application.imagetransform.dto.command.ProcessPendingImageTransformCommand;
import com.ryuqq.marketplace.application.imagetransform.dto.command.RecoverTimeoutImageTransformCommand;
import com.ryuqq.marketplace.application.imagetransform.port.in.command.PollProcessingImageTransformUseCase;
import com.ryuqq.marketplace.application.imagetransform.port.in.command.ProcessPendingImageTransformUseCase;
import com.ryuqq.marketplace.application.imagetransform.port.in.command.RecoverTimeoutImageTransformUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 이미지 변환 Outbox 처리 스케줄러.
 *
 * <p>세 가지 작업을 수행합니다:
 *
 * <ul>
 *   <li>processPending: PENDING 상태의 Outbox 처리 (이미지 변환 요청)
 *   <li>pollProcessing: PROCESSING 상태의 Outbox 폴링 (변환 완료 확인)
 *   <li>recoverTimeout: PROCESSING 타임아웃 Outbox 복구
 * </ul>
 *
 * <p>스케줄 주기 및 배치 크기는 환경별 설정 파일(scheduler-{profile}.yml)에서 관리됩니다.
 *
 * @see SchedulerProperties
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.image-transform-outbox.process-pending",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class ImageTransformOutboxScheduler {

    private final ProcessPendingImageTransformUseCase processPendingUseCase;
    private final PollProcessingImageTransformUseCase pollProcessingUseCase;
    private final RecoverTimeoutImageTransformUseCase recoverTimeoutUseCase;
    private final SchedulerProperties.ImageTransformOutbox config;

    public ImageTransformOutboxScheduler(
            ProcessPendingImageTransformUseCase processPendingUseCase,
            PollProcessingImageTransformUseCase pollProcessingUseCase,
            RecoverTimeoutImageTransformUseCase recoverTimeoutUseCase,
            SchedulerProperties schedulerProperties) {
        this.processPendingUseCase = processPendingUseCase;
        this.pollProcessingUseCase = pollProcessingUseCase;
        this.recoverTimeoutUseCase = recoverTimeoutUseCase;
        this.config = schedulerProperties.jobs().imageTransformOutbox();
    }

    /**
     * PENDING 상태의 이미지 변환 Outbox를 처리합니다.
     *
     * <p>생성된 지 설정된 지연 시간 이상 경과한 PENDING Outbox를 처리합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.image-transform-outbox.process-pending.cron}",
            zone = "${scheduler.jobs.image-transform-outbox.process-pending.timezone}")
    @SchedulerJob("ImageTransformOutbox-ProcessPending")
    public SchedulerBatchProcessingResult processPending() {
        SchedulerProperties.ProcessPending processPending = config.processPending();
        ProcessPendingImageTransformCommand command =
                ProcessPendingImageTransformCommand.of(
                        processPending.batchSize(), processPending.delaySeconds());
        return processPendingUseCase.execute(command);
    }

    /**
     * PROCESSING 상태의 이미지 변환 Outbox를 폴링합니다.
     *
     * <p>PROCESSING 상태의 Outbox에 대해 외부 서비스의 변환 완료 여부를 확인합니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.image-transform-outbox.poll-processing.cron}",
            zone = "${scheduler.jobs.image-transform-outbox.poll-processing.timezone}")
    @SchedulerJob("ImageTransformOutbox-PollProcessing")
    public SchedulerBatchProcessingResult pollProcessing() {
        SchedulerProperties.PollProcessing pollProcessing = config.pollProcessing();
        PollProcessingImageTransformCommand command =
                PollProcessingImageTransformCommand.of(pollProcessing.batchSize());
        return pollProcessingUseCase.execute(command);
    }

    /**
     * PROCESSING 상태에서 타임아웃된 좀비 Outbox를 복구합니다.
     *
     * <p>PROCESSING 상태에서 설정된 타임아웃 시간 이상 경과한 Outbox를 PENDING으로 복구합니다. 실제 재처리는 다음 processPending 주기에서
     * 수행됩니다.
     */
    @Scheduled(
            cron = "${scheduler.jobs.image-transform-outbox.recover-timeout.cron}",
            zone = "${scheduler.jobs.image-transform-outbox.recover-timeout.timezone}")
    @SchedulerJob("ImageTransformOutbox-RecoverTimeout")
    public SchedulerBatchProcessingResult recoverTimeout() {
        SchedulerProperties.RecoverTimeout recoverTimeout = config.recoverTimeout();
        RecoverTimeoutImageTransformCommand command =
                RecoverTimeoutImageTransformCommand.of(
                        recoverTimeout.batchSize(), recoverTimeout.timeoutSeconds());
        return recoverTimeoutUseCase.execute(command);
    }
}
