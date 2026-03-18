package com.ryuqq.marketplace.domain.exchange.outbox.aggregate;

import com.ryuqq.marketplace.domain.exchange.outbox.id.ExchangeOutboxId;
import com.ryuqq.marketplace.domain.exchange.outbox.vo.ExchangeOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.exchange.outbox.vo.ExchangeOutboxStatus;
import com.ryuqq.marketplace.domain.exchange.outbox.vo.ExchangeOutboxType;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;

/**
 * 교환 상태 변경 Outbox Aggregate.
 *
 * <p>교환 상태 변경 시 외부 판매채널에 동기화하기 위한 Outbox 패턴 구현체입니다.
 */
public class ExchangeOutbox {

    private static final int DEFAULT_MAX_RETRY = 3;

    private final ExchangeOutboxId id;
    private final OrderItemId orderItemId;
    private final ExchangeOutboxType outboxType;
    private ExchangeOutboxStatus status;
    private final String payload;
    private int retryCount;
    private final int maxRetry;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant processedAt;
    private String errorMessage;
    private long version;
    private final ExchangeOutboxIdempotencyKey idempotencyKey;

    private ExchangeOutbox(
            ExchangeOutboxId id,
            OrderItemId orderItemId,
            ExchangeOutboxType outboxType,
            ExchangeOutboxStatus status,
            String payload,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            ExchangeOutboxIdempotencyKey idempotencyKey) {
        this.id = id;
        this.orderItemId = orderItemId;
        this.outboxType = outboxType;
        this.status = status;
        this.payload = payload;
        this.retryCount = retryCount;
        this.maxRetry = maxRetry;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.processedAt = processedAt;
        this.errorMessage = errorMessage;
        this.version = version;
        this.idempotencyKey = idempotencyKey;
    }

    public static ExchangeOutbox forNew(
            OrderItemId orderItemId, ExchangeOutboxType outboxType, String payload, Instant now) {
        ExchangeOutboxIdempotencyKey idempotencyKey =
                ExchangeOutboxIdempotencyKey.generate(orderItemId.value(), outboxType, now);
        return new ExchangeOutbox(
                ExchangeOutboxId.forNew(), orderItemId, outboxType,
                ExchangeOutboxStatus.PENDING, payload,
                0, DEFAULT_MAX_RETRY, now, now, null, null, 0L, idempotencyKey);
    }

    public static ExchangeOutbox reconstitute(
            ExchangeOutboxId id, OrderItemId orderItemId, ExchangeOutboxType outboxType,
            ExchangeOutboxStatus status, String payload, int retryCount, int maxRetry,
            Instant createdAt, Instant updatedAt, Instant processedAt,
            String errorMessage, long version, String idempotencyKey) {
        return new ExchangeOutbox(
                id, orderItemId, outboxType, status, payload, retryCount, maxRetry,
                createdAt, updatedAt, processedAt, errorMessage, version,
                ExchangeOutboxIdempotencyKey.of(idempotencyKey));
    }

    public boolean isNew() { return id.isNew(); }

    /** 처리 시작. PENDING → PROCESSING. */
    public void startProcessing(Instant now) {
        if (!status.isPending()) {
            throw new IllegalStateException(
                    "PENDING 상태에서만 처리를 시작할 수 있습니다. 현재 상태: " + status);
        }
        this.status = ExchangeOutboxStatus.PROCESSING;
        this.updatedAt = now;
    }

    /** 처리 완료. PROCESSING → COMPLETED. */
    public void complete(Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException(
                    "PROCESSING 상태에서만 완료할 수 있습니다. 현재 상태: " + status);
        }
        this.status = ExchangeOutboxStatus.COMPLETED;
        this.processedAt = now;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    /** 처리 실패 및 재시도. 최대 재시도 초과 시 FAILED. */
    public void failAndRetry(String errorMessage, Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException(
                    "PROCESSING 상태에서만 실패 처리할 수 있습니다. 현재 상태: " + status);
        }
        this.retryCount++;
        this.errorMessage = errorMessage;
        this.updatedAt = now;
        if (this.retryCount >= this.maxRetry) {
            this.status = ExchangeOutboxStatus.FAILED;
            this.processedAt = now;
        } else {
            this.status = ExchangeOutboxStatus.PENDING;
        }
    }

    /** 즉시 실패 처리 (재시도 없이). */
    public void fail(String errorMessage, Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException(
                    "PROCESSING 상태에서만 실패 처리할 수 있습니다. 현재 상태: " + status);
        }
        this.status = ExchangeOutboxStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = now;
        this.updatedAt = now;
    }

    /** 외부 API 실패 결과를 반영합니다. */
    public void recordFailure(boolean canRetry, String errorMessage, Instant now) {
        if (canRetry) {
            failAndRetry(errorMessage, now);
        } else {
            fail(errorMessage, now);
        }
    }

    /** FAILED → PENDING 수동 재처리. */
    public void retry(Instant now) {
        if (!status.isFailed()) {
            throw new IllegalStateException(
                    "FAILED 상태에서만 재처리할 수 있습니다. 현재 상태: " + status);
        }
        this.status = ExchangeOutboxStatus.PENDING;
        this.retryCount = 0;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    /** PROCESSING 타임아웃 복구. */
    public void recoverFromTimeout(Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException(
                    "타임아웃 복구는 PROCESSING 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.status = ExchangeOutboxStatus.PENDING;
        this.updatedAt = now;
        this.errorMessage = "타임아웃으로 인한 복구";
    }

    public boolean canRetry() { return retryCount < maxRetry && status.canProcess(); }
    public boolean shouldProcess() { return status.isPending(); }

    public ExchangeOutboxId id() { return id; }
    public Long idValue() { return id.value(); }
    public OrderItemId orderItemId() { return orderItemId; }
    public String orderItemIdValue() { return orderItemId.value(); }
    public ExchangeOutboxType outboxType() { return outboxType; }
    public ExchangeOutboxStatus status() { return status; }
    public String payload() { return payload; }
    public int retryCount() { return retryCount; }
    public int maxRetry() { return maxRetry; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Instant processedAt() { return processedAt; }
    public String errorMessage() { return errorMessage; }
    public long version() { return version; }
    public void refreshVersion(long version) { this.version = version; }
    public ExchangeOutboxIdempotencyKey idempotencyKey() { return idempotencyKey; }
    public String idempotencyKeyValue() { return idempotencyKey.value(); }
    public boolean isPending() { return status.isPending(); }
    public boolean isProcessing() { return status.isProcessing(); }
    public boolean isCompleted() { return status.isCompleted(); }
    public boolean isFailed() { return status.isFailed(); }
}
