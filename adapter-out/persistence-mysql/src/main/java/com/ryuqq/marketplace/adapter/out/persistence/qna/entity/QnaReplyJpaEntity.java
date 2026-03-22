package com.ryuqq.marketplace.adapter.out.persistence.qna.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** QnaReply JPA 엔티티. */
@Entity
@Table(name = "qna_replies")
public class QnaReplyJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "qna_id", nullable = false)
    private long qnaId;

    @Column(name = "parent_reply_id")
    private Long parentReplyId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "author_name", nullable = false, length = 100)
    private String authorName;

    @Column(name = "reply_type", nullable = false, length = 30)
    private String replyType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected QnaReplyJpaEntity() {}

    private QnaReplyJpaEntity(
            Long id,
            long qnaId,
            Long parentReplyId,
            String content,
            String authorName,
            String replyType,
            Instant createdAt) {
        this.id = id;
        this.qnaId = qnaId;
        this.parentReplyId = parentReplyId;
        this.content = content;
        this.authorName = authorName;
        this.replyType = replyType;
        this.createdAt = createdAt;
    }

    public static QnaReplyJpaEntity create(
            Long id,
            long qnaId,
            Long parentReplyId,
            String content,
            String authorName,
            String replyType,
            Instant createdAt) {
        return new QnaReplyJpaEntity(id, qnaId, parentReplyId, content, authorName, replyType, createdAt);
    }

    public Long getId() {
        return id;
    }

    public long getQnaId() {
        return qnaId;
    }

    public Long getParentReplyId() {
        return parentReplyId;
    }

    public String getContent() {
        return content;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getReplyType() {
        return replyType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
