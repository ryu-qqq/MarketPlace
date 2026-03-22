package com.ryuqq.marketplace.adapter.in.rest.internal;

import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.OrderCancelledWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.OrderCancelledWebhookRequest.CancelledItemRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.OrderCreatedWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.OrderCreatedWebhookRequest.OrderCreatedItemRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.PurchaseConfirmedWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.PurchaseConfirmedWebhookRequest.PurchaseConfirmedItemRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.ReturnRequestedWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.ReturnRequestedWebhookRequest.ReturnRequestedItemRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.ReturnWithdrawnWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.ReturnWithdrawnWebhookRequest.ReturnWithdrawnItemRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.response.ClaimSyncWebhookResponse;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.response.OrderCreatedWebhookResponse;
import com.ryuqq.marketplace.application.claimsync.dto.result.ClaimSyncResult;
import com.ryuqq.marketplace.application.inboundorder.dto.result.InboundOrderPollingResult;
import java.time.Instant;
import java.util.List;

/**
 * 내부 웹훅 API 테스트 Fixtures.
 *
 * <p>Internal Webhook REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.2.0
 */
public final class InternalWebhookApiFixtures {

    private InternalWebhookApiFixtures() {}

    // ===== 상수 =====

    public static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final long DEFAULT_SHOP_ID = 10L;
    public static final String DEFAULT_EXTERNAL_ORDER_NO = "EXT-ORDER-001";
    public static final String DEFAULT_EXTERNAL_ORDER_ID = "EXT-ORDER-001";
    public static final String DEFAULT_EXTERNAL_PRODUCT_ORDER_ID = "EXT-PROD-ORDER-001";
    public static final String DEFAULT_EXTERNAL_PRODUCT_ID = "EXT-PROD-001";
    public static final String DEFAULT_EXTERNAL_OPTION_ID = "EXT-OPT-001";
    public static final Instant DEFAULT_ORDERED_AT = Instant.parse("2026-03-21T01:00:00Z");
    public static final Instant DEFAULT_PAID_AT = Instant.parse("2026-03-21T01:01:00Z");

    // ===== OrderCreatedWebhookRequest =====

    public static OrderCreatedWebhookRequest orderCreatedRequest() {
        return new OrderCreatedWebhookRequest(
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_EXTERNAL_ORDER_NO,
                DEFAULT_ORDERED_AT,
                "홍길동",
                "buyer@example.com",
                "010-1234-5678",
                "CARD",
                30000,
                DEFAULT_PAID_AT,
                List.of(orderCreatedItemRequest()));
    }

    public static OrderCreatedItemRequest orderCreatedItemRequest() {
        return new OrderCreatedItemRequest(
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ID,
                DEFAULT_EXTERNAL_OPTION_ID,
                "테스트 상품명",
                "옵션A",
                "https://example.com/image.jpg",
                30000,
                1,
                30000,
                0,
                30000,
                "김수령",
                "010-9999-8888",
                "12345",
                "서울시 강남구",
                "테헤란로 123",
                "문 앞에 놔주세요");
    }

    // ===== OrderCancelledWebhookRequest =====

    public static OrderCancelledWebhookRequest orderCancelledRequest() {
        return new OrderCancelledWebhookRequest(
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_ORDER_ID,
                List.of(cancelledItemRequest()));
    }

    public static CancelledItemRequest cancelledItemRequest() {
        return new CancelledItemRequest(
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                "고객 변심",
                "다른 상품으로 구매 예정",
                1);
    }

    // ===== ReturnRequestedWebhookRequest =====

    public static ReturnRequestedWebhookRequest returnRequestedRequest() {
        return new ReturnRequestedWebhookRequest(
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_ORDER_ID,
                List.of(returnRequestedItemRequest()));
    }

    public static ReturnRequestedItemRequest returnRequestedItemRequest() {
        return new ReturnRequestedItemRequest(
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                "상품 불량",
                "수령 후 파손 확인",
                1,
                "CJ대한통운",
                "1234567890123");
    }

    // ===== ReturnWithdrawnWebhookRequest =====

    public static ReturnWithdrawnWebhookRequest returnWithdrawnRequest() {
        return new ReturnWithdrawnWebhookRequest(
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_ORDER_ID,
                List.of(returnWithdrawnItemRequest()));
    }

    public static ReturnWithdrawnItemRequest returnWithdrawnItemRequest() {
        return new ReturnWithdrawnItemRequest(DEFAULT_EXTERNAL_PRODUCT_ORDER_ID);
    }

    // ===== PurchaseConfirmedWebhookRequest =====

    public static PurchaseConfirmedWebhookRequest purchaseConfirmedRequest() {
        return new PurchaseConfirmedWebhookRequest(
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_ORDER_ID,
                List.of(purchaseConfirmedItemRequest()));
    }

    public static PurchaseConfirmedItemRequest purchaseConfirmedItemRequest() {
        return new PurchaseConfirmedItemRequest(DEFAULT_EXTERNAL_PRODUCT_ORDER_ID);
    }

    // ===== InboundOrderPollingResult (Application) =====

    public static InboundOrderPollingResult pollingResult() {
        return InboundOrderPollingResult.of(1, 1, 0, 0, 0);
    }

    public static InboundOrderPollingResult emptyPollingResult() {
        return InboundOrderPollingResult.empty();
    }

    // ===== ClaimSyncResult (Application) =====

    public static ClaimSyncResult cancelSyncResult() {
        return new ClaimSyncResult(1, 1, 0, 0, 0, 0);
    }

    public static ClaimSyncResult returnSyncResult() {
        return new ClaimSyncResult(1, 0, 1, 0, 0, 0);
    }

    public static ClaimSyncResult emptySyncResult() {
        return ClaimSyncResult.empty();
    }

    // ===== OrderCreatedWebhookResponse =====

    public static OrderCreatedWebhookResponse orderCreatedWebhookResponse() {
        return new OrderCreatedWebhookResponse(1, 1, 0, 0, 0);
    }

    // ===== ClaimSyncWebhookResponse =====

    public static ClaimSyncWebhookResponse cancelSyncWebhookResponse() {
        return new ClaimSyncWebhookResponse(1, 1, 0, 0, 0, 0);
    }

    public static ClaimSyncWebhookResponse returnSyncWebhookResponse() {
        return new ClaimSyncWebhookResponse(1, 0, 1, 0, 0, 0);
    }
}
