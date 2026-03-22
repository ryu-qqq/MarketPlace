package com.ryuqq.marketplace.adapter.out.persistence.inboundqna.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** InboundQna JPA 엔티티. */
@Entity
@Table(name = "inbound_qnas")
public class InboundQnaJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sales_channel_id", nullable = false)
    private long salesChannelId;

    @Column(name = "external_qna_id", nullable = false, length = 100)
    private String externalQnaId;

    @Column(name = "qna_type", nullable = false, length = 30)
    private String qnaType;

    @Column(name = "question_content", nullable = false, columnDefinition = "TEXT")
    private String questionContent;

    @Column(name = "question_author", nullable = false, length = 100)
    private String questionAuthor;

    @Column(name = "raw_payload", columnDefinition = "TEXT")
    private String rawPayload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private Status status;

    @Column(name = "internal_qna_id")
    private Long internalQnaId;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    protected InboundQnaJpaEntity() {
        super();
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private InboundQnaJpaEntity(
            Long id,
            long salesChannelId,
            String externalQnaId,
            String qnaType,
            String questionContent,
            String questionAuthor,
            String rawPayload,
            Status status,
            Long internalQnaId,
            String failureReason,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.salesChannelId = salesChannelId;
        this.externalQnaId = externalQnaId;
        this.qnaType = qnaType;
        this.questionContent = questionContent;
        this.questionAuthor = questionAuthor;
        this.rawPayload = rawPayload;
        this.status = status;
        this.internalQnaId = internalQnaId;
        this.failureReason = failureReason;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static InboundQnaJpaEntity create(
            Long id,
            long salesChannelId,
            String externalQnaId,
            String qnaType,
            String questionContent,
            String questionAuthor,
            String rawPayload,
            Status status,
            Long internalQnaId,
            String failureReason,
            Instant createdAt,
            Instant updatedAt) {
        return new InboundQnaJpaEntity(
                id,
                salesChannelId,
                externalQnaId,
                qnaType,
                questionContent,
                questionAuthor,
                rawPayload,
                status,
                internalQnaId,
                failureReason,
                createdAt,
                updatedAt);
    }

    public enum Status {
        RECEIVED,
        CONVERTED,
        FAILED
    }

    public Long getId() {
        return id;
    }

    public long getSalesChannelId() {
        return salesChannelId;
    }

    public String getExternalQnaId() {
        return externalQnaId;
    }

    public String getQnaType() {
        return qnaType;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public String getQuestionAuthor() {
        return questionAuthor;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public Status getStatus() {
        return status;
    }

    public Long getInternalQnaId() {
        return internalQnaId;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
