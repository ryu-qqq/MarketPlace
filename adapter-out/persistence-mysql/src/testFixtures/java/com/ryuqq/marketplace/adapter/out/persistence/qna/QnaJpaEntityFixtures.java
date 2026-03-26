package com.ryuqq.marketplace.adapter.out.persistence.qna;

import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaReplyJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * QnaJpaEntity / QnaReplyJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 QnaJpaEntity 관련 객체들을 생성합니다.
 */
public final class QnaJpaEntityFixtures {

    private QnaJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final long DEFAULT_SELLER_ID = 1L;
    public static final long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final String DEFAULT_QNA_TYPE = "PRODUCT";
    public static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final String DEFAULT_EXTERNAL_QNA_ID = "EXT-QNA-001";
    public static final String DEFAULT_QUESTION_TITLE = "문의 제목";
    public static final String DEFAULT_QUESTION_CONTENT = "이 상품 사이즈가 어떻게 되나요?";
    public static final String DEFAULT_QUESTION_AUTHOR = "구매자A";
    public static final String DEFAULT_ANSWER_CONTENT = "해당 상품은 Free 사이즈입니다.";
    public static final String DEFAULT_ANSWER_AUTHOR = "판매자A";
    public static final String DEFAULT_REPLY_TYPE = "SELLER_ANSWER";

    // ===== QnaJpaEntity Fixtures =====

    /** PENDING 상태의 신규 Qna Entity (ID null). */
    public static QnaJpaEntity pendingEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return QnaJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                null,
                DEFAULT_QNA_TYPE,
                DEFAULT_SALES_CHANNEL_ID,
                "EXT-QNA-" + seq,
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                QnaJpaEntity.Status.PENDING,
                now,
                now);
    }

    /** ID를 지정한 PENDING 상태 Entity. */
    public static QnaJpaEntity pendingEntity(Long id) {
        Instant now = Instant.now();
        return QnaJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                null,
                DEFAULT_QNA_TYPE,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                QnaJpaEntity.Status.PENDING,
                now,
                now);
    }

    /** ANSWERED 상태 Entity (ID null). */
    public static QnaJpaEntity answeredEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return QnaJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                null,
                DEFAULT_QNA_TYPE,
                DEFAULT_SALES_CHANNEL_ID,
                "EXT-QNA-" + seq,
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                QnaJpaEntity.Status.ANSWERED,
                now.minusSeconds(3600),
                now);
    }

    /** ID를 지정한 ANSWERED 상태 Entity. */
    public static QnaJpaEntity answeredEntity(Long id) {
        Instant now = Instant.now();
        return QnaJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                null,
                DEFAULT_QNA_TYPE,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                QnaJpaEntity.Status.ANSWERED,
                now.minusSeconds(3600),
                now);
    }

    /** CLOSED 상태 Entity. */
    public static QnaJpaEntity closedEntity(Long id) {
        Instant now = Instant.now();
        return QnaJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                null,
                DEFAULT_QNA_TYPE,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                QnaJpaEntity.Status.CLOSED,
                now.minusSeconds(7200),
                now);
    }

    /** 단위 테스트용 기본 Entity (DEFAULT_ID 사용). */
    public static QnaJpaEntity entity() {
        Instant now = Instant.now();
        return QnaJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                null,
                DEFAULT_QNA_TYPE,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                QnaJpaEntity.Status.PENDING,
                now,
                now);
    }

    /** ID를 지정한 단위 테스트용 Entity. */
    public static QnaJpaEntity entity(Long id) {
        Instant now = Instant.now();
        return QnaJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                null,
                DEFAULT_QNA_TYPE,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                QnaJpaEntity.Status.PENDING,
                now,
                now);
    }

    // ===== QnaReplyJpaEntity Fixtures =====

    /** 기본 판매자 답변 Reply Entity 생성. */
    public static QnaReplyJpaEntity sellerReplyEntity(long qnaId) {
        return QnaReplyJpaEntity.create(
                1L,
                qnaId,
                null,
                DEFAULT_ANSWER_CONTENT,
                DEFAULT_ANSWER_AUTHOR,
                DEFAULT_REPLY_TYPE,
                Instant.now());
    }

    /** ID를 지정한 판매자 답변 Reply Entity 생성. */
    public static QnaReplyJpaEntity sellerReplyEntity(Long id, long qnaId) {
        return QnaReplyJpaEntity.create(
                id,
                qnaId,
                null,
                DEFAULT_ANSWER_CONTENT,
                DEFAULT_ANSWER_AUTHOR,
                DEFAULT_REPLY_TYPE,
                Instant.now());
    }

    /** 구매자 추가 질문 Reply Entity 생성. */
    public static QnaReplyJpaEntity buyerFollowUpEntity(Long id, long qnaId, Long parentReplyId) {
        return QnaReplyJpaEntity.create(
                id,
                qnaId,
                parentReplyId,
                "그러면 90kg도 입을 수 있나요?",
                DEFAULT_QUESTION_AUTHOR,
                "BUYER_FOLLOW_UP",
                Instant.now());
    }
}
