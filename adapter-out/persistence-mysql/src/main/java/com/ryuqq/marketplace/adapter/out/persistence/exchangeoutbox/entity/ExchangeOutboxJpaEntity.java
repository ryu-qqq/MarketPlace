package com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;

/** 교환 아웃박스 JPA 엔티티. */
@Entity
@Table(name = "exchange_outboxes")
public class ExchangeOutboxJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    @Column(name = "outbox_type", nullable = false, length = 20)
    private String outboxType;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "max_retry", nullable = false)
    private int maxRetry;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "idempotency_key", nullable = false, length = 100, unique = true)
    private String idempotencyKey;

    protected ExchangeOutboxJpaEntity() {}

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private ExchangeOutboxJpaEntity(
            Long id,
            Long orderItemId,
            String outboxType,
            String status,
            String payload,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
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

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static ExchangeOutboxJpaEntity create(
            Long id,
            Long orderItemId,
            String outboxType,
            String status,
            String payload,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        return new ExchangeOutboxJpaEntity(
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
                idempotencyKey);
    }

    public Long getId() {
        return id;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public String getOutboxType() {
        return outboxType;
    }

    public String getStatus() {
        return status;
    }

    public String getPayload() {
        return payload;
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
}
