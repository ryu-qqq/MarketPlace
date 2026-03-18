package com.ryuqq.marketplace.adapter.in.rest.order;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.query.SearchOrdersApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse.CancelInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse.ClaimInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse.SettlementApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse.TimeLineApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.CancelSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.CancelSummaryApiResponse.LatestCancelApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.ClaimSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.ClaimSummaryApiResponse.LatestClaimApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.DeliveryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.OrderInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.PaymentInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.ProductOrderApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.ReceiverApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponseV4;
import com.ryuqq.marketplace.application.order.dto.response.OrderCancelResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderClaimResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.CancelSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.CancelSummary.LatestCancel;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ClaimSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ClaimSummary.LatestClaim;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.DeliveryInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.OrderInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.PaymentInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ProductOrderInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ReceiverInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderPageResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Order API 테스트 Fixtures.
 *
 * <p>Order REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class OrderApiFixtures {

    private OrderApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000001";
    public static final String DEFAULT_ORDER_ID = "01900000-0000-7000-0000-000000000001";
    public static final String DEFAULT_ORDER_NUMBER = "ORD-20260101-0001";
    public static final String DEFAULT_ORDER_STATUS = "PREPARING";
    public static final String DEFAULT_PAYMENT_ID = "01900000-0000-7000-0000-000000000002";
    public static final String DEFAULT_PAYMENT_NUMBER = "PAY-20260101-0001";
    public static final String DEFAULT_PAYMENT_STATUS = "PAID";
    public static final String DEFAULT_PAYMENT_METHOD = "CARD";
    public static final String DEFAULT_PAYMENT_AGENCY_ID = "TID_20260101_001";
    public static final int DEFAULT_PAYMENT_AMOUNT = 50000;
    public static final String DEFAULT_SHOP_CODE = "SHOP_001";
    public static final String DEFAULT_SHOP_NAME = "테스트 샵";
    public static final String DEFAULT_BUYER_NAME = "홍길동";
    public static final String DEFAULT_BUYER_EMAIL = "buyer@example.com";
    public static final String DEFAULT_BUYER_PHONE = "010-1234-5678";
    public static final String DEFAULT_RECEIVER_NAME = "이순신";
    public static final String DEFAULT_RECEIVER_PHONE = "010-9876-5432";
    public static final String DEFAULT_RECEIVER_ZIPCODE = "12345";
    public static final String DEFAULT_RECEIVER_ADDRESS = "서울시 강남구";
    public static final String DEFAULT_RECEIVER_ADDRESS_DETAIL = "테헤란로 123";
    public static final String DEFAULT_DELIVERY_REQUEST = "부재 시 경비실 맡겨주세요";
    public static final String DEFAULT_DELIVERY_STATUS = "PREPARING";
    public static final String DEFAULT_SKU_CODE = "SKU-20260101-001";
    public static final String DEFAULT_PRODUCT_GROUP_NAME = "테스트 상품";
    public static final String DEFAULT_BRAND_NAME = "테스트 브랜드";
    public static final String DEFAULT_SELLER_NAME = "테스트 셀러";
    public static final String DEFAULT_MAIN_IMAGE_URL = "https://example.com/image.jpg";
    public static final String DEFAULT_CANCEL_ID = "01900000-0000-7000-0000-000000000003";
    public static final String DEFAULT_CANCEL_NUMBER = "CAN-20260101-0001";
    public static final String DEFAULT_CLAIM_ID = "01900000-0000-7000-0000-000000000004";
    public static final String DEFAULT_CLAIM_NUMBER = "CLM-20260101-0001";

    // ===== SearchOrdersApiRequest =====

    public static SearchOrdersApiRequest searchRequest() {
        return new SearchOrdersApiRequest(null, null, null, null, null, null, null, null, 0, 20);
    }

    public static SearchOrdersApiRequest searchRequest(
            String status, String searchField, String searchWord, int page, int size) {
        return new SearchOrdersApiRequest(
                "ORDERED",
                null,
                null,
                List.of(status),
                searchField,
                searchWord,
                "CREATED_AT",
                "DESC",
                page,
                size);
    }

    // ===== Application Result Fixtures (ProductOrderListResult) =====

    public static OrderInfo orderInfo() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        return new OrderInfo(
                DEFAULT_ORDER_ID,
                DEFAULT_ORDER_NUMBER,
                1L,
                10L,
                DEFAULT_SHOP_CODE,
                DEFAULT_SHOP_NAME,
                "EXT-ORDER-001",
                now,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                now,
                now);
    }

    public static ProductOrderInfo productOrderInfo() {
        return new ProductOrderInfo(
                DEFAULT_ORDER_ITEM_ID,
                "ORD-20250115-0001-001",
                100L,
                200L,
                1L,
                50L,
                DEFAULT_SKU_CODE,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_BRAND_NAME,
                DEFAULT_SELLER_NAME,
                DEFAULT_MAIN_IMAGE_URL,
                "EXT-PRODUCT-001",
                "EXT-OPTION-001",
                "외부 상품명",
                "외부 옵션명",
                "https://external.com/image.jpg",
                50000,
                1,
                50000,
                0,
                50000);
    }

    public static PaymentInfo paymentInfo() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        return new PaymentInfo(
                DEFAULT_PAYMENT_ID,
                DEFAULT_PAYMENT_NUMBER,
                DEFAULT_PAYMENT_STATUS,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_PAYMENT_AGENCY_ID,
                DEFAULT_PAYMENT_AMOUNT,
                now,
                null);
    }

    public static ReceiverInfo receiverInfo() {
        return new ReceiverInfo(
                DEFAULT_RECEIVER_NAME,
                DEFAULT_RECEIVER_PHONE,
                DEFAULT_RECEIVER_ZIPCODE,
                DEFAULT_RECEIVER_ADDRESS,
                DEFAULT_RECEIVER_ADDRESS_DETAIL,
                DEFAULT_DELIVERY_REQUEST);
    }

    public static DeliveryInfo deliveryInfo() {
        return new DeliveryInfo(DEFAULT_DELIVERY_STATUS, null, null, null);
    }

    public static CancelSummary cancelSummaryNone() {
        return CancelSummary.none(1);
    }

    public static CancelSummary cancelSummaryWithLatest() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        LatestCancel latest =
                new LatestCancel(DEFAULT_CANCEL_ID, DEFAULT_CANCEL_NUMBER, "COMPLETED", 1, now);
        return new CancelSummary(false, 1, 0, latest);
    }

    public static ClaimSummary claimSummaryNone() {
        return ClaimSummary.none(1);
    }

    public static ClaimSummary claimSummaryWithLatest() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        LatestClaim latest =
                new LatestClaim(
                        DEFAULT_CLAIM_ID, DEFAULT_CLAIM_NUMBER, "REFUND", "IN_PROGRESS", 1, now);
        return new ClaimSummary(true, 1, 1, 0, latest);
    }

    public static ProductOrderListResult productOrderListResult() {
        return new ProductOrderListResult(
                orderInfo(),
                productOrderInfo(),
                paymentInfo(),
                receiverInfo(),
                deliveryInfo(),
                cancelSummaryNone(),
                claimSummaryNone());
    }

    public static List<ProductOrderListResult> productOrderListResults(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> productOrderListResult()).toList();
    }

    public static ProductOrderPageResult productOrderPageResult(int count, int page, int size) {
        List<ProductOrderListResult> results = productOrderListResults(count);
        PageMeta pageMeta = PageMeta.of(page, size, count);
        return new ProductOrderPageResult(results, pageMeta);
    }

    public static ProductOrderPageResult emptyPageResult() {
        PageMeta pageMeta = PageMeta.of(0, 20, 0);
        return new ProductOrderPageResult(List.of(), pageMeta);
    }

    // ===== Application Result Fixtures (ProductOrderDetailResult) =====

    public static OrderCancelResult cancelResult() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        return new OrderCancelResult(
                3001L,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_CANCEL_NUMBER,
                "COMPLETED",
                1,
                "CHANGE_MIND",
                "단순 변심",
                50000,
                50000,
                "CARD",
                now,
                now,
                now);
    }

    public static OrderClaimResult claimResult() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        return new OrderClaimResult(
                4001L,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_CLAIM_NUMBER,
                "REFUND",
                "IN_PROGRESS",
                1,
                "DEFECTIVE",
                "상품 불량",
                "COLLECT",
                50000,
                0,
                null,
                50000,
                "CARD",
                null,
                now,
                null,
                null);
    }

    public static OrderHistoryResult historyResult() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        return new OrderHistoryResult(5001L, "ORDERED", "PREPARING", "SYSTEM", "발주 확인", now);
    }

    public static ProductOrderDetailResult.SettlementInfo settlementInfo() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        return new ProductOrderDetailResult.SettlementInfo(10, 5000, 45000, 0, 100, now, null);
    }

    public static ProductOrderDetailResult productOrderDetailResult() {
        return new ProductOrderDetailResult(
                orderInfo(),
                productOrderInfo(),
                paymentInfo(),
                receiverInfo(),
                deliveryInfo(),
                cancelSummaryNone(),
                claimSummaryNone(),
                settlementInfo(),
                List.of(cancelResult()),
                List.of(claimResult()),
                List.of(historyResult()));
    }

    // ===== API Response Fixtures =====

    public static PaymentInfoApiResponse paymentInfoApiResponse() {
        return new PaymentInfoApiResponse(
                DEFAULT_PAYMENT_ID,
                DEFAULT_PAYMENT_NUMBER,
                DEFAULT_PAYMENT_STATUS,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_PAYMENT_AGENCY_ID,
                DEFAULT_PAYMENT_AMOUNT,
                "2026-01-01T09:00:00+09:00",
                null);
    }

    public static OrderInfoApiResponse orderInfoApiResponse() {
        return new OrderInfoApiResponse(
                DEFAULT_ORDER_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_ORDER_STATUS,
                1L,
                10L,
                DEFAULT_SHOP_CODE,
                DEFAULT_SHOP_NAME,
                "EXT-ORDER-001",
                "2026-01-01T09:00:00+09:00",
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                "2026-01-01T09:00:00+09:00",
                "2026-01-01T09:00:00+09:00");
    }

    public static ProductOrderApiResponse productOrderApiResponse() {
        return new ProductOrderApiResponse(
                DEFAULT_ORDER_ITEM_ID,
                "ORD-20260101-0001-001",
                100L,
                200L,
                1L,
                50L,
                DEFAULT_SKU_CODE,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_BRAND_NAME,
                DEFAULT_SELLER_NAME,
                DEFAULT_MAIN_IMAGE_URL,
                "EXT-PRODUCT-001",
                "EXT-OPTION-001",
                "외부 상품명",
                "외부 옵션명",
                "https://external.com/image.jpg",
                50000,
                1,
                50000,
                0,
                50000);
    }

    public static ReceiverApiResponse receiverApiResponse() {
        return new ReceiverApiResponse(
                DEFAULT_RECEIVER_NAME,
                DEFAULT_RECEIVER_PHONE,
                DEFAULT_RECEIVER_ZIPCODE,
                DEFAULT_RECEIVER_ADDRESS,
                DEFAULT_RECEIVER_ADDRESS_DETAIL,
                DEFAULT_DELIVERY_REQUEST);
    }

    public static DeliveryApiResponse deliveryApiResponse() {
        return new DeliveryApiResponse(DEFAULT_DELIVERY_STATUS, null, null, null);
    }

    public static CancelSummaryApiResponse cancelSummaryApiResponse() {
        return new CancelSummaryApiResponse(false, 0, 1, null);
    }

    public static ClaimSummaryApiResponse claimSummaryApiResponse() {
        return new ClaimSummaryApiResponse(false, 0, 0, 1, null);
    }

    public static LatestCancelApiResponse latestCancelApiResponse() {
        return new LatestCancelApiResponse(
                DEFAULT_CANCEL_ID,
                DEFAULT_CANCEL_NUMBER,
                "COMPLETED",
                1,
                "2026-01-01T09:00:00+09:00");
    }

    public static LatestClaimApiResponse latestClaimApiResponse() {
        return new LatestClaimApiResponse(
                DEFAULT_CLAIM_ID,
                DEFAULT_CLAIM_NUMBER,
                "REFUND",
                "IN_PROGRESS",
                1,
                "2026-01-01T09:00:00+09:00");
    }

    public static OrderListApiResponse orderListApiResponse() {
        return new OrderListApiResponse(
                orderInfoApiResponse(),
                productOrderApiResponse(),
                paymentInfoApiResponse(),
                receiverApiResponse(),
                deliveryApiResponse(),
                cancelSummaryApiResponse(),
                claimSummaryApiResponse());
    }

    public static List<OrderListApiResponse> orderListApiResponses(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> orderListApiResponse()).toList();
    }

    public static PageApiResponse<OrderListApiResponse> pageApiResponse(int count) {
        return PageApiResponse.of(orderListApiResponses(count), 0, 20, count);
    }

    public static OrderListApiResponseV4 orderListApiResponseV4() {
        return new OrderListApiResponseV4(
                DEFAULT_ORDER_ITEM_ID,
                "ORD-20260101-0001-001",
                new OrderListApiResponseV4.BuyerInfoApiResponse(
                        DEFAULT_BUYER_NAME, DEFAULT_BUYER_EMAIL, DEFAULT_BUYER_PHONE),
                new OrderListApiResponseV4.PaymentDetailApiResponse(
                        DEFAULT_PAYMENT_ID,
                        DEFAULT_PAYMENT_NUMBER,
                        DEFAULT_PAYMENT_AGENCY_ID,
                        DEFAULT_PAYMENT_STATUS,
                        DEFAULT_PAYMENT_METHOD,
                        "2026-01-01T09:00:00+09:00",
                        null,
                        0L,
                        DEFAULT_SHOP_CODE,
                        DEFAULT_PAYMENT_AMOUNT,
                        DEFAULT_PAYMENT_AMOUNT,
                        0),
                new OrderListApiResponseV4.ReceiverInfoApiResponse(
                        DEFAULT_RECEIVER_NAME,
                        DEFAULT_RECEIVER_PHONE,
                        DEFAULT_RECEIVER_ADDRESS,
                        DEFAULT_RECEIVER_ADDRESS_DETAIL,
                        DEFAULT_RECEIVER_ZIPCODE,
                        DEFAULT_DELIVERY_REQUEST),
                new OrderListApiResponseV4.PaymentShipmentInfoApiResponse(
                        DEFAULT_DELIVERY_STATUS, null, null, null),
                new OrderListApiResponseV4.OrderProductApiResponse(
                        DEFAULT_ORDER_ID,
                        DEFAULT_PRODUCT_GROUP_NAME,
                        new OrderListApiResponseV4.PriceApiResponse(50000, 50000, 50000, 0, 0, 0),
                        new OrderListApiResponseV4.BrandApiResponse(50L, DEFAULT_BRAND_NAME),
                        100L,
                        200L,
                        DEFAULT_SELLER_NAME,
                        DEFAULT_MAIN_IMAGE_URL,
                        "",
                        1,
                        "",
                        50000,
                        50000,
                        0,
                        "",
                        DEFAULT_SKU_CODE,
                        List.of()),
                new OrderListApiResponseV4.ExternalOrderInfoApiResponse(
                        10L,
                        DEFAULT_SHOP_CODE,
                        "EXT-ORDER-001",
                        "",
                        "2026-01-01T09:00:00+09:00"),
                new OrderListApiResponseV4.CancelSummaryV4ApiResponse(false, 0, 1, null),
                new OrderListApiResponseV4.ClaimSummaryV4ApiResponse(false, 0, 0, 1, null));
    }

    public static List<OrderListApiResponseV4> orderListApiResponsesV4(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> orderListApiResponseV4()).toList();
    }

    public static PageApiResponse<OrderListApiResponseV4> pageApiResponseV4(int count) {
        return PageApiResponse.of(orderListApiResponsesV4(count), 0, 20, count);
    }

    public static SettlementApiResponse settlementApiResponse() {
        return new SettlementApiResponse(
                10.0, 5000, 45000, 0, 100.0, "2026-01-31T09:00:00+09:00", null);
    }

    public static CancelInfoApiResponse cancelInfoApiResponse() {
        return new CancelInfoApiResponse(
                String.valueOf(3001L),
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_CANCEL_NUMBER,
                "COMPLETED",
                1,
                "CHANGE_MIND",
                "단순 변심",
                50000,
                50000,
                "CARD",
                "2026-01-01T09:00:00+09:00",
                "2026-01-01T09:00:00+09:00",
                "2026-01-01T09:00:00+09:00");
    }

    public static ClaimInfoApiResponse claimInfoApiResponse() {
        return new ClaimInfoApiResponse(
                String.valueOf(4001L),
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_CLAIM_NUMBER,
                "REFUND",
                "IN_PROGRESS",
                1,
                "DEFECTIVE",
                "상품 불량",
                "COLLECT",
                50000,
                0,
                null,
                50000,
                "CARD",
                null,
                "2026-01-01T09:00:00+09:00",
                null,
                null);
    }

    public static TimeLineApiResponse timeLineApiResponse() {
        return new TimeLineApiResponse(
                5001L, "ORDERED", "PREPARING", "SYSTEM", "발주 확인", "2026-01-01T09:00:00+09:00");
    }

    public static OrderDetailApiResponse orderDetailApiResponse() {
        return new OrderDetailApiResponse(
                orderInfoApiResponse(),
                productOrderApiResponse(),
                paymentInfoApiResponse(),
                receiverApiResponse(),
                deliveryApiResponse(),
                cancelSummaryApiResponse(),
                claimSummaryApiResponse(),
                settlementApiResponse(),
                List.of(cancelInfoApiResponse()),
                List.of(claimInfoApiResponse()),
                List.of(timeLineApiResponse()));
    }

}
