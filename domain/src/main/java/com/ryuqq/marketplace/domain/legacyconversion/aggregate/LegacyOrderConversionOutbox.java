package com.ryuqq.marketplace.domain.legacyconversion.aggregate;

import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyOrderConversionOutboxId;
import com.ryuqq.marketplace.domain.legacyconversion.vo.LegacyConversionOutboxStatus;
import java.time.Instant;

/**
 * 레거시 주문 변환 Outbox Aggregate.
 *
 * <p>레거시 주문(orders) 단위로 생성되며, 스케줄러에 의해 내부 주문으로 변환됩니다. legacyOrderId +
 * legacyPaymentId를 저장하고, 변환 시점에 luxurydb에서 최신 데이터를 조회합니다.
 */
public class LegacyOrderConversionOutbox {

    private static final int DEFAULT_MAX_RETRY = 3;

    private final LegacyOrderConversionOutboxId id;
    private final long legacyOrderId;
    private final long legacyPaymentId;
    private LegacyConversionOutboxStatus status;
    private int retryCount;
    private final int maxRetry;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant processedAt;
    private String errorMessage;
    private long version;

    private LegacyOrderConversionOutbox(
            LegacyOrderConversionOutboxId id,
            long legacyOrderId,
            long legacyPaymentId,
            LegacyConversionOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version) {
        this.id = id;
        this.legacyOrderId = legacyOrderId;
        this.legacyPaymentId = legacyPaymentId;
        this.status = status;
        this.retryCount = retryCount;
        this.maxRetry = maxRetry;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.processedAt = processedAt;
        this.errorMessage = errorMessage;
        this.version = version;
    }

    public static LegacyOrderConversionOutbox forNew(
            long legacyOrderId, long legacyPaymentId, Instant now) {
        return new LegacyOrderConversionOutbox(
                LegacyOrderConversionOutboxId.forNew(),
                legacyOrderId,
                legacyPaymentId,
                LegacyConversionOutboxStatus.PENDING,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L);
    }

    public static LegacyOrderConversionOutbox reconstitute(
            LegacyOrderConversionOutboxId id,
            long legacyOrderId,
            long legacyPaymentId,
            LegacyConversionOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version) {
        return new LegacyOrderConversionOutbox(
                id,
                legacyOrderId,
                legacyPaymentId,
                status,
                retryCount,
                maxRetry,
                createdAt,
                updatedAt,
                processedAt,
                errorMessage,
                version);
    }

    public boolean isNew() {
        return id.isNew();
    }

    public void startProcessing(Instant now) {
        if (!status.canProcess()) {
            throw new IllegalStateException("처리할 수 없는 상태입니다. 현재 상태: " + status);
        }
        this.status = LegacyConversionOutboxStatus.PROCESSING;
        this.updatedAt = now;
    }

    public void complete(Instant now) {
        this.status = LegacyConversionOutboxStatus.COMPLETED;
        this.processedAt = now;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    public void failAndRetry(String errorMessage, Instant now) {
        this.retryCount++;
        this.errorMessage = errorMessage;
        this.updatedAt = now;

        if (this.retryCount >= this.maxRetry) {
            this.status = LegacyConversionOutboxStatus.FAILED;
            this.processedAt = now;
        } else {
            this.status = LegacyConversionOutboxStatus.PENDING;
        }
    }

    public void recoverFromTimeout(Instant now) {
        if (this.status != LegacyConversionOutboxStatus.PROCESSING) {
            throw new IllegalStateException(
                    "타임아웃 복구는 PROCESSING 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.status = LegacyConversionOutboxStatus.PENDING;
        this.updatedAt = now;
        this.errorMessage = "타임아웃으로 인한 복구";
    }

    public boolean canRetry() {
        return retryCount < maxRetry && status.canProcess();
    }

    // Getters
    public LegacyOrderConversionOutboxId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public long legacyOrderId() {
        return legacyOrderId;
    }

    public long legacyPaymentId() {
        return legacyPaymentId;
    }

    public LegacyConversionOutboxStatus status() {
        return status;
    }

    public int retryCount() {
        return retryCount;
    }

    public int maxRetry() {
        return maxRetry;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Instant processedAt() {
        return processedAt;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public long version() {
        return version;
    }

    public void refreshVersion(long version) {
        this.version = version;
    }
}
