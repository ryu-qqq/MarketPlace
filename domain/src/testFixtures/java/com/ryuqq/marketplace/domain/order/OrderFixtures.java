package com.ryuqq.marketplace.domain.order;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.common.vo.Email;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.aggregate.OrderHistory;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderHistoryId;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.id.OrderNumber;
import com.ryuqq.marketplace.domain.order.id.PaymentNumber;
import com.ryuqq.marketplace.domain.order.vo.BuyerInfo;
import com.ryuqq.marketplace.domain.order.vo.BuyerName;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderItemPrice;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderReference;
import com.ryuqq.marketplace.domain.order.vo.ExternalProductSnapshot;
import com.ryuqq.marketplace.domain.order.vo.InternalProductReference;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
import com.ryuqq.marketplace.domain.order.vo.PaymentInfo;
import com.ryuqq.marketplace.domain.order.vo.ReceiverInfo;
import java.time.Instant;
import java.util.List;

/**
 * Order 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 Order 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class OrderFixtures {

    private OrderFixtures() {}

    // ===== 기본 상수 =====
    private static final String DEFAULT_ORDER_ID = "01900000-0000-7000-8000-000000000001";
    private static final String DEFAULT_ORDER_NUMBER = "ORD-20260218-0001";
    private static final String DEFAULT_CHANGED_BY = "system";
    private static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    private static final long DEFAULT_SHOP_ID = 10L;
    private static final String DEFAULT_EXTERNAL_ORDER_NO = "EXT-ORDER-20260218-001";
    private static final long DEFAULT_PRODUCT_GROUP_ID = 100L;
    private static final long DEFAULT_PRODUCT_ID = 200L;
    private static final long DEFAULT_SELLER_ID = 1L;
    private static final long DEFAULT_BRAND_ID = 5L;
    private static final String DEFAULT_SKU_CODE = "SKU-TEST-0001";

    // ===== ID Fixtures =====

    public static OrderId defaultOrderId() {
        return OrderId.of(DEFAULT_ORDER_ID);
    }

    public static OrderId defaultOrderId(String value) {
        return OrderId.of(value);
    }

    public static OrderNumber defaultOrderNumber() {
        return OrderNumber.of(DEFAULT_ORDER_NUMBER);
    }

    public static OrderItemId defaultOrderItemId() {
        return OrderItemId.of(1L);
    }

    public static OrderHistoryId defaultOrderHistoryId() {
        return OrderHistoryId.of(1L);
    }

    // ===== VO Fixtures =====

    public static BuyerName defaultBuyerName() {
        return BuyerName.of("홍길동");
    }

    public static BuyerInfo defaultBuyerInfo() {
        return BuyerInfo.of(
                defaultBuyerName(), Email.of("buyer@example.com"), PhoneNumber.of("010-1234-5678"));
    }

    public static ReceiverInfo defaultReceiverInfo() {
        return ReceiverInfo.of(
                "김수령",
                PhoneNumber.of("010-9876-5432"),
                Address.of("12345", "서울시 강남구 테헤란로 1", "101호"),
                "부재시 문앞에 놓아주세요");
    }

    public static ExternalOrderReference defaultExternalOrderReference() {
        return ExternalOrderReference.of(
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                null,
                null,
                DEFAULT_EXTERNAL_ORDER_NO,
                CommonVoFixtures.yesterday());
    }

    public static PaymentInfo defaultPaymentInfo() {
        return PaymentInfo.of(
                PaymentNumber.of("PAY-20260218-0001"),
                "CARD",
                Money.of(20000),
                CommonVoFixtures.now());
    }

    public static ExternalProductSnapshot defaultExternalProductSnapshot() {
        return ExternalProductSnapshot.of(
                "EXT-PROD-001",
                "EXT-OPT-001",
                "테스트 상품명",
                "블랙 / L",
                "https://example.com/images/product.jpg");
    }

    public static InternalProductReference defaultInternalProductReference() {
        return InternalProductReference.of(
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_PRODUCT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_BRAND_ID,
                DEFAULT_SKU_CODE,
                "테스트 상품그룹",
                "테스트 브랜드",
                "테스트 셀러",
                "https://example.com/images/main.jpg");
    }

    public static ExternalOrderItemPrice defaultExternalOrderItemPrice() {
        Money unitPrice = Money.of(10000);
        int quantity = 2;
        Money totalAmount = Money.of(20000);
        Money discountAmount = Money.of(0);
        Money paymentAmount = Money.of(20000);
        return ExternalOrderItemPrice.of(
                unitPrice, quantity, totalAmount, discountAmount, paymentAmount);
    }

    // ===== OrderItem Fixtures =====

    public static OrderItem defaultOrderItem() {
        return OrderItem.forNew(
                defaultInternalProductReference(),
                defaultExternalProductSnapshot(),
                defaultExternalOrderItemPrice(),
                defaultReceiverInfo());
    }

    public static OrderItem reconstitutedOrderItem() {
        return OrderItem.reconstitute(
                defaultOrderItemId(),
                defaultInternalProductReference(),
                defaultExternalProductSnapshot(),
                defaultExternalOrderItemPrice(),
                defaultReceiverInfo(),
                OrderItemStatus.READY,
                null);
    }

    public static OrderItem confirmedOrderItem() {
        return OrderItem.reconstitute(
                defaultOrderItemId(),
                defaultInternalProductReference(),
                defaultExternalProductSnapshot(),
                defaultExternalOrderItemPrice(),
                defaultReceiverInfo(),
                OrderItemStatus.CONFIRMED,
                null);
    }

    public static OrderItem reconstitutedOrderItem(long id, OrderItemStatus status) {
        return OrderItem.reconstitute(
                OrderItemId.of(id),
                defaultInternalProductReference(),
                defaultExternalProductSnapshot(),
                defaultExternalOrderItemPrice(),
                defaultReceiverInfo(),
                status,
                null);
    }

    // ===== OrderHistory Fixtures =====

    public static OrderHistory defaultOrderHistory(OrderId orderId) {
        return OrderHistory.of(
                orderId,
                null,
                OrderStatus.ORDERED,
                DEFAULT_CHANGED_BY,
                null,
                CommonVoFixtures.now());
    }

    // ===== Order Aggregate Fixtures =====

    public static Order newOrder() {
        return Order.forNew(
                defaultOrderId(),
                defaultOrderNumber(),
                defaultBuyerInfo(),
                defaultPaymentInfo(),
                defaultExternalOrderReference(),
                List.of(defaultOrderItem()),
                DEFAULT_CHANGED_BY,
                CommonVoFixtures.now());
    }

    public static Order orderedOrder() {
        return Order.reconstitute(
                defaultOrderId(),
                defaultOrderNumber(),
                OrderStatus.ORDERED,
                defaultBuyerInfo(),
                defaultPaymentInfo(),
                defaultExternalOrderReference(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                List.of(reconstitutedOrderItem()),
                List.of(defaultOrderHistory(defaultOrderId())));
    }

    public static Order preparingOrder() {
        return Order.reconstitute(
                defaultOrderId(),
                defaultOrderNumber(),
                OrderStatus.PREPARING,
                defaultBuyerInfo(),
                defaultPaymentInfo(),
                defaultExternalOrderReference(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                List.of(reconstitutedOrderItem()),
                List.of());
    }

    public static Order shippedOrder() {
        return Order.reconstitute(
                defaultOrderId(),
                defaultOrderNumber(),
                OrderStatus.SHIPPED,
                defaultBuyerInfo(),
                defaultPaymentInfo(),
                defaultExternalOrderReference(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                List.of(reconstitutedOrderItem()),
                List.of());
    }

    public static Order deliveredOrder() {
        return Order.reconstitute(
                defaultOrderId(),
                defaultOrderNumber(),
                OrderStatus.DELIVERED,
                defaultBuyerInfo(),
                defaultPaymentInfo(),
                defaultExternalOrderReference(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                List.of(reconstitutedOrderItem()),
                List.of());
    }

    public static Order confirmedOrder() {
        return Order.reconstitute(
                defaultOrderId(),
                defaultOrderNumber(),
                OrderStatus.CONFIRMED,
                defaultBuyerInfo(),
                defaultPaymentInfo(),
                defaultExternalOrderReference(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                List.of(reconstitutedOrderItem()),
                List.of());
    }

    public static Order cancelledOrder() {
        return Order.reconstitute(
                defaultOrderId(),
                defaultOrderNumber(),
                OrderStatus.CANCELLED,
                defaultBuyerInfo(),
                defaultPaymentInfo(),
                defaultExternalOrderReference(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                List.of(reconstitutedOrderItem()),
                List.of());
    }

    public static Order claimInProgressOrder() {
        return Order.reconstitute(
                defaultOrderId(),
                defaultOrderNumber(),
                OrderStatus.CLAIM_IN_PROGRESS,
                defaultBuyerInfo(),
                defaultPaymentInfo(),
                defaultExternalOrderReference(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                List.of(reconstitutedOrderItem()),
                List.of());
    }

    public static Order refundedOrder() {
        return Order.reconstitute(
                defaultOrderId(),
                defaultOrderNumber(),
                OrderStatus.REFUNDED,
                defaultBuyerInfo(),
                defaultPaymentInfo(),
                defaultExternalOrderReference(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                List.of(reconstitutedOrderItem()),
                List.of());
    }

    public static Order exchangedOrder() {
        return Order.reconstitute(
                defaultOrderId(),
                defaultOrderNumber(),
                OrderStatus.EXCHANGED,
                defaultBuyerInfo(),
                defaultPaymentInfo(),
                defaultExternalOrderReference(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                List.of(reconstitutedOrderItem()),
                List.of());
    }

    // ===== 특정 시간 지정 Fixtures =====

    public static Order newOrder(Instant now) {
        return Order.forNew(
                defaultOrderId(),
                defaultOrderNumber(),
                defaultBuyerInfo(),
                defaultPaymentInfo(),
                defaultExternalOrderReference(),
                List.of(defaultOrderItem()),
                DEFAULT_CHANGED_BY,
                now);
    }
}
