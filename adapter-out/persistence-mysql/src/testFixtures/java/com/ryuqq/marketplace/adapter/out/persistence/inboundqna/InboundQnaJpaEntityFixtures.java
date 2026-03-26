package com.ryuqq.marketplace.adapter.out.persistence.inboundqna;

import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.entity.InboundQnaJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * InboundQnaJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 InboundQnaJpaEntity 관련 객체들을 생성합니다.
 */
public final class InboundQnaJpaEntityFixtures {

    private InboundQnaJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final String DEFAULT_EXTERNAL_QNA_ID = "EXT-QNA-001";
    public static final String DEFAULT_QNA_TYPE = "PRODUCT";
    public static final String DEFAULT_QUESTION_CONTENT = "이 상품 사이즈가 어떻게 되나요?";
    public static final String DEFAULT_QUESTION_AUTHOR = "구매자A";
    public static final String DEFAULT_RAW_PAYLOAD = "{\"externalQnaId\":\"EXT-QNA-001\"}";
    public static final Long DEFAULT_INTERNAL_QNA_ID = 100L;
    public static final String DEFAULT_FAILURE_REASON = "매핑 실패: 상품을 찾을 수 없습니다";

    // ===== RECEIVED 상태 Entity =====

    /** RECEIVED 상태의 신규 수신 Entity 생성 (ID null). */
    public static InboundQnaJpaEntity receivedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundQnaJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                "EXT-QNA-" + seq,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                DEFAULT_RAW_PAYLOAD,
                InboundQnaJpaEntity.Status.RECEIVED,
                null,
                null,
                now,
                now);
    }

    /** ID를 지정한 RECEIVED 상태 Entity 생성. */
    public static InboundQnaJpaEntity receivedEntity(Long id) {
        Instant now = Instant.now();
        return InboundQnaJpaEntity.create(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                DEFAULT_RAW_PAYLOAD,
                InboundQnaJpaEntity.Status.RECEIVED,
                null,
                null,
                now,
                now);
    }

    /** salesChannelId와 externalQnaId를 지정한 RECEIVED 상태 Entity 생성. */
    public static InboundQnaJpaEntity receivedEntity(long salesChannelId, String externalQnaId) {
        Instant now = Instant.now();
        return InboundQnaJpaEntity.create(
                null,
                salesChannelId,
                externalQnaId,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                DEFAULT_RAW_PAYLOAD,
                InboundQnaJpaEntity.Status.RECEIVED,
                null,
                null,
                now,
                now);
    }

    // ===== CONVERTED 상태 Entity =====

    /** CONVERTED 상태 Entity 생성 (internalQnaId 포함). */
    public static InboundQnaJpaEntity convertedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundQnaJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                "EXT-QNA-" + seq,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                DEFAULT_RAW_PAYLOAD,
                InboundQnaJpaEntity.Status.CONVERTED,
                DEFAULT_INTERNAL_QNA_ID,
                null,
                now,
                now);
    }

    /** ID를 지정한 CONVERTED 상태 Entity 생성. */
    public static InboundQnaJpaEntity convertedEntity(Long id) {
        Instant now = Instant.now();
        return InboundQnaJpaEntity.create(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                DEFAULT_RAW_PAYLOAD,
                InboundQnaJpaEntity.Status.CONVERTED,
                DEFAULT_INTERNAL_QNA_ID,
                null,
                now,
                now);
    }

    // ===== FAILED 상태 Entity =====

    /** FAILED 상태 Entity 생성 (failureReason 포함). */
    public static InboundQnaJpaEntity failedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundQnaJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                "EXT-QNA-" + seq,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                DEFAULT_RAW_PAYLOAD,
                InboundQnaJpaEntity.Status.FAILED,
                null,
                DEFAULT_FAILURE_REASON,
                now,
                now);
    }

    /** ID를 지정한 FAILED 상태 Entity 생성. */
    public static InboundQnaJpaEntity failedEntity(Long id) {
        Instant now = Instant.now();
        return InboundQnaJpaEntity.create(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                DEFAULT_RAW_PAYLOAD,
                InboundQnaJpaEntity.Status.FAILED,
                null,
                DEFAULT_FAILURE_REASON,
                now,
                now);
    }

    // ===== 단위 테스트용 기본 Entity =====

    /** 단위 테스트용 기본 Entity (DEFAULT_ID 사용). */
    public static InboundQnaJpaEntity entity() {
        Instant now = Instant.now();
        return InboundQnaJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                DEFAULT_RAW_PAYLOAD,
                InboundQnaJpaEntity.Status.RECEIVED,
                null,
                null,
                now,
                now);
    }

    /** ID를 지정한 단위 테스트용 Entity. */
    public static InboundQnaJpaEntity entity(Long id) {
        Instant now = Instant.now();
        return InboundQnaJpaEntity.create(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                DEFAULT_RAW_PAYLOAD,
                InboundQnaJpaEntity.Status.RECEIVED,
                null,
                null,
                now,
                now);
    }
}
