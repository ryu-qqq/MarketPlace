package com.ryuqq.marketplace.application.qna;

import com.ryuqq.marketplace.application.qna.dto.query.QnaSearchCondition;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;

/**
 * Qna Application Query 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class QnaQueryFixtures {

    private QnaQueryFixtures() {}

    // ===== 기본 상수 =====
    private static final long DEFAULT_SELLER_ID = 1L;
    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LIMIT = 20;
    private static final int DEFAULT_SIZE = 20;

    // ===== QnaSearchCondition =====

    public static QnaSearchCondition searchCondition() {
        return new QnaSearchCondition(
                DEFAULT_SELLER_ID,
                QnaStatus.PENDING,
                null,
                null,
                null,
                null,
                null,
                DEFAULT_SIZE);
    }

    public static QnaSearchCondition searchCondition(Long sellerId, QnaStatus status) {
        return new QnaSearchCondition(
                sellerId,
                status,
                null,
                null,
                null,
                null,
                null,
                DEFAULT_SIZE);
    }

    public static QnaSearchCondition searchCondition(Long sellerId, QnaStatus status, int size) {
        return new QnaSearchCondition(
                sellerId,
                status,
                null,
                null,
                null,
                null,
                null,
                size);
    }

    public static QnaSearchCondition searchConditionWithType(QnaType qnaType) {
        return new QnaSearchCondition(
                DEFAULT_SELLER_ID,
                null,
                qnaType,
                null,
                null,
                null,
                null,
                DEFAULT_SIZE);
    }

    public static QnaSearchCondition searchConditionWithKeyword(String keyword) {
        return new QnaSearchCondition(
                DEFAULT_SELLER_ID,
                null,
                null,
                keyword,
                null,
                null,
                null,
                DEFAULT_SIZE);
    }

    public static QnaSearchCondition searchConditionWithCursor(Long cursorId) {
        return new QnaSearchCondition(
                DEFAULT_SELLER_ID,
                QnaStatus.PENDING,
                null,
                null,
                null,
                null,
                cursorId,
                DEFAULT_SIZE);
    }

    public static QnaSearchCondition emptyCondition() {
        return new QnaSearchCondition(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                DEFAULT_SIZE);
    }
}
