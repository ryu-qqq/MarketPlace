package com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox;

import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.entity.QnaOutboxJpaEntity;
import java.time.Instant;

/**
 * QnaOutboxJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 QnaOutboxJpaEntity 관련 객체들을 생성합니다.
 */
public final class QnaOutboxJpaEntityFixtures {

    private QnaOutboxJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final long DEFAULT_QNA_ID = 1L;
    public static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final String DEFAULT_EXTERNAL_QNA_ID = "EXT-QNA-001";
    public static final String DEFAULT_OUTBOX_TYPE = "ANSWER";
    public static final String DEFAULT_PAYLOAD = "{\"qnaId\":1,\"externalQnaId\":\"EXT-QNA-001\"}";
    public static final int DEFAULT_RETRY_COUNT = 0;
    public static final int DEFAULT_MAX_RETRY = 3;
    public static final String DEFAULT_IDEMPOTENCY_KEY =
            "QNBO:1:ANSWER:1700000000000";

    // ===== PENDING 상태 Entity =====

    /** PENDING 상태의 QnaOutbox Entity 생성 (기본 ID). */
    public static QnaOutboxJpaEntity pendingEntity() {
        Instant now = Instant.now();
        return QnaOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_QNA_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_OUTBOX_TYPE,
                QnaOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now.minusSeconds(60),
                now.minusSeconds(60),
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    /** PENDING 상태 Entity 생성 (ID 지정). */
    public static QnaOutboxJpaEntity pendingEntity(Long id) {
        Instant now = Instant.now();
        return QnaOutboxJpaEntity.create(
                id,
                DEFAULT_QNA_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_OUTBOX_TYPE,
                QnaOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now.minusSeconds(60),
                now.minusSeconds(60),
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY + "_" + id);
    }

    /** 특정 createdAt 시간을 가진 PENDING Entity 생성. */
    public static QnaOutboxJpaEntity pendingEntityCreatedAt(Long id, Instant createdAt) {
        return QnaOutboxJpaEntity.create(
                id,
                DEFAULT_QNA_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_OUTBOX_TYPE,
                QnaOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                createdAt,
                createdAt,
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY + "_" + id);
    }

    // ===== PROCESSING 상태 Entity =====

    /** PROCESSING 상태의 QnaOutbox Entity 생성. */
    public static QnaOutboxJpaEntity processingEntity() {
        Instant now = Instant.now();
        Instant past = now.minusSeconds(300);
        return QnaOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_QNA_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_OUTBOX_TYPE,
                QnaOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                past,
                past,
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    /** 특정 updatedAt 시간을 가진 PROCESSING Entity 생성 (타임아웃 테스트용). */
    public static QnaOutboxJpaEntity processingEntityUpdatedAt(Long id, Instant updatedAt) {
        Instant now = Instant.now();
        return QnaOutboxJpaEntity.create(
                id,
                DEFAULT_QNA_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_OUTBOX_TYPE,
                QnaOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now.minusSeconds(600),
                updatedAt,
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY + "_" + id);
    }

    // ===== COMPLETED 상태 Entity =====

    /** COMPLETED 상태의 QnaOutbox Entity 생성. */
    public static QnaOutboxJpaEntity completedEntity() {
        Instant now = Instant.now();
        Instant past = now.minusSeconds(600);
        return QnaOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_QNA_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_OUTBOX_TYPE,
                QnaOutboxJpaEntity.Status.COMPLETED,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                past,
                now,
                now,
                null,
                1L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    // ===== FAILED 상태 Entity =====

    /** FAILED 상태의 QnaOutbox Entity 생성 (최대 재시도 초과). */
    public static QnaOutboxJpaEntity failedEntity() {
        Instant now = Instant.now();
        Instant past = now.minusSeconds(3600);
        return QnaOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_QNA_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_OUTBOX_TYPE,
                QnaOutboxJpaEntity.Status.FAILED,
                DEFAULT_PAYLOAD,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                past,
                now,
                now,
                "외부 API 호출 실패",
                1L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    // ===== null ID (신규 저장용) Entity =====

    /** null ID를 가진 PENDING Entity 생성 (신규 저장 시뮬레이션). */
    public static QnaOutboxJpaEntity newPendingEntity() {
        Instant now = Instant.now();
        return QnaOutboxJpaEntity.create(
                null,
                DEFAULT_QNA_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_OUTBOX_TYPE,
                QnaOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY + "_new");
    }
}
