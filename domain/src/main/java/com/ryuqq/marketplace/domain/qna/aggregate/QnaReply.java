package com.ryuqq.marketplace.domain.qna.aggregate;

import com.ryuqq.marketplace.domain.qna.id.QnaReplyId;
import com.ryuqq.marketplace.domain.qna.vo.QnaReplyType;
import java.time.Instant;

/**
 * QnaReply Entity.
 *
 * <p>QnA에 대한 답변 또는 추가 질문을 나타냅니다.
 * parentReplyId가 null이면 최상위 답변, 값이 있으면 대댓글입니다.
 */
public class QnaReply {

    private final QnaReplyId id;
    private final Long parentReplyId;
    private String content;
    private final String authorName;
    private final QnaReplyType replyType;
    private final Instant createdAt;

    private QnaReply(
            QnaReplyId id,
            Long parentReplyId,
            String content,
            String authorName,
            QnaReplyType replyType,
            Instant createdAt) {
        this.id = id;
        this.parentReplyId = parentReplyId;
        this.content = content;
        this.authorName = authorName;
        this.replyType = replyType;
        this.createdAt = createdAt;
    }

    public static QnaReply forNew(
            Long parentReplyId,
            String content,
            String authorName,
            QnaReplyType replyType,
            Instant now) {
        return new QnaReply(
                QnaReplyId.forNew(),
                parentReplyId,
                content,
                authorName,
                replyType,
                now);
    }

    public static QnaReply reconstitute(
            QnaReplyId id,
            Long parentReplyId,
            String content,
            String authorName,
            QnaReplyType replyType,
            Instant createdAt) {
        return new QnaReply(id, parentReplyId, content, authorName, replyType, createdAt);
    }

    /**
     * 답변 내용을 수정합니다.
     *
     * <p>판매자 답변(SELLER_ANSWER)만 수정 가능합니다.
     */
    public void updateContent(String newContent) {
        if (!isSellerAnswer()) {
            throw new IllegalStateException("판매자 답변만 수정할 수 있습니다");
        }
        this.content = newContent;
    }

    public boolean isTopLevel() {
        return parentReplyId == null;
    }

    public boolean isSellerAnswer() {
        return replyType == QnaReplyType.SELLER_ANSWER;
    }

    public Long idValue() {
        return id != null ? id.value() : null;
    }

    public QnaReplyId id() {
        return id;
    }

    public Long parentReplyId() {
        return parentReplyId;
    }

    public String content() {
        return content;
    }

    public String authorName() {
        return authorName;
    }

    public QnaReplyType replyType() {
        return replyType;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
