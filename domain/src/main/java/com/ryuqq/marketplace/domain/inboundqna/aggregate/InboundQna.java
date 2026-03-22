package com.ryuqq.marketplace.domain.inboundqna.aggregate;

import com.ryuqq.marketplace.domain.inboundqna.id.InboundQnaId;
import com.ryuqq.marketplace.domain.inboundqna.vo.InboundQnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import java.time.Instant;

/**
 * InboundQna Aggregate Root.
 *
 * <p>외부 판매채널에서 수신한 QnA 원본을 관리합니다. 내부 Qna 변환의 상태 머신을 제공합니다.
 */
public class InboundQna {

    private final InboundQnaId id;
    private final long salesChannelId;
    private final String externalQnaId;
    private final QnaType qnaType;
    private final String questionContent;
    private final String questionAuthor;
    private final String rawPayload;

    private InboundQnaStatus status;
    private Long internalQnaId;
    private String failureReason;

    private final Instant createdAt;
    private Instant updatedAt;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private InboundQna(
            InboundQnaId id,
            long salesChannelId,
            String externalQnaId,
            QnaType qnaType,
            String questionContent,
            String questionAuthor,
            String rawPayload,
            InboundQnaStatus status,
            Long internalQnaId,
            String failureReason,
            Instant createdAt,
            Instant updatedAt) {
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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static InboundQna forNew(
            long salesChannelId,
            String externalQnaId,
            QnaType qnaType,
            String questionContent,
            String questionAuthor,
            String rawPayload,
            Instant now) {
        return new InboundQna(
                InboundQnaId.forNew(),
                salesChannelId,
                externalQnaId,
                qnaType,
                questionContent,
                questionAuthor,
                rawPayload,
                InboundQnaStatus.RECEIVED,
                null,
                null,
                now,
                now);
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static InboundQna reconstitute(
            InboundQnaId id,
            long salesChannelId,
            String externalQnaId,
            QnaType qnaType,
            String questionContent,
            String questionAuthor,
            String rawPayload,
            InboundQnaStatus status,
            Long internalQnaId,
            String failureReason,
            Instant createdAt,
            Instant updatedAt) {
        return new InboundQna(
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

    public void markConverted(Long internalQnaId, Instant now) {
        if (!status.canConvert()) {
            throw new IllegalStateException("현재 상태(" + status + ")에서 CONVERTED로 전이할 수 없습니다");
        }
        this.internalQnaId = internalQnaId;
        this.status = InboundQnaStatus.CONVERTED;
        this.updatedAt = now;
    }

    public void markFailed(String reason, Instant now) {
        if (!status.canConvert()) {
            throw new IllegalStateException("현재 상태(" + status + ")에서 FAILED로 전이할 수 없습니다");
        }
        this.failureReason = reason;
        this.status = InboundQnaStatus.FAILED;
        this.updatedAt = now;
    }

    public Long idValue() {
        return id != null ? id.value() : null;
    }

    public InboundQnaId id() {
        return id;
    }

    public long salesChannelId() {
        return salesChannelId;
    }

    public String externalQnaId() {
        return externalQnaId;
    }

    public QnaType qnaType() {
        return qnaType;
    }

    public String questionContent() {
        return questionContent;
    }

    public String questionAuthor() {
        return questionAuthor;
    }

    public String rawPayload() {
        return rawPayload;
    }

    public InboundQnaStatus status() {
        return status;
    }

    public Long internalQnaId() {
        return internalQnaId;
    }

    public String failureReason() {
        return failureReason;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
