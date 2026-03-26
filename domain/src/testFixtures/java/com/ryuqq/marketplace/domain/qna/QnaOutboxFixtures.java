package com.ryuqq.marketplace.domain.qna;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.qna.id.QnaId;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import com.ryuqq.marketplace.domain.qna.outbox.id.QnaOutboxId;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxStatus;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxType;
import java.time.Instant;

/**
 * QnaOutbox 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 QnaOutbox 관련 객체들을 생성합니다.
 *
 * <p>참조 모델: RefundOutboxFixtures, ExchangeOutboxFixtures
 */
public final class QnaOutboxFixtures {

    private QnaOutboxFixtures() {}

    // ===== 기본 상수 =====
    public static final long DEFAULT_QNA_ID = 1L;
    public static final long DEFAULT_OUTBOX_ID = 1L;
    public static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final String DEFAULT_EXTERNAL_QNA_ID = "EXT-QNA-001";
    public static final String DEFAULT_PAYLOAD = "{\"qnaId\":1,\"answer\":\"Free 사이즈입니다.\"}";

    // ===== ID Fixtures =====

    public static QnaOutboxId defaultQnaOutboxId() {
        return QnaOutboxId.of(DEFAULT_OUTBOX_ID);
    }

    public static QnaOutboxId newQnaOutboxId() {
        return QnaOutboxId.forNew();
    }

    public static QnaId defaultQnaId() {
        return QnaId.of(DEFAULT_QNA_ID);
    }

    // ===== IdempotencyKey Fixtures =====

    public static QnaOutboxIdempotencyKey defaultIdempotencyKey() {
        return QnaOutboxIdempotencyKey.generate(
                DEFAULT_QNA_ID,
                QnaOutboxType.ANSWER,
                Instant.ofEpochMilli(1700000000000L));
    }

    // ===== forNew Fixtures =====

    public static QnaOutbox newQnaOutbox() {
        return QnaOutbox.forNew(
                defaultQnaId(),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                QnaOutboxType.ANSWER,
                DEFAULT_PAYLOAD,
                CommonVoFixtures.now());
    }

    public static QnaOutbox newQnaOutbox(QnaId qnaId) {
        return QnaOutbox.forNew(
                qnaId,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                QnaOutboxType.ANSWER,
                DEFAULT_PAYLOAD,
                CommonVoFixtures.now());
    }

    // ===== reconstitute - 상태별 =====

    public static QnaOutbox pendingQnaOutbox() {
        Instant now = CommonVoFixtures.now();
        return QnaOutbox.reconstitute(
                defaultQnaOutboxId(),
                defaultQnaId(),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                QnaOutboxType.ANSWER,
                QnaOutboxStatus.PENDING,
                DEFAULT_PAYLOAD,
                0,
                3,
                now,
                now,
                null,
                null,
                0L,
                defaultIdempotencyKey().value());
    }

    public static QnaOutbox processingQnaOutbox() {
        Instant now = CommonVoFixtures.now();
        return QnaOutbox.reconstitute(
                defaultQnaOutboxId(),
                defaultQnaId(),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                QnaOutboxType.ANSWER,
                QnaOutboxStatus.PROCESSING,
                DEFAULT_PAYLOAD,
                0,
                3,
                now,
                now,
                null,
                null,
                0L,
                defaultIdempotencyKey().value());
    }

    public static QnaOutbox completedQnaOutbox() {
        Instant now = CommonVoFixtures.now();
        return QnaOutbox.reconstitute(
                defaultQnaOutboxId(),
                defaultQnaId(),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                QnaOutboxType.ANSWER,
                QnaOutboxStatus.COMPLETED,
                DEFAULT_PAYLOAD,
                0,
                3,
                now,
                now,
                now,
                null,
                1L,
                defaultIdempotencyKey().value());
    }

    public static QnaOutbox failedQnaOutbox() {
        Instant now = CommonVoFixtures.now();
        return QnaOutbox.reconstitute(
                defaultQnaOutboxId(),
                defaultQnaId(),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                QnaOutboxType.ANSWER,
                QnaOutboxStatus.FAILED,
                DEFAULT_PAYLOAD,
                3,
                3,
                now,
                now,
                now,
                "외부 API 호출 실패",
                1L,
                defaultIdempotencyKey().value());
    }

    public static QnaOutbox processingQnaOutboxWithRetry(int retryCount) {
        Instant now = CommonVoFixtures.now();
        return QnaOutbox.reconstitute(
                defaultQnaOutboxId(),
                defaultQnaId(),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                QnaOutboxType.ANSWER,
                QnaOutboxStatus.PROCESSING,
                DEFAULT_PAYLOAD,
                retryCount,
                3,
                now,
                now,
                null,
                null,
                0L,
                defaultIdempotencyKey().value());
    }
}
