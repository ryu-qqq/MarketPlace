package com.ryuqq.marketplace.adapter.in.rest.internal;

/**
 * InternalWebhookEndpoints - 내부 웹훅 API 엔드포인트 상수.
 *
 * <p>자사몰 → MarketPlace 내부 VPC 통신용. 인증 없이 접근 가능.
 *
 * <p>API-END-001: Endpoints final class + private 생성자.
 *
 * <p>API-END-002: static final 상수.
 *
 * @author ryu-qqq
 * @since 1.2.0
 */
public final class InternalWebhookEndpoints {

    private InternalWebhookEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /** 기본 경로 */
    public static final String BASE = "/api/v1/market/internal/webhooks/orders";

    /** 주문 생성 (결제 완료) */
    public static final String CREATED = BASE + "/created";

    /** 즉시 취소 (판매자 확인 전) */
    public static final String CANCELLED = BASE + "/cancelled";

    /** 반품 요청 (배송 완료 후) */
    public static final String RETURN_REQUESTED = BASE + "/return-requested";

    /** 반품 철회 (구매자 요청 취소) */
    public static final String RETURN_WITHDRAWN = BASE + "/return-withdrawn";

    /** 구매 확정 (배송 완료 후 자동/수동) */
    public static final String PURCHASE_CONFIRMED = BASE + "/purchase-confirmed";

    /** QnA 기본 경로 */
    public static final String QNA_BASE = "/api/v1/market/internal/webhooks/qnas";

    /** QnA 수신 */
    public static final String QNA_RECEIVED = QNA_BASE + "/received";

    /** QnA 수정 */
    public static final String QNA_UPDATED = QNA_BASE + "/updated";
}
