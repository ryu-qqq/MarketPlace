package com.ryuqq.marketplace.adapter.in.rest.legacy.order;

import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyUpdateOrderRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.BrandInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.BuyerInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.ClothesDetailInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.OrderHistoryInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.OrderProductInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.PaymentInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.PaymentShipmentInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.PriceInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.ProductGroupDetails;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.ProductStatusInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.ReceiverInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.SettlementInfo;
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
        BuyerInfo buyerInfo = new BuyerInfo("", "", "");

        PaymentInfo payment =
                new PaymentInfo(
                        DEFAULT_PAYMENT_ID,
                        "",
                        "",
                        "",
                        "2024-06-13 15:59:06",
                        null,
                        DEFAULT_USER_ID,
                        "OUR_MALL",
                        50000L,
                        50000L,
                        0);

        ReceiverInfo receiverInfo =
                new ReceiverInfo("홍길동", "010-1234-5678", "서울시 강남구", "101호", "06000", "KR", "");

        PaymentShipmentInfo paymentShipmentInfo =
                new PaymentShipmentInfo("DELIVERY_PENDING", "REFER_DETAIL", "", null);

        SettlementInfo settlementInfo =
                new SettlementInfo(10.0, 5000.0, 45000, 45000, 90.0, null, null);

        PriceInfo priceInfo = new PriceInfo(60000L, 50000L, 50000L);
        ProductStatusInfo productStatus = new ProductStatusInfo("N", "Y");
        ClothesDetailInfo clothesDetailInfo = new ClothesDetailInfo("NEW", "", null);
        ProductGroupDetails productGroupDetails =
                new ProductGroupDetails(
                        "테스트 상품",
                        "OPTION_ONE",
                        "MENUAL",
                        priceInfo,
                        productStatus,
                        clothesDetailInfo,
                        1L,
                        1390L,
                        5465L);

        BrandInfo brand = new BrandInfo(5465L, "테스트 브랜드");

        OrderProductInfo orderProduct =
                new OrderProductInfo(
                        DEFAULT_ORDER_ID,
                        productGroupDetails,
                        brand,
                        DEFAULT_PRODUCT_GROUP_ID,
                        DEFAULT_PRODUCT_ID,
                        "",
                        "",
                        "",
                        1,
                        DEFAULT_ORDER_STATUS,
                        60000L,
                        50000L,
                        0,
                        "",
                        "",
                        List.of());

        List<OrderHistoryInfo> orderHistories = List.of();

        return new LegacyOrderResponse(
                DEFAULT_ORDER_ID,
                buyerInfo,
                payment,
                receiverInfo,
                paymentShipmentInfo,
                settlementInfo,
                orderProduct,
                orderHistories);
    }

    public static List<LegacyOrderResponse> orderListResponses(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> orderResponse())
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
