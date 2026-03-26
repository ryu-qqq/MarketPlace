package com.ryuqq.marketplace.adapter.out.persistence.qna.entity;

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

/** Qna JPA 엔티티. */
@Entity
@Table(name = "qnas")
public class QnaJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private long sellerId;

    @Column(name = "product_group_id", nullable = false)
    private long productGroupId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "qna_type", nullable = false, length = 30)
    private String qnaType;

    @Column(name = "sales_channel_id", nullable = false)
    private long salesChannelId;

    @Column(name = "external_qna_id", nullable = false, length = 100)
    private String externalQnaId;

    @Column(name = "question_title", length = 500)
    private String questionTitle;

    @Column(name = "question_content", nullable = false, columnDefinition = "TEXT")
    private String questionContent;

    @Column(name = "question_author", nullable = false, length = 100)
    private String questionAuthor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private Status status;

    protected QnaJpaEntity() {
        super();
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private QnaJpaEntity(
            Long id,
            long sellerId,
            long productGroupId,
            Long orderId,
            String qnaType,
            long salesChannelId,
            String externalQnaId,
            String questionTitle,
            String questionContent,
            String questionAuthor,
            Status status,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.sellerId = sellerId;
        this.productGroupId = productGroupId;
        this.orderId = orderId;
        this.qnaType = qnaType;
        this.salesChannelId = salesChannelId;
        this.externalQnaId = externalQnaId;
        this.questionTitle = questionTitle;
        this.questionContent = questionContent;
        this.questionAuthor = questionAuthor;
        this.status = status;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static QnaJpaEntity create(
            Long id,
            long sellerId,
            long productGroupId,
            Long orderId,
            String qnaType,
            long salesChannelId,
            String externalQnaId,
            String questionTitle,
            String questionContent,
            String questionAuthor,
            Status status,
            Instant createdAt,
            Instant updatedAt) {
        return new QnaJpaEntity(
                id, sellerId, productGroupId, orderId, qnaType, salesChannelId,
                externalQnaId, questionTitle, questionContent, questionAuthor, status,
                createdAt, updatedAt);
    }

    public enum Status {
        PENDING, ANSWERED, CLOSED
    }

    public Long getId() {
        return id;
    }

    public long getSellerId() {
        return sellerId;
    }

    public long getProductGroupId() {
        return productGroupId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getQnaType() {
        return qnaType;
    }

    public long getSalesChannelId() {
        return salesChannelId;
    }

    public String getExternalQnaId() {
        return externalQnaId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public String getQuestionAuthor() {
        return questionAuthor;
    }

    public Status getStatus() {
        return status;
    }
}
