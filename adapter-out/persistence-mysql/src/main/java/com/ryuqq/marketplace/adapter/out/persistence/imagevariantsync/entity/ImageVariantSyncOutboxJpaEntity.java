package com.ryuqq.marketplace.adapter.out.persistence.imagevariantsync.entity;

import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariantsync.vo.ImageVariantSyncOutboxStatus;
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
 * ImageVariantSyncOutboxJpaEntity - 이미지 Variant Sync Outbox JPA 엔티티.
 *
 * <p>이미지 변환 완료 후 세토프 Sync API로 Variant 정보를 동기화하기 위한 Outbox 테이블입니다.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 */
@Entity
@Table(name = "image_variant_sync_outboxes")
public class ImageVariantSyncOutboxJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_image_id", nullable = false)
    private Long sourceImageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 30)
    private ImageSourceType sourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ImageVariantSyncOutboxStatus status;

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

    protected ImageVariantSyncOutboxJpaEntity() {}

    private ImageVariantSyncOutboxJpaEntity(
            Long id,
            Long sourceImageId,
            ImageSourceType sourceType,
            ImageVariantSyncOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version) {
        this.id = id;
        this.sourceImageId = sourceImageId;
        this.sourceType = sourceType;
        this.status = status;
        this.retryCount = retryCount;
        this.maxRetry = maxRetry;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.processedAt = processedAt;
        this.errorMessage = errorMessage;
        this.version = version;
    }

    public static ImageVariantSyncOutboxJpaEntity create(
            Long id,
            Long sourceImageId,
            ImageSourceType sourceType,
            ImageVariantSyncOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version) {
        return new ImageVariantSyncOutboxJpaEntity(
                id,
                sourceImageId,
                sourceType,
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

    public Long getSourceImageId() {
        return sourceImageId;
    }

    public ImageSourceType getSourceType() {
        return sourceType;
    }

    public ImageVariantSyncOutboxStatus getStatus() {
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
}
