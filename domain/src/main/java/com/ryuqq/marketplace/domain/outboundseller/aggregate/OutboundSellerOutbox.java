package com.ryuqq.marketplace.domain.outboundseller.aggregate;

import com.ryuqq.marketplace.domain.outboundseller.id.OutboundSellerOutboxId;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerEntityType;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerOperationType;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerOutboxStatus;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;

/**
 * 외부 셀러 시스템 데이터 동기화 Outbox Aggregate.
 *
 * <p>셀러, 배송정책, 환불정책, 셀러주소 엔티티의 생성/수정/삭제 시 외부 시스템에 동기화하기 위한 통합 Outbox 패턴 구현체입니다.
 *
 * <p><strong>동시성 제어</strong>:
 *
 * <ul>
 *   <li>version: 낙관적 락을 위한 버전 필드
 *   <li>updatedAt: PROCESSING 좀비 상태 감지를 위한 갱신 시각
 *   <li>idempotencyKey: 외부 API 호출 멱등성 보장
 * </ul>
 */
public class OutboundSellerOutbox {

    private static final int DEFAULT_MAX_RETRY = 3;

    private final OutboundSellerOutboxId id;
    private final SellerId sellerId;
    private final Long entityId;
    private final OutboundSellerEntityType entityType;
    private final OutboundSellerOperationType operationType;
    private OutboundSellerOutboxStatus status;
    private int retryCount;
    private final int maxRetry;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant processedAt;
    private String errorMessage;
    private long version;
    private final OutboundSellerOutboxIdempotencyKey idempotencyKey;

    private OutboundSellerOutbox(
            OutboundSellerOutboxId id,
            SellerId sellerId,
            Long entityId,
            OutboundSellerEntityType entityType,
            OutboundSellerOperationType operationType,
            OutboundSellerOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            OutboundSellerOutboxIdempotencyKey idempotencyKey) {
        this.id = id;
        this.sellerId = sellerId;
        this.entityId = entityId;
        this.entityType = entityType;
        this.operationType = operationType;
        this.status = status;
        this.retryCount = retryCount;
        this.maxRetry = maxRetry;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.processedAt = processedAt;
        this.errorMessage = errorMessage;
        this.version = version;
        this.idempotencyKey = idempotencyKey;
    }

    public static OutboundSellerOutbox forNew(
            SellerId sellerId,
            Long entityId,
            OutboundSellerEntityType entityType,
            OutboundSellerOperationType operationType,
            Instant now) {
        OutboundSellerOutboxIdempotencyKey idempotencyKey =
                OutboundSellerOutboxIdempotencyKey.generate(entityType, entityId, now);
        return new OutboundSellerOutbox(
                OutboundSellerOutboxId.forNew(),
                sellerId,
                entityId,
                entityType,
                operationType,
                OutboundSellerOutboxStatus.PENDING,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                idempotencyKey);
    }

    public static OutboundSellerOutbox reconstitute(
            OutboundSellerOutboxId id,
            SellerId sellerId,
            Long entityId,
            OutboundSellerEntityType entityType,
            OutboundSellerOperationType operationType,
            OutboundSellerOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        return new OutboundSellerOutbox(
                id,
                sellerId,
                entityId,
                entityType,
                operationType,
                status,
                retryCount,
                maxRetry,
                createdAt,
                updatedAt,
                processedAt,
                errorMessage,
                version,
                OutboundSellerOutboxIdempotencyKey.of(idempotencyKey));
    }

    public boolean isNew() {
        return id.isNew();
    }

    public void startProcessing(Instant now) {
        if (!status.canProcess()) {
            throw new IllegalStateException("처리할 수 없는 상태입니다. 현재 상태: " + status);
        }
        this.status = OutboundSellerOutboxStatus.PROCESSING;
        this.updatedAt = now;
    }

    public void complete(Instant now) {
        this.status = OutboundSellerOutboxStatus.COMPLETED;
        this.processedAt = now;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    public void failAndRetry(String errorMessage, Instant now) {
        this.retryCount++;
        this.errorMessage = errorMessage;
        this.updatedAt = now;

        if (this.retryCount >= this.maxRetry) {
            this.status = OutboundSellerOutboxStatus.FAILED;
            this.processedAt = now;
        } else {
            this.status = OutboundSellerOutboxStatus.PENDING;
        }
    }

    public void fail(String errorMessage, Instant now) {
        this.status = OutboundSellerOutboxStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = now;
        this.updatedAt = now;
    }

    public void recordFailure(boolean canRetry, String errorMessage, Instant now) {
        if (canRetry) {
            failAndRetry(errorMessage, now);
        } else {
            fail(errorMessage, now);
        }
    }

    public void recoverFromTimeout(Instant now) {
        if (this.status != OutboundSellerOutboxStatus.PROCESSING) {
            throw new IllegalStateException("타임아웃 복구는 PROCESSING 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.status = OutboundSellerOutboxStatus.PENDING;
        this.updatedAt = now;
        this.errorMessage = "타임아웃으로 인한 복구";
    }

    public boolean canRetry() {
        return retryCount < maxRetry && status.canProcess();
    }

    public boolean shouldProcess() {
        return status.isPending();
    }

    // Getters
    public OutboundSellerOutboxId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public SellerId sellerId() {
        return sellerId;
    }

    public Long sellerIdValue() {
        return sellerId.value();
    }

    public Long entityId() {
        return entityId;
    }

    public OutboundSellerEntityType entityType() {
        return entityType;
    }

    public OutboundSellerOperationType operationType() {
        return operationType;
    }

    public OutboundSellerOutboxStatus status() {
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

    public OutboundSellerOutboxIdempotencyKey idempotencyKey() {
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
