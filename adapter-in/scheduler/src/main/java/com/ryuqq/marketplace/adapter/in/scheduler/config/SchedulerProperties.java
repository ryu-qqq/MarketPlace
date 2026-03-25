package com.ryuqq.marketplace.adapter.in.scheduler.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 스케줄러 설정 프로퍼티.
 *
 * <p>환경별 설정 파일(scheduler-{profile}.yml)에서 값을 주입받습니다.
 *
 * @see com.ryuqq.marketplace.adapter.in.scheduler.seller.SellerAuthOutboxScheduler
 * @see com.ryuqq.marketplace.adapter.in.scheduler.selleradmin.SellerAdminAuthOutboxScheduler
 * @see com.ryuqq.marketplace.adapter.in.scheduler.selleradmin.SellerAdminEmailOutboxScheduler
 * @see com.ryuqq.marketplace.adapter.in.scheduler.outboundseller.OutboundSellerOutboxScheduler
 */
@ConfigurationProperties(prefix = "scheduler")
public record SchedulerProperties(Jobs jobs) {

    public record Jobs(
            SellerAuthOutbox sellerAuthOutbox,
            SellerAdminAuthOutbox sellerAdminAuthOutbox,
            SellerAdminEmailOutbox sellerAdminEmailOutbox,
            ImageUploadOutbox imageUploadOutbox,
            ImageTransformOutbox imageTransformOutbox,
            ImageVariantSyncOutbox imageVariantSyncOutbox,
            DescriptionPublish descriptionPublish,
            IntelligencePipeline intelligencePipeline,
            InboundProductRetry inboundProductRetry,
            OutboundSyncOutbox outboundSyncOutbox,
            OutboundSellerOutbox outboundSellerOutbox,
            LegacyConversionSeeder legacyConversionSeeder,
            InboundOrderPolling inboundOrderPolling,
            InboundOrderRetry inboundOrderRetry,
            SellicOrderIssuing sellicOrderIssuing,
            ShipmentOutbox shipmentOutbox,
            CancelOutbox cancelOutbox,
            RefundOutbox refundOutbox,
            ExchangeOutbox exchangeOutbox,
            QnaOutbox qnaOutbox,
            InboundQnaPolling inboundQnaPolling,
            InboundQnaRetry inboundQnaRetry,
            InboundOrderPolling purchaseConfirmedPolling) {}

    public record OutboundSyncOutbox(
            ProcessPending processPending, RecoverTimeout recoverTimeout) {}

    public record OutboundSellerOutbox(
            ProcessPending processPending, RecoverTimeout recoverTimeout) {}

    public record IntelligencePipeline(
            ProcessPending processPending,
            RecoverTimeout recoverTimeout,
            RecoverStuckAggregation recoverStuckAggregation) {}

    public record RecoverStuckAggregation(
            boolean enabled, String cron, String timezone, int batchSize, long stuckSeconds) {}

    public record SellerAuthOutbox(ProcessPending processPending, RecoverTimeout recoverTimeout) {}

    public record SellerAdminAuthOutbox(
            ProcessPending processPending, RecoverTimeout recoverTimeout) {}

    public record SellerAdminEmailOutbox(
            ProcessPending processPending, RecoverTimeout recoverTimeout) {}

    public record ImageUploadOutbox(
            ProcessPending processPending,
            PollProcessing pollProcessing,
            RecoverTimeout recoverTimeout,
            RecoverFailed recoverFailed) {}

    public record RecoverFailed(
            boolean enabled,
            String cron,
            String timezone,
            int batchSize,
            long failedAfterSeconds) {}

    public record ImageTransformOutbox(
            ProcessPending processPending,
            PollProcessing pollProcessing,
            RecoverTimeout recoverTimeout) {}

    public record PollProcessing(boolean enabled, String cron, String timezone, int batchSize) {}

    public record DescriptionPublish(
            boolean enabled, String cron, String timezone, int batchSize) {}

    public record ProcessPending(
            boolean enabled, String cron, String timezone, int batchSize, int delaySeconds) {}

    public record RecoverTimeout(
            boolean enabled, String cron, String timezone, int batchSize, long timeoutSeconds) {}

    public record InboundProductRetry(
            boolean enabled, String cron, String timezone, int batchSize) {}

    public record LegacyConversionSeeder(
            boolean enabled, String cron, String timezone, int batchSize, int maxTotal) {}

    public record InboundOrderPolling(boolean enabled, List<InboundOrderPollingEntry> entries) {}

    public record InboundOrderPollingEntry(
            long salesChannelId, boolean enabled, String cron, String timezone, int batchSize) {}

    public record InboundOrderRetry(boolean enabled, String cron, String timezone, int batchSize) {}

    public record SellicOrderIssuing(
            boolean enabled, String cron, String timezone, int batchSize, long salesChannelId) {}

    public record ShipmentOutbox(ProcessPending processPending, RecoverTimeout recoverTimeout) {}

    public record CancelOutbox(ProcessPending processPending, RecoverTimeout recoverTimeout) {}

    public record RefundOutbox(ProcessPending processPending, RecoverTimeout recoverTimeout) {}

    public record ExchangeOutbox(ProcessPending processPending, RecoverTimeout recoverTimeout) {}

    public record QnaOutbox(ProcessPending processPending, RecoverTimeout recoverTimeout) {}

    public record InboundQnaPolling(
            boolean enabled,
            String cron,
            String timezone,
            int batchSize,
            List<Long> salesChannelIds) {}

    public record InboundQnaRetry(boolean enabled, String cron, String timezone, int batchSize) {}

    public record ImageVariantSyncOutbox(ImageVariantSyncProcessPending processPending) {}

    public record ImageVariantSyncProcessPending(
            boolean enabled, String cron, String timezone, int batchSize) {}

}
