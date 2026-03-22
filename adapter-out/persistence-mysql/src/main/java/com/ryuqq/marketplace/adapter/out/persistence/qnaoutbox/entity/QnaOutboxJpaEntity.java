package com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
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

/** QnaOutbox JPA 엔티티. */
@Entity
@Table(name = "qna_outboxes")
public class QnaOutboxJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "qna_id", nullable = false)
    private long qnaId;

    @Column(name = "sales_channel_id", nullable = false)
    private long salesChannelId;

    @Column(name = "external_qna_id", nullable = false, length = 100)
    private String externalQnaId;

    @Column(name = "outbox_type", nullable = false, length = 30)
    private String outboxType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private Status status;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "max_retry", nullable = false)
    private int maxRetry;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "idempotency_key", nullable = false, length = 200, unique = true)
    private String idempotencyKey;

    protected QnaOutboxJpaEntity() {
        super();
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private QnaOutboxJpaEntity(
            Long id, long qnaId, long salesChannelId, String externalQnaId,
            String outboxType, Status status, String payload,
            int retryCount, int maxRetry, Instant createdAt, Instant updatedAt,
            Instant processedAt, String errorMessage, long version, String idempotencyKey) {
        super(createdAt, updatedAt);
        this.id = id;
        this.qnaId = qnaId;
        this.salesChannelId = salesChannelId;
        this.externalQnaId = externalQnaId;
        this.outboxType = outboxType;
        this.status = status;
        this.payload = payload;
        this.retryCount = retryCount;
        this.maxRetry = maxRetry;
        this.processedAt = processedAt;
        this.errorMessage = errorMessage;
        this.version = version;
        this.idempotencyKey = idempotencyKey;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static QnaOutboxJpaEntity create(
            Long id, long qnaId, long salesChannelId, String externalQnaId,
            String outboxType, Status status, String payload,
            int retryCount, int maxRetry, Instant createdAt, Instant updatedAt,
            Instant processedAt, String errorMessage, long version, String idempotencyKey) {
        return new QnaOutboxJpaEntity(
                id, qnaId, salesChannelId, externalQnaId,
                outboxType, status, payload,
                retryCount, maxRetry, createdAt, updatedAt,
                processedAt, errorMessage, version, idempotencyKey);
    }

    public enum Status {
        PENDING, PROCESSING, COMPLETED, FAILED
    }

    public Long getId() {
        return id;
    }

    public long getQnaId() {
        return qnaId;
    }

    public long getSalesChannelId() {
        return salesChannelId;
    }

    public String getExternalQnaId() {
        return externalQnaId;
    }

    public String getOutboxType() {
        return outboxType;
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
