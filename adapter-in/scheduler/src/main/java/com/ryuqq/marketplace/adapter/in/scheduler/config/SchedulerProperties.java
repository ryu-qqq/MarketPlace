package com.ryuqq.marketplace.adapter.in.scheduler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 스케줄러 설정 프로퍼티.
 *
 * <p>환경별 설정 파일(scheduler-{profile}.yml)에서 값을 주입받습니다.
 *
 * @see com.ryuqq.marketplace.adapter.in.scheduler.seller.SellerAuthOutboxScheduler
 * @see com.ryuqq.marketplace.adapter.in.scheduler.selleradmin.SellerAdminAuthOutboxScheduler
 * @see com.ryuqq.marketplace.adapter.in.scheduler.selleradmin.SellerAdminEmailOutboxScheduler
 */
@ConfigurationProperties(prefix = "scheduler")
public record SchedulerProperties(Jobs jobs) {

    public record Jobs(
            SellerAuthOutbox sellerAuthOutbox,
            SellerAdminAuthOutbox sellerAdminAuthOutbox,
            SellerAdminEmailOutbox sellerAdminEmailOutbox,
            ImageUploadOutbox imageUploadOutbox,
            ImageTransformOutbox imageTransformOutbox,
            DescriptionPublish descriptionPublish,
            ProductGroupInspection productGroupInspection) {}

    public record SellerAuthOutbox(ProcessPending processPending, RecoverTimeout recoverTimeout) {}

    public record SellerAdminAuthOutbox(
            ProcessPending processPending, RecoverTimeout recoverTimeout) {}

    public record SellerAdminEmailOutbox(
            ProcessPending processPending, RecoverTimeout recoverTimeout) {}

    public record ImageUploadOutbox(ProcessPending processPending, RecoverTimeout recoverTimeout) {}

    public record ImageTransformOutbox(
            ProcessPending processPending,
            PollProcessing pollProcessing,
            RecoverTimeout recoverTimeout) {}

    public record ProductGroupInspection(
            ProcessPending processPending, RecoverTimeout recoverTimeout) {}

    public record PollProcessing(boolean enabled, String cron, String timezone, int batchSize) {}

    public record DescriptionPublish(
            boolean enabled, String cron, String timezone, int batchSize) {}

    public record ProcessPending(
            boolean enabled, String cron, String timezone, int batchSize, int delaySeconds) {}

    public record RecoverTimeout(
            boolean enabled, String cron, String timezone, int batchSize, long timeoutSeconds) {}
}
