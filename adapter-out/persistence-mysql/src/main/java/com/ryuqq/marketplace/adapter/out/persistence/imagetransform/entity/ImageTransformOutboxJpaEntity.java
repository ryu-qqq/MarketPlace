package com.ryuqq.marketplace.adapter.out.persistence.imagetransform.entity;

import com.ryuqq.marketplace.domain.imagetransform.vo.ImageTransformOutboxStatus;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
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
 * ImageTransformOutboxJpaEntity - 이미지 변환 Outbox JPA 엔티티.
 *
 * <p>멀티 사이즈 WEBP 변환을 위한 Outbox 테이블입니다.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 */
@Entity
@Table(name = "image_transform_outboxes")
public class ImageTransformOutboxJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_image_id", nullable = false)
    private Long sourceImageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 30)
    private ImageSourceType sourceType;

    @Column(name = "uploaded_url", nullable = false, length = 500)
    private String uploadedUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "variant_type", nullable = false, length = 30)
    private ImageVariantType variantType;

    @Column(name = "file_asset_id", length = 100)
    private String fileAssetId;

    @Column(name = "transform_request_id", length = 100)
    private String transformRequestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ImageTransformOutboxStatus status;

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

    protected ImageTransformOutboxJpaEntity() {}

    private ImageTransformOutboxJpaEntity(
            Long id,
            Long sourceImageId,
            ImageSourceType sourceType,
            String uploadedUrl,
            ImageVariantType variantType,
            String fileAssetId,
            String transformRequestId,
            ImageTransformOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        this.id = id;
        this.sourceImageId = sourceImageId;
        this.sourceType = sourceType;
        this.uploadedUrl = uploadedUrl;
        this.variantType = variantType;
        this.fileAssetId = fileAssetId;
        this.transformRequestId = transformRequestId;
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

    public static ImageTransformOutboxJpaEntity create(
            Long id,
            Long sourceImageId,
            ImageSourceType sourceType,
            String uploadedUrl,
            ImageVariantType variantType,
            String fileAssetId,
            String transformRequestId,
            ImageTransformOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        return new ImageTransformOutboxJpaEntity(
                id,
                sourceImageId,
                sourceType,
                uploadedUrl,
                variantType,
                fileAssetId,
                transformRequestId,
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

    public Long getSourceImageId() {
        return sourceImageId;
    }

    public ImageSourceType getSourceType() {
        return sourceType;
    }

    public String getUploadedUrl() {
        return uploadedUrl;
    }

    public ImageVariantType getVariantType() {
        return variantType;
    }

    public String getFileAssetId() {
        return fileAssetId;
    }

    public String getTransformRequestId() {
        return transformRequestId;
    }

    public ImageTransformOutboxStatus getStatus() {
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
}
