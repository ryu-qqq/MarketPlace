package com.ryuqq.marketplace.domain.setofsync.aggregate;

import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.setofsync.id.SetofSyncOutboxId;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncEntityType;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncOperationType;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncOutboxStatus;
import java.time.Instant;

/**
 * Setof Commerce 셀러 데이터 동기화 Outbox Aggregate.
 *
 * <p>셀러, 배송정책, 환불정책, 셀러주소 엔티티의 생성/수정/삭제 시 Setof Commerce에 동기화하기 위한 통합 Outbox 패턴 구현체입니다.
 *
 * <p><strong>동시성 제어</strong>:
 *
 * <ul>
 *   <li>version: 낙관적 락을 위한 버전 필드
 *   <li>updatedAt: PROCESSING 좀비 상태 감지를 위한 갱신 시각
 *   <li>idempotencyKey: 외부 API 호출 멱등성 보장
 * </ul>
 */
public class SetofSyncOutbox {

    private static final int DEFAULT_MAX_RETRY = 3;

    private final SetofSyncOutboxId id;
    private final SellerId sellerId;
    private final Long entityId;
    private final SetofSyncEntityType entityType;
    private final SetofSyncOperationType operationType;
    private SetofSyncOutboxStatus status;
    private int retryCount;
    private final int maxRetry;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant processedAt;
    private String errorMessage;
    private long version;
    private final SetofSyncOutboxIdempotencyKey idempotencyKey;

    private SetofSyncOutbox(
            SetofSyncOutboxId id,
            SellerId sellerId,
            Long entityId,
            SetofSyncEntityType entityType,
            SetofSyncOperationType operationType,
            SetofSyncOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            SetofSyncOutboxIdempotencyKey idempotencyKey) {
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

    public static SetofSyncOutbox forNew(
            SellerId sellerId,
            Long entityId,
            SetofSyncEntityType entityType,
            SetofSyncOperationType operationType,
            Instant now) {
        SetofSyncOutboxIdempotencyKey idempotencyKey =
                SetofSyncOutboxIdempotencyKey.generate(entityType, entityId, now);
        return new SetofSyncOutbox(
                SetofSyncOutboxId.forNew(),
                sellerId,
                entityId,
                entityType,
                operationType,
                SetofSyncOutboxStatus.PENDING,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                idempotencyKey);
    }

    public static SetofSyncOutbox reconstitute(
            SetofSyncOutboxId id,
            SellerId sellerId,
            Long entityId,
            SetofSyncEntityType entityType,
            SetofSyncOperationType operationType,
            SetofSyncOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        return new SetofSyncOutbox(
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
                SetofSyncOutboxIdempotencyKey.of(idempotencyKey));
    }

    public boolean isNew() {
        return id.isNew();
    }

    public void startProcessing(Instant now) {
        if (!status.canProcess()) {
            throw new IllegalStateException("처리할 수 없는 상태입니다. 현재 상태: " + status);
        }
        this.status = SetofSyncOutboxStatus.PROCESSING;
        this.updatedAt = now;
    }

    public void complete(Instant now) {
        this.status = SetofSyncOutboxStatus.COMPLETED;
        this.processedAt = now;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    public void failAndRetry(String errorMessage, Instant now) {
        this.retryCount++;
        this.errorMessage = errorMessage;
        this.updatedAt = now;

        if (this.retryCount >= this.maxRetry) {
            this.status = SetofSyncOutboxStatus.FAILED;
            this.processedAt = now;
        } else {
            this.status = SetofSyncOutboxStatus.PENDING;
        }
    }

    public void fail(String errorMessage, Instant now) {
        this.status = SetofSyncOutboxStatus.FAILED;
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
        if (this.status != SetofSyncOutboxStatus.PROCESSING) {
            throw new IllegalStateException("타임아웃 복구는 PROCESSING 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.status = SetofSyncOutboxStatus.PENDING;
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
    public SetofSyncOutboxId id() {
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

    public SetofSyncEntityType entityType() {
        return entityType;
    }

    public SetofSyncOperationType operationType() {
        return operationType;
    }

    public SetofSyncOutboxStatus status() {
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

    public SetofSyncOutboxIdempotencyKey idempotencyKey() {
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
