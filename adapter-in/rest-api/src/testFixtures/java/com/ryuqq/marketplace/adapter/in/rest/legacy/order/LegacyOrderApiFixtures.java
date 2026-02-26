package com.ryuqq.marketplace.adapter.in.rest.legacy.order;

import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyShipmentInfoRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyUpdateOrderRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderListResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyUpdateOrderResponse;
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
    public static final String DEFAULT_ORDER_STATUS = "PAID";
    public static final String DEFAULT_TO_BE_ORDER_STATUS = "SHIPPING";
    public static final String DEFAULT_CHANGE_REASON = "배송 시작";
    public static final String DEFAULT_CHANGE_DETAIL_REASON = "출고 완료 후 배송 상태로 변경";
    public static final String DEFAULT_INVOICE_NO = "1234567890";
    public static final String DEFAULT_SHIPMENT_TYPE = "PARCEL";
    public static final String DEFAULT_COMPANY_CODE = "CJ";
    public static final String DEFAULT_COURIER_CODE = "CJ_LOGISTICS";
    public static final String DEFAULT_TRACKING_NUMBER = "9876543210";
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

    public static LegacyShipmentInfoRequest shipmentInfoRequest() {
        return new LegacyShipmentInfoRequest(
                DEFAULT_INVOICE_NO, DEFAULT_SHIPMENT_TYPE, DEFAULT_COMPANY_CODE);
    }

    // ===== Response Fixtures =====

    public static LegacyOrderResponse orderResponse() {
        return new LegacyOrderResponse(DEFAULT_ORDER_ID);
    }

    public static LegacyOrderListResponse orderListResponse() {
        return new LegacyOrderListResponse(DEFAULT_ORDER_ID);
    }

    public static List<LegacyOrderListResponse> orderListResponses(int count) {
        return java.util.stream.LongStream.rangeClosed(1, count)
                .mapToObj(LegacyOrderListResponse::new)
                .toList();
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
