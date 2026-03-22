package com.ryuqq.marketplace.domain.qna.vo;

/**
 * QnA 문의 유형.
 *
 * <p>커머스에서 발생할 수 있는 주요 QnA 카테고리를 정의합니다.
 */
public enum QnaType {
    /** 상품 관련 문의 (사이즈, 소재, 색상, 스펙 등) */
    PRODUCT,

    /** 배송 관련 문의 (배송 일정, 추적, 지연 등) */
    SHIPPING,

    /** 주문 관련 문의 (주문 변경, 결제, 주문 확인 등) */
    ORDER,

    /** 교환 관련 문의 */
    EXCHANGE,

    /** 반품/환불 관련 문의 */
    REFUND,

    /** 재입고 관련 문의 */
    RESTOCK,

    /** 가격/프로모션 관련 문의 (할인, 쿠폰, 적립금 등) */
    PRICE,

    /** 기타 문의 */
    ETC
}
