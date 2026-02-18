package com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.entity;

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
 * ProductGroupInspectionOutboxJpaEntity - 상품 그룹 검수 Outbox JPA 엔티티.
 *
 * <p>상품 그룹 등록 후 자동 검수를 비동기로 처리하기 위한 Outbox 테이블입니다.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 */
@Entity
@Table(name = "product_group_inspection_outboxes")
public class ProductGroupInspectionOutboxJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_group_id", nullable = false)
    private Long productGroupId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Column(name = "inspection_result_json", columnDefinition = "TEXT")
    private String inspectionResultJson;

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "passed")
    private Boolean passed;

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

    protected ProductGroupInspectionOutboxJpaEntity() {}

    private ProductGroupInspectionOutboxJpaEntity(
            Long id,
            Long productGroupId,
            Status status,
            String inspectionResultJson,
            Integer totalScore,
            Boolean passed,
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
        this.status = status;
        this.inspectionResultJson = inspectionResultJson;
        this.totalScore = totalScore;
        this.passed = passed;
        this.retryCount = retryCount;
        this.maxRetry = maxRetry;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.processedAt = processedAt;
        this.errorMessage = errorMessage;
        this.version = version;
        this.idempotencyKey = idempotencyKey;
    }

    public static ProductGroupInspectionOutboxJpaEntity create(
            Long id,
            Long productGroupId,
            Status status,
            String inspectionResultJson,
            Integer totalScore,
            Boolean passed,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        return new ProductGroupInspectionOutboxJpaEntity(
                id,
                productGroupId,
                status,
                inspectionResultJson,
                totalScore,
                passed,
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

    public Status getStatus() {
        return status;
    }

    public String getInspectionResultJson() {
        return inspectionResultJson;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public Boolean getPassed() {
        return passed;
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
        SENT,
        SCORING,
        ENHANCING,
        VERIFYING,
        COMPLETED,
        FAILED
    }
}
