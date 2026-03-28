package com.ryuqq.marketplace.domain.refund.outbox.aggregate;

import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.outbox.id.RefundOutboxId;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxStatus;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxType;
import java.time.Instant;

/**
 * 환불 상태 변경 Outbox Aggregate.
 *
 * <p>환불 상태 변경 시 외부 판매채널에 동기화하기 위한 Outbox 패턴 구현체입니다.
 */
public class RefundOutbox {

    private static final int DEFAULT_MAX_RETRY = 3;

    private final RefundOutboxId id;
    private final OrderItemId orderItemId;
    private final RefundOutboxType outboxType;
    private RefundOutboxStatus status;
    private final String payload;
    private int retryCount;
    private final int maxRetry;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant processedAt;
    private String errorMessage;
    private long version;
    private final RefundOutboxIdempotencyKey idempotencyKey;

    private RefundOutbox(
            RefundOutboxId id,
            OrderItemId orderItemId,
            RefundOutboxType outboxType,
            RefundOutboxStatus status,
            String payload,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            RefundOutboxIdempotencyKey idempotencyKey) {
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

    public static RefundOutbox forNew(
            OrderItemId orderItemId, RefundOutboxType outboxType, String payload, Instant now) {
        RefundOutboxIdempotencyKey idempotencyKey =
                RefundOutboxIdempotencyKey.generate(
                        String.valueOf(orderItemId.value()), outboxType, now);
        return new RefundOutbox(
                RefundOutboxId.forNew(),
                orderItemId,
                outboxType,
                RefundOutboxStatus.PENDING,
                payload,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                idempotencyKey);
    }

    public static RefundOutbox reconstitute(
            RefundOutboxId id,
            OrderItemId orderItemId,
            RefundOutboxType outboxType,
            RefundOutboxStatus status,
            String payload,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        return new RefundOutbox(
                id,
                orderItemId,
                outboxType,
                status,
                payload,
                retryCount,
                maxRetry,
                createdAt,
                updatedAt,
                processedAt,
                errorMessage,
                version,
                RefundOutboxIdempotencyKey.of(idempotencyKey));
    }

    public boolean isNew() {
        return id.isNew();
    }

    /** 처리 시작. PENDING → PROCESSING. */
    public void startProcessing(Instant now) {
        if (!status.isPending()) {
            throw new IllegalStateException("PENDING 상태에서만 처리를 시작할 수 있습니다. 현재 상태: " + status);
        }
        this.status = RefundOutboxStatus.PROCESSING;
        this.updatedAt = now;
    }

    /** 처리 완료. PROCESSING → COMPLETED. */
    public void complete(Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException("PROCESSING 상태에서만 완료할 수 있습니다. 현재 상태: " + status);
        }
        this.status = RefundOutboxStatus.COMPLETED;
        this.processedAt = now;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    /** 처리 실패 및 재시도. 최대 재시도 초과 시 FAILED. */
    public void failAndRetry(String errorMessage, Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException("PROCESSING 상태에서만 실패 처리할 수 있습니다. 현재 상태: " + status);
        }
        this.retryCount++;
        this.errorMessage = errorMessage;
        this.updatedAt = now;

        if (this.retryCount >= this.maxRetry) {
            this.status = RefundOutboxStatus.FAILED;
            this.processedAt = now;
        } else {
            this.status = RefundOutboxStatus.PENDING;
        }
    }

    /** 즉시 실패 처리 (재시도 없이). */
    public void fail(String errorMessage, Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException("PROCESSING 상태에서만 실패 처리할 수 있습니다. 현재 상태: " + status);
        }
        this.status = RefundOutboxStatus.FAILED;
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
            throw new IllegalStateException("FAILED 상태에서만 재처리할 수 있습니다. 현재 상태: " + status);
        }
        this.status = RefundOutboxStatus.PENDING;
        this.retryCount = 0;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    /** PROCESSING 타임아웃 복구. */
    public void recoverFromTimeout(Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException("타임아웃 복구는 PROCESSING 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.status = RefundOutboxStatus.PENDING;
        this.updatedAt = now;
        this.errorMessage = "타임아웃으로 인한 복구";
    }

    public boolean canRetry() {
        return retryCount < maxRetry && status.canProcess();
    }

    public boolean shouldProcess() {
        return status.isPending();
    }

    public RefundOutboxId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public OrderItemId orderItemId() {
        return orderItemId;
    }

    public Long orderItemIdValue() {
        return orderItemId.value();
    }

    public RefundOutboxType outboxType() {
        return outboxType;
    }

    public RefundOutboxStatus status() {
        return status;
    }

    public String payload() {
        return payload;
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

    public RefundOutboxIdempotencyKey idempotencyKey() {
        return idempotencyKey;
    }

    public String idempotencyKeyValue() {
        return idempotencyKey.value();
    }

    public boolean isPending() {
        return status.isPending();
    }

    public boolean isProcessing() {
        return status.isProcessing();
    }

    public boolean isCompleted() {
        return status.isCompleted();
    }

    public boolean isFailed() {
        return status.isFailed();
    }
}
