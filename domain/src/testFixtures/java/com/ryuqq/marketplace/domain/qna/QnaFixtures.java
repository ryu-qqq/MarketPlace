package com.ryuqq.marketplace.domain.qna;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.aggregate.QnaReply;
import com.ryuqq.marketplace.domain.qna.id.QnaId;
import com.ryuqq.marketplace.domain.qna.id.QnaReplyId;
import com.ryuqq.marketplace.domain.qna.vo.QnaReplyType;
import com.ryuqq.marketplace.domain.qna.vo.QnaSource;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import java.util.List;

/** Qna 테스트 Fixtures. */
public final class QnaFixtures {

    private QnaFixtures() {}

    public static final long DEFAULT_SELLER_ID = 1L;
    public static final long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final Long DEFAULT_ORDER_ID = null;
    public static final QnaType DEFAULT_QNA_TYPE = QnaType.PRODUCT;
    public static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final String DEFAULT_EXTERNAL_QNA_ID = "EXT-QNA-001";
    public static final String DEFAULT_QUESTION_TITLE = "사이즈 문의";
    public static final String DEFAULT_QUESTION_CONTENT = "이 상품 사이즈가 어떻게 되나요?";
    public static final String DEFAULT_QUESTION_AUTHOR = "구매자A";
    public static final String DEFAULT_ANSWER_CONTENT = "해당 상품은 Free 사이즈입니다.";
    public static final String DEFAULT_ANSWER_AUTHOR = "판매자A";

    public static QnaSource defaultSource() {
        return new QnaSource(DEFAULT_SALES_CHANNEL_ID, DEFAULT_EXTERNAL_QNA_ID);
    }

    /** 신규 PENDING 상태 Qna */
    public static Qna newQna() {
        return Qna.forNew(
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_ORDER_ID,
                DEFAULT_QNA_TYPE,
                defaultSource(),
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                CommonVoFixtures.now());
    }

    /** PENDING 상태 Qna (영속성 복원) */
    public static Qna pendingQna() {
        return pendingQna(1L);
    }

    public static Qna pendingQna(long id) {
        return Qna.reconstitute(
                QnaId.of(id),
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_ORDER_ID,
                DEFAULT_QNA_TYPE,
                defaultSource(),
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                QnaStatus.PENDING,
                List.of(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** ANSWERED 상태 Qna (영속성 복원) */
    public static Qna answeredQna() {
        return answeredQna(1L);
    }

    public static Qna answeredQna(long id) {
        return Qna.reconstitute(
                QnaId.of(id),
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_ORDER_ID,
                DEFAULT_QNA_TYPE,
                defaultSource(),
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                QnaStatus.ANSWERED,
                List.of(defaultSellerReply()),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }

    /** CLOSED 상태 Qna (영속성 복원) */
    public static Qna closedQna() {
        return Qna.reconstitute(
                QnaId.of(1L),
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_ORDER_ID,
                DEFAULT_QNA_TYPE,
                defaultSource(),
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                QnaStatus.CLOSED,
                List.of(defaultSellerReply()),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }

    /** 주문 문의 PENDING 상태 Qna */
    public static Qna orderQna(long id, long orderId) {
        return Qna.reconstitute(
                QnaId.of(id),
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                orderId,
                QnaType.ORDER,
                defaultSource(),
                "주문 문의",
                "주문 관련 문의 내용",
                DEFAULT_QUESTION_AUTHOR,
                QnaStatus.PENDING,
                List.of(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** 기본 판매자 답변 Reply */
    public static QnaReply defaultSellerReply() {
        return QnaReply.reconstitute(
                QnaReplyId.of(1L),
                null,
                DEFAULT_ANSWER_CONTENT,
                DEFAULT_ANSWER_AUTHOR,
                QnaReplyType.SELLER_ANSWER,
                CommonVoFixtures.now());
    }

    /** 구매자 추가 질문 Reply */
    public static QnaReply defaultBuyerFollowUp() {
        return QnaReply.reconstitute(
                QnaReplyId.of(2L),
                1L,
                "그러면 90kg도 입을 수 있나요?",
                DEFAULT_QUESTION_AUTHOR,
                QnaReplyType.BUYER_FOLLOW_UP,
                CommonVoFixtures.now());
    }
}
