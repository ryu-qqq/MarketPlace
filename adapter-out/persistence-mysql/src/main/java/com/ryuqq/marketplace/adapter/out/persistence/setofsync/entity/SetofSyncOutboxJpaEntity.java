package com.ryuqq.marketplace.adapter.out.persistence.setofsync.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;

@Entity
@Table(name = "setof_sync_outboxes")
public class SetofSyncOutboxJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private long sellerId;

    @Column(name = "entity_id", nullable = false)
    private long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 30)
    private EntityType entityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 20)
    private OperationType operationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "max_retry", nullable = false)
    private int maxRetry;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    protected SetofSyncOutboxJpaEntity() {}

    public static SetofSyncOutboxJpaEntity create(
            Long id,
            long sellerId,
            long entityId,
            EntityType entityType,
            OperationType operationType,
            Status status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        SetofSyncOutboxJpaEntity entity = new SetofSyncOutboxJpaEntity();
        entity.id = id;
        entity.sellerId = sellerId;
        entity.entityId = entityId;
        entity.entityType = entityType;
        entity.operationType = operationType;
        entity.status = status;
        entity.retryCount = retryCount;
        entity.maxRetry = maxRetry;
        entity.createdAt = createdAt;
        entity.updatedAt = updatedAt;
        entity.processedAt = processedAt;
        entity.errorMessage = errorMessage;
        entity.version = version;
        entity.idempotencyKey = idempotencyKey;
        return entity;
    }

    public Long getId() {
        return id;
    }

    public long getSellerId() {
        return sellerId;
    }

    public long getEntityId() {
        return entityId;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public Status getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public long getVersion() {
        return version;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public enum Status {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }

    public enum EntityType {
        SELLER,
        SHIPPING_POLICY,
        REFUND_POLICY,
        SELLER_ADDRESS
    }

    public enum OperationType {
        CREATE,
        UPDATE,
        DELETE
    }
}
