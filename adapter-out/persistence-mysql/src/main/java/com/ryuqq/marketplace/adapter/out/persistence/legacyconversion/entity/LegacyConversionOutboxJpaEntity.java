package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity;

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
 * LegacyConversionOutboxJpaEntity - 레거시 변환 Outbox JPA 엔티티.
 *
 * <p>레거시 상품 → 내부 상품 변환 요청을 추적하는 Outbox 테이블입니다. payload 없이 legacyProductGroupId 참조만 저장합니다.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 */
@Entity
@Table(name = "legacy_conversion_outboxes")
public class LegacyConversionOutboxJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_product_group_id", nullable = false)
    private Long legacyProductGroupId;

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

    protected LegacyConversionOutboxJpaEntity() {}

    private LegacyConversionOutboxJpaEntity(
            Long id,
            Long legacyProductGroupId,
            Status status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version) {
        this.id = id;
        this.legacyProductGroupId = legacyProductGroupId;
        this.status = status;
        this.retryCount = retryCount;
        this.maxRetry = maxRetry;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.processedAt = processedAt;
        this.errorMessage = errorMessage;
        this.version = version;
    }

    public static LegacyConversionOutboxJpaEntity create(
            Long id,
            Long legacyProductGroupId,
            Status status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version) {
        return new LegacyConversionOutboxJpaEntity(
                id,
                legacyProductGroupId,
                status,
                retryCount,
                maxRetry,
                createdAt,
                updatedAt,
                processedAt,
                errorMessage,
                version);
    }

    public Long getId() {
        return id;
    }

    public Long getLegacyProductGroupId() {
        return legacyProductGroupId;
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

    /** Outbox 상태. */
    public enum Status {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}
