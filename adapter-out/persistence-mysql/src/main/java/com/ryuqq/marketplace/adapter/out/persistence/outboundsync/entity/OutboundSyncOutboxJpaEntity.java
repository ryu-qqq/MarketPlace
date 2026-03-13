package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity;

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

/**
 * OutboundSyncOutboxJpaEntity - 외부 상품 연동 Outbox JPA 엔티티.
 *
 * <p>외부 판매채널로의 상품 연동을 위한 Outbox 테이블입니다.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 *
 * <p>BaseAuditEntity 미상속: @Version 필드와 함께 created_at, updated_at을 직접 관리.
 */
@Entity
@Table(name = "outbound_sync_outboxes")
public class OutboundSyncOutboxJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_group_id", nullable = false)
    private Long productGroupId;

    @Column(name = "sales_channel_id", nullable = false)
    private Long salesChannelId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sync_type", nullable = false, length = 20)
    private SyncType syncType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

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

    @Column(name = "idempotency_key", nullable = false, length = 100, unique = true)
    private String idempotencyKey;

    protected OutboundSyncOutboxJpaEntity() {}

    private OutboundSyncOutboxJpaEntity(
            Long id,
            Long productGroupId,
            Long salesChannelId,
            Long shopId,
            Long sellerId,
            SyncType syncType,
            Status status,
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
        this.productGroupId = productGroupId;
        this.salesChannelId = salesChannelId;
        this.shopId = shopId;
        this.sellerId = sellerId;
        this.syncType = syncType;
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

    public static OutboundSyncOutboxJpaEntity of(
            Long id,
            Long productGroupId,
            Long salesChannelId,
            Long shopId,
            Long sellerId,
            SyncType syncType,
            Status status,
            String payload,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        return new OutboundSyncOutboxJpaEntity(
                id,
                productGroupId,
                salesChannelId,
                shopId,
                sellerId,
                syncType,
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

    public Long getProductGroupId() {
        return productGroupId;
    }

    public Long getSalesChannelId() {
        return salesChannelId;
    }

    public Long getShopId() {
        return shopId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public SyncType getSyncType() {
        return syncType;
    }

    public Status getStatus() {
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

    /** 연동 타입. */
    public enum SyncType {
        CREATE,
        UPDATE,
        DELETE
    }

    /** Outbox 상태. */
    public enum Status {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}
