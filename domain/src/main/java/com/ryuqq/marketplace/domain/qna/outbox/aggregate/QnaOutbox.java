package com.ryuqq.marketplace.domain.qna.outbox.aggregate;

import com.ryuqq.marketplace.domain.qna.id.QnaId;
import com.ryuqq.marketplace.domain.qna.outbox.id.QnaOutboxId;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxStatus;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxType;
import java.time.Instant;

/**
 * QnA 아웃박스 Aggregate.
 *
 * <p>QnA 답변 시 외부 판매채널에 동기화하기 위한 Outbox 패턴 구현체입니다.
 */
public class QnaOutbox {

    private static final int DEFAULT_MAX_RETRY = 3;

    private final QnaOutboxId id;
    private final QnaId qnaId;
    private final long salesChannelId;
    private final String externalQnaId;
    private final QnaOutboxType outboxType;
    private QnaOutboxStatus status;
    private final String payload;
    private int retryCount;
    private final int maxRetry;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant processedAt;
    private String errorMessage;
    private long version;
    private final QnaOutboxIdempotencyKey idempotencyKey;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private QnaOutbox(
            QnaOutboxId id,
            QnaId qnaId,
            long salesChannelId,
            String externalQnaId,
            QnaOutboxType outboxType,
            QnaOutboxStatus status,
            String payload,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            QnaOutboxIdempotencyKey idempotencyKey) {
        this.id = id;
        this.qnaId = qnaId;
        this.salesChannelId = salesChannelId;
        this.externalQnaId = externalQnaId;
        this.outboxType = outboxType;
        this.status = status;
        this.payload = payload;
        this.retryCount = retryCount;
        this.maxRetry = maxRetry;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.processedAt = processedAt;
        this.errorMessage = errorMessage;
        this.version = version;
        this.idempotencyKey = idempotencyKey;
    }

    public static QnaOutbox forNew(
            QnaId qnaId,
            long salesChannelId,
            String externalQnaId,
            QnaOutboxType outboxType,
            String payload,
            Instant now) {
        QnaOutboxIdempotencyKey idempotencyKey =
                QnaOutboxIdempotencyKey.generate(qnaId.value(), outboxType, now);
        return new QnaOutbox(
                QnaOutboxId.forNew(), qnaId, salesChannelId, externalQnaId,
                outboxType, QnaOutboxStatus.PENDING, payload,
                0, DEFAULT_MAX_RETRY, now, now, null, null, 0L, idempotencyKey);
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static QnaOutbox reconstitute(
            QnaOutboxId id,
            QnaId qnaId,
            long salesChannelId,
            String externalQnaId,
            QnaOutboxType outboxType,
            QnaOutboxStatus status,
            String payload,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        return new QnaOutbox(
                id, qnaId, salesChannelId, externalQnaId,
                outboxType, status, payload,
                retryCount, maxRetry, createdAt, updatedAt, processedAt,
                errorMessage, version, QnaOutboxIdempotencyKey.of(idempotencyKey));
    }

    public boolean isNew() { return id.isNew(); }

    /** PENDING → PROCESSING. */
    public void startProcessing(Instant now) {
        if (!status.isPending()) {
            throw new IllegalStateException("PENDING 상태에서만 처리를 시작할 수 있습니다. 현재 상태: " + status);
        }
        this.status = QnaOutboxStatus.PROCESSING;
        this.updatedAt = now;
    }

    /** PROCESSING → COMPLETED. */
    public void complete(Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException("PROCESSING 상태에서만 완료할 수 있습니다. 현재 상태: " + status);
        }
        this.status = QnaOutboxStatus.COMPLETED;
        this.processedAt = now;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    /** 처리 실패 및 재시도. 최대 재시도 초과 시 FAILED. */
    public void failAndRetry(String errorMessage, Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException("PROCESSING 상태에서만 실패 처리할 수 있습니다. 현재 상태: " + status);
        }
        this.retryCount++;
        this.errorMessage = errorMessage;
        this.updatedAt = now;
        if (this.retryCount >= this.maxRetry) {
            this.status = QnaOutboxStatus.FAILED;
            this.processedAt = now;
        } else {
            this.status = QnaOutboxStatus.PENDING;
        }
    }

    /** 즉시 실패 처리 (재시도 없이). */
    public void fail(String errorMessage, Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException("PROCESSING 상태에서만 실패 처리할 수 있습니다. 현재 상태: " + status);
        }
        this.status = QnaOutboxStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = now;
        this.updatedAt = now;
    }

    /** 외부 API 실패 결과를 반영합니다. */
    public void recordFailure(boolean canRetry, String errorMessage, Instant now) {
        if (canRetry) { failAndRetry(errorMessage, now); }
        else { fail(errorMessage, now); }
    }

    /** PROCESSING 타임아웃 복구. */
    public void recoverFromTimeout(Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException("타임아웃 복구는 PROCESSING 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.status = QnaOutboxStatus.PENDING;
        this.updatedAt = now;
        this.errorMessage = "타임아웃으로 인한 복구";
    }

    /** FAILED → PENDING 수동 재처리. */
    public void retry(Instant now) {
        if (!status.isFailed()) {
            throw new IllegalStateException("FAILED 상태에서만 재처리할 수 있습니다. 현재 상태: " + status);
        }
        this.status = QnaOutboxStatus.PENDING;
        this.retryCount = 0;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    public void refreshVersion(long version) { this.version = version; }

    public QnaOutboxId id() { return id; }
    public Long idValue() { return id.value(); }
    public QnaId qnaId() { return qnaId; }
    public Long qnaIdValue() { return qnaId.value(); }
    public long salesChannelId() { return salesChannelId; }
    public String externalQnaId() { return externalQnaId; }
    public QnaOutboxType outboxType() { return outboxType; }
    public QnaOutboxStatus status() { return status; }
    public String payload() { return payload; }
    public int retryCount() { return retryCount; }
    public int maxRetry() { return maxRetry; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Instant processedAt() { return processedAt; }
    public String errorMessage() { return errorMessage; }
    public long version() { return version; }
    public QnaOutboxIdempotencyKey idempotencyKey() { return idempotencyKey; }
    public String idempotencyKeyValue() { return idempotencyKey.value(); }
    public boolean isPending() { return status.isPending(); }
    public boolean isProcessing() { return status.isProcessing(); }
}
