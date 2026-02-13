package com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity;

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
 * ImageUploadOutboxJpaEntity - 이미지 업로드 Outbox JPA 엔티티.
 *
 * <p>S3 이미지 비동기 업로드를 위한 Outbox 테이블입니다.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 */
@Entity
@Table(name = "image_upload_outboxes")
public class ImageUploadOutboxJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 30)
    private SourceType sourceType;

    @Column(name = "origin_url", nullable = false, length = 500)
    private String originUrl;

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

    @Column(name = "idempotency_key", nullable = false, length = 100, unique = true)
    private String idempotencyKey;

    protected ImageUploadOutboxJpaEntity() {}

    private ImageUploadOutboxJpaEntity(
            Long id,
            Long sourceId,
            SourceType sourceType,
            String originUrl,
            Status status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        this.id = id;
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        this.originUrl = originUrl;
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

    public static ImageUploadOutboxJpaEntity create(
            Long id,
            Long sourceId,
            SourceType sourceType,
            String originUrl,
            Status status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        return new ImageUploadOutboxJpaEntity(
                id,
                sourceId,
                sourceType,
                originUrl,
                status,
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

    public Long getSourceId() {
        return sourceId;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public String getOriginUrl() {
        return originUrl;
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

    /** Outbox 상태. */
    public enum Status {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }

    /** 이미지 소스 타입. */
    public enum SourceType {
        PRODUCT_GROUP_IMAGE,
        DESCRIPTION_IMAGE
    }
}
