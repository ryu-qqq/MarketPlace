package com.ryuqq.marketplace.domain.qna.aggregate;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.qna.event.QnaAnsweredEvent;
import com.ryuqq.marketplace.domain.qna.id.QnaId;
import com.ryuqq.marketplace.domain.qna.vo.QnaReplyType;
import com.ryuqq.marketplace.domain.qna.vo.QnaSource;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Qna Aggregate Root.
 *
 * <p>내부 QnA 도메인 모델입니다. 질문 1건에 대해 여러 답변(QnaReply)을 관리합니다.
 * 상태 머신: PENDING → ANSWERED → CLOSED.
 */
public class Qna {

    private final QnaId id;
    private final long sellerId;
    private final long productGroupId;
    private final Long orderId;
    private final QnaType qnaType;
    private final QnaSource source;
    private String questionTitle;
    private String questionContent;
    private final String questionAuthor;

    private QnaStatus status;
    private final List<QnaReply> replies;

    private final List<DomainEvent> events = new ArrayList<>();

    private final Instant createdAt;
    private Instant updatedAt;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private Qna(
            QnaId id,
            long sellerId,
            long productGroupId,
            Long orderId,
            QnaType qnaType,
            QnaSource source,
            String questionTitle,
            String questionContent,
            String questionAuthor,
            QnaStatus status,
            List<QnaReply> replies,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.productGroupId = productGroupId;
        this.orderId = orderId;
        this.qnaType = qnaType;
        this.source = source;
        this.questionTitle = questionTitle;
        this.questionContent = questionContent;
        this.questionAuthor = questionAuthor;
        this.status = status;
        this.replies = new ArrayList<>(replies);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static Qna forNew(
            long sellerId,
            long productGroupId,
            Long orderId,
            QnaType qnaType,
            QnaSource source,
            String questionTitle,
            String questionContent,
            String questionAuthor,
            Instant now) {
        return new Qna(
                QnaId.forNew(),
                sellerId,
                productGroupId,
                orderId,
                qnaType,
                source,
                questionTitle,
                questionContent,
                questionAuthor,
                QnaStatus.PENDING,
                List.of(),
                now,
                now);
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static Qna reconstitute(
            QnaId id,
            long sellerId,
            long productGroupId,
            Long orderId,
            QnaType qnaType,
            QnaSource source,
            String questionTitle,
            String questionContent,
            String questionAuthor,
            QnaStatus status,
            List<QnaReply> replies,
            Instant createdAt,
            Instant updatedAt) {
        return new Qna(
                id,
                sellerId,
                productGroupId,
                orderId,
                qnaType,
                source,
                questionTitle,
                questionContent,
                questionAuthor,
                status,
                replies,
                createdAt,
                updatedAt);
    }

    /**
     * 판매자 답변을 등록합니다.
     *
     * <p>PENDING 상태에서만 호출 가능합니다. 답변 등록 시 QnaAnsweredEvent를 발행합니다.
     */
    public QnaReply answer(String content, String authorName, Long parentReplyId, Instant now) {
        if (!status.canAnswer()) {
            throw new IllegalStateException("현재 상태(" + status + ")에서 답변할 수 없습니다");
        }

        QnaReply reply = QnaReply.forNew(parentReplyId, content, authorName, QnaReplyType.SELLER_ANSWER, now);
        this.replies.add(reply);
        this.status = QnaStatus.ANSWERED;
        this.updatedAt = now;

        registerEvent(new QnaAnsweredEvent(
                id,
                sellerId,
                source.salesChannelId(),
                source.externalQnaId(),
                now));

        return reply;
    }

    /**
     * 기존 답변 내용을 수정합니다.
     *
     * <p>해당 replyId의 답변을 찾아 내용을 교체합니다.
     */
    public QnaReply updateReply(long replyId, String newContent, Instant now) {
        QnaReply reply = replies.stream()
                .filter(r -> r.idValue() != null && r.idValue() == replyId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("답변을 찾을 수 없습니다. replyId=" + replyId));

        reply.updateContent(newContent);
        this.updatedAt = now;
        return reply;
    }

    /**
     * 구매자 추가 질문을 등록합니다.
     *
     * <p>ANSWERED 상태에서 추가 질문이 오면 다시 PENDING으로 전환됩니다.
     */
    public QnaReply addFollowUp(String content, String authorName, Long parentReplyId, Instant now) {
        if (status.isTerminal()) {
            throw new IllegalStateException("종결된 QnA에는 추가 질문을 등록할 수 없습니다");
        }

        QnaReply reply = QnaReply.forNew(parentReplyId, content, authorName, QnaReplyType.BUYER_FOLLOW_UP, now);
        this.replies.add(reply);
        this.status = QnaStatus.PENDING;
        this.updatedAt = now;

        return reply;
    }

    /**
     * 질문 내용을 수정합니다.
     *
     * <p>외부몰에서 고객이 질문 내용을 수정한 경우 호출합니다.
     */
    public void updateQuestion(String newTitle, String newContent, Instant now) {
        if (newTitle != null && !newTitle.isBlank()) {
            this.questionTitle = newTitle;
        }
        if (newContent != null && !newContent.isBlank()) {
            this.questionContent = newContent;
        }
        this.updatedAt = now;
    }

    /**
     * QnA를 종결합니다.
     *
     * <p>ANSWERED 상태에서만 종결 가능합니다.
     */
    public void close(Instant now) {
        if (!status.canClose()) {
            throw new IllegalStateException("현재 상태(" + status + ")에서 종결할 수 없습니다");
        }
        this.status = QnaStatus.CLOSED;
        this.updatedAt = now;
    }

    public List<DomainEvent> pollEvents() {
        List<DomainEvent> snapshot = Collections.unmodifiableList(new ArrayList<>(events));
        events.clear();
        return snapshot;
    }

    protected void registerEvent(DomainEvent event) {
        events.add(event);
    }

    public Long idValue() {
        return id != null ? id.value() : null;
    }

    public QnaId id() {
        return id;
    }

    public long sellerId() {
        return sellerId;
    }

    public long productGroupId() {
        return productGroupId;
    }

    public Long orderId() {
        return orderId;
    }

    public QnaType qnaType() {
        return qnaType;
    }

    public QnaSource source() {
        return source;
    }

    public String questionTitle() {
        return questionTitle;
    }

    public String questionContent() {
        return questionContent;
    }

    public String questionAuthor() {
        return questionAuthor;
    }

    public QnaStatus status() {
        return status;
    }

    public List<QnaReply> replies() {
        return Collections.unmodifiableList(replies);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
