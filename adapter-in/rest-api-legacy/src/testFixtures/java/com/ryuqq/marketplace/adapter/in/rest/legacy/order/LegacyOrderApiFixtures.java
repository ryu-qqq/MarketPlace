package com.ryuqq.marketplace.adapter.in.rest.legacy.order;

import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyUpdateOrderRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderHistoryResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderListResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.LegacyOrderProductInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.LegacyPaymentInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.LegacyReceiverInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyUpdateOrderResponse;
import java.time.Instant;
import java.util.List;

/**
 * Legacy Order API 테스트 Fixtures.
 *
 * <p>Legacy 주문 REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacyOrderApiFixtures {

    private LegacyOrderApiFixtures() {}

    // ===== 상수 =====
    public static final long DEFAULT_ORDER_ID = 1001L;
    public static final long DEFAULT_USER_ID = 42L;
    public static final long DEFAULT_PAYMENT_ID = 2001L;
    public static final long DEFAULT_PRODUCT_ID = 3001L;
    public static final long DEFAULT_PRODUCT_GROUP_ID = 4001L;
    public static final String DEFAULT_ORDER_STATUS = "PAID";
    public static final String DEFAULT_TO_BE_ORDER_STATUS = "SHIPPING";
    public static final String DEFAULT_CHANGE_REASON = "배송 시작";
    public static final String DEFAULT_CHANGE_DETAIL_REASON = "출고 완료 후 배송 상태로 변경";
    public static final String DEFAULT_INVOICE_NO = "1234567890";
    public static final String DEFAULT_SHIPMENT_TYPE = "PARCEL";
    public static final String DEFAULT_COMPANY_CODE = "CJ";
    public static final String DEFAULT_UPDATE_TYPE = "shipOrder";

    // ===== Request Fixtures =====

    public static LegacyUpdateOrderRequest updateOrderRequest() {
        return new LegacyUpdateOrderRequest(
                DEFAULT_UPDATE_TYPE,
                DEFAULT_ORDER_ID,
                DEFAULT_TO_BE_ORDER_STATUS,
                false,
                DEFAULT_CHANGE_REASON,
                DEFAULT_CHANGE_DETAIL_REASON,
                shipmentInfoRequest());
    }

    public static LegacyUpdateOrderRequest updateOrderRequestWithoutShipment() {
        return new LegacyUpdateOrderRequest(
                "normalOrder",
                DEFAULT_ORDER_ID,
                DEFAULT_TO_BE_ORDER_STATUS,
                false,
                DEFAULT_CHANGE_REASON,
                DEFAULT_CHANGE_DETAIL_REASON,
                null);
    }

    public static LegacyUpdateOrderRequest.ShipmentInfo shipmentInfoRequest() {
        return new LegacyUpdateOrderRequest.ShipmentInfo(
                DEFAULT_INVOICE_NO, DEFAULT_SHIPMENT_TYPE, DEFAULT_COMPANY_CODE);
    }

    // ===== Response Fixtures =====

    public static LegacyOrderResponse orderResponse() {
        LegacyPaymentInfo payment = new LegacyPaymentInfo(DEFAULT_PAYMENT_ID, 50000L, 10L, 90L);
        LegacyReceiverInfo receiverInfo =
                new LegacyReceiverInfo("홍길동", "010-1234-5678", "서울시 강남구", "101호", "06000", "");
        LegacyOrderProductInfo orderProduct =
                new LegacyOrderProductInfo(
                        DEFAULT_PRODUCT_GROUP_ID,
                        DEFAULT_PRODUCT_ID,
                        "테스트 상품",
                        "테스트 브랜드",
                        "",
                        1,
                        DEFAULT_ORDER_STATUS,
                        60000L,
                        50000L,
                        "",
                        List.of());

        return new LegacyOrderResponse(
                DEFAULT_ORDER_ID, "", payment, receiverInfo, orderProduct, Instant.now());
    }

    public static LegacyOrderListResponse orderListResponse() {
        return new LegacyOrderListResponse(orderResponse(), List.of());
    }

    public static List<LegacyOrderListResponse> orderListResponses(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> orderListResponse())
                .toList();
    }

    public static LegacyOrderHistoryResponse orderHistoryResponse() {
        return new LegacyOrderHistoryResponse(
                1L,
                DEFAULT_ORDER_ID,
                DEFAULT_ORDER_STATUS,
                DEFAULT_CHANGE_REASON,
                DEFAULT_CHANGE_DETAIL_REASON,
                Instant.now());
    }

    public static LegacyUpdateOrderResponse updateOrderResponse() {
        return new LegacyUpdateOrderResponse(
                DEFAULT_ORDER_ID,
                DEFAULT_USER_ID,
                DEFAULT_TO_BE_ORDER_STATUS,
                DEFAULT_ORDER_STATUS,
                DEFAULT_CHANGE_REASON,
                DEFAULT_CHANGE_DETAIL_REASON);
    }
}
