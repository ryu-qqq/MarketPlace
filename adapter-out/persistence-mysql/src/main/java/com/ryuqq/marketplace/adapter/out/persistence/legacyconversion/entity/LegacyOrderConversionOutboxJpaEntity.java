package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;

/**
 * LegacyOrderConversionOutboxJpaEntity - 레거시 주문 변환 Outbox JPA 엔티티.
 *
 * <p>레거시 주문 → 내부 주문 변환 요청을 추적하는 Outbox 테이블입니다. legacyOrderId + legacyPaymentId를 저장하고, 변환 시점에 데이터를
 * 조회합니다.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 */
@Entity
@Table(name = "legacy_order_conversion_outboxes")
public class LegacyOrderConversionOutboxJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_order_id", nullable = false, unique = true)
    private long legacyOrderId;

    @Column(name = "legacy_payment_id", nullable = false)
    private long legacyPaymentId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "max_retry", nullable = false)
    private int maxRetry;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected LegacyOrderConversionOutboxJpaEntity() {}

    private LegacyOrderConversionOutboxJpaEntity(
            Long id,
            long legacyOrderId,
            long legacyPaymentId,
            String status,
            int retryCount,
            int maxRetry,
            String errorMessage,
            Instant processedAt,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.legacyOrderId = legacyOrderId;
        this.legacyPaymentId = legacyPaymentId;
        this.status = status;
        this.retryCount = retryCount;
        this.maxRetry = maxRetry;
        this.errorMessage = errorMessage;
        this.processedAt = processedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static LegacyOrderConversionOutboxJpaEntity create(
            Long id,
            long legacyOrderId,
            long legacyPaymentId,
            String status,
            int retryCount,
            int maxRetry,
            String errorMessage,
            Instant processedAt,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        return new LegacyOrderConversionOutboxJpaEntity(
                id,
                legacyOrderId,
                legacyPaymentId,
                status,
                retryCount,
                maxRetry,
                errorMessage,
                processedAt,
                createdAt,
                updatedAt,
                version);
    }

    public Long getId() {
        return id;
    }

    public long getLegacyOrderId() {
        return legacyOrderId;
    }

    public long getLegacyPaymentId() {
        return legacyPaymentId;
    }

    public String getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public long getVersion() {
        return version;
    }
}
