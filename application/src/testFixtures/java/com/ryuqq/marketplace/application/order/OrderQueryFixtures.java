package com.ryuqq.marketplace.application.order;

import com.ryuqq.marketplace.application.order.dto.response.OrderCancelResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderClaimResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderSummaryResult;
import com.ryuqq.marketplace.application.order.dto.response.PaymentResult;
import com.ryuqq.marketplace.application.order.internal.ProductOrderDetailBundle;
import com.ryuqq.marketplace.application.order.internal.ProductOrderListBundle;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Order Application Query 테스트 Fixtures.
 *
 * <p>Order 관련 Query 파라미터 및 Result 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class OrderQueryFixtures {

    private OrderQueryFixtures() {}

    // ===== 기본 상수 =====

    public static final String DEFAULT_ORDER_ID = "01900000-0000-7000-8000-000000000001";
    public static final String DEFAULT_ORDER_NUMBER = "ORD-20260218-0001";
    public static final String DEFAULT_PAYMENT_ID = "01900000-0000-7000-8000-000000000002";
    public static final String DEFAULT_PAYMENT_NUMBER = "PAY-20260218-0001";

    // ===== PaymentResult Fixtures =====

    public static PaymentResult paymentResult() {
        return new PaymentResult(
                DEFAULT_PAYMENT_ID,
                DEFAULT_PAYMENT_NUMBER,
                "PAID",
                "CARD",
                "PG-TXN-001",
                20000,
                Instant.parse("2026-02-18T10:05:00Z"),
                null);
    }

    public static PaymentResult paymentResultWithCanceled() {
        return new PaymentResult(
                DEFAULT_PAYMENT_ID,
                DEFAULT_PAYMENT_NUMBER,
                "CANCELLED",
                "CARD",
                "PG-TXN-001",
                20000,
                Instant.parse("2026-02-18T10:05:00Z"),
                Instant.parse("2026-02-19T10:00:00Z"));
    }

    // ===== OrderListResult Fixtures =====

    public static OrderListResult orderListResult() {
        return orderListResult(DEFAULT_ORDER_ID);
    }

    public static OrderListResult orderListResult(String orderId) {
        return new OrderListResult(
                orderId,
                DEFAULT_ORDER_NUMBER,
                1L,
                10L,
                "NAVER",
                "네이버 스마트스토어",
                "EXT-ORDER-20260218-001",
                Instant.parse("2026-02-18T10:00:00Z"),
                "홍길동",
                "buyer@example.com",
                "010-1234-5678",
                DEFAULT_PAYMENT_ID,
                DEFAULT_PAYMENT_NUMBER,
                "PAID",
                "CARD",
                20000,
                Instant.parse("2026-02-18T10:05:00Z"),
                1L,
                Instant.parse("2026-02-18T10:06:00Z"),
                Instant.parse("2026-02-18T10:06:00Z"));
    }

    // ===== OrderItemResult Fixtures =====

    public static OrderItemResult orderItemResult() {
        return orderItemResult(1001L, DEFAULT_ORDER_ID);
    }

    public static OrderItemResult orderItemResult(Long orderItemId, String orderId) {
        return new OrderItemResult(
                orderItemId,
                "ORD-20250115-0001-001",
                orderId,
                100L,
                1L,
                1L,
                null,
                200L,
                "SKU-TEST-0001",
                "테스트 상품그룹",
                "테스트 브랜드",
                "테스트 셀러",
                "https://example.com/images/main.jpg",
                "EXT-PROD-001",
                "EXT-OPT-001",
                "테스트 상품명",
                "블랙 / L",
                "https://example.com/images/product.jpg",
                10000,
                10000,
                2,
                20000,
                0,
                20000,
                "김수령",
                "010-9876-5432",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호",
                "부재시 문앞에 놓아주세요",
                "PENDING",
                null);
    }

    // ===== OrderCancelResult Fixtures =====

    public static OrderCancelResult completedCancelResult(Long orderItemId) {
        return new OrderCancelResult(
                "1",
                orderItemId,
                "CANCEL-20260218-0001",
                "COMPLETED",
                1,
                "CHANGE_MIND",
                "단순 변심",
                10000,
                10000,
                "CARD",
                Instant.parse("2026-02-19T11:00:00Z"),
                Instant.parse("2026-02-19T10:00:00Z"),
                Instant.parse("2026-02-19T11:00:00Z"));
    }

    public static OrderCancelResult requestedCancelResult(Long orderItemId) {
        return new OrderCancelResult(
                "2",
                orderItemId,
                "CANCEL-20260218-0002",
                "REQUESTED",
                1,
                "CHANGE_MIND",
                "단순 변심",
                10000,
                10000,
                "CARD",
                null,
                Instant.parse("2026-02-19T10:00:00Z"),
                null);
    }

    // ===== OrderClaimResult Fixtures =====

    public static OrderClaimResult completedClaimResult(Long orderItemId) {
        return new OrderClaimResult(
                "1",
                orderItemId,
                "CLAIM-20260218-0001",
                "REFUND",
                "COMPLETED",
                1,
                "DEFECT",
                "상품 불량",
                "COLLECT",
                10000,
                0,
                null,
                10000,
                "CARD",
                Instant.parse("2026-02-20T11:00:00Z"),
                Instant.parse("2026-02-20T10:00:00Z"),
                Instant.parse("2026-02-20T11:00:00Z"),
                null);
    }

    public static OrderClaimResult requestedClaimResult(Long orderItemId) {
        return new OrderClaimResult(
                "2",
                orderItemId,
                "CLAIM-20260218-0002",
                "REFUND",
                "REQUESTED",
                1,
                "DEFECT",
                "상품 불량",
                "COLLECT",
                10000,
                0,
                null,
                10000,
                "CARD",
                null,
                Instant.parse("2026-02-20T10:00:00Z"),
                null,
                null);
    }

    // ===== ProductOrderListBundle Fixtures =====

    public static ProductOrderListBundle productOrderListBundle() {
        OrderItemResult item = orderItemResult();
        OrderListResult order = orderListResult();
        return new ProductOrderListBundle(
                List.of(item), Map.of(DEFAULT_ORDER_ID, order), Map.of(), Map.of(), 1L);
    }

    public static ProductOrderListBundle productOrderListBundleWithCancels() {
        OrderItemResult item = orderItemResult();
        OrderListResult order = orderListResult();
        return new ProductOrderListBundle(
                List.of(item),
                Map.of(DEFAULT_ORDER_ID, order),
                Map.of(item.orderItemId(), List.of(completedCancelResult(item.orderItemId()))),
                Map.of(),
                1L);
    }

    public static ProductOrderListBundle emptyProductOrderListBundle() {
        return new ProductOrderListBundle(List.of(), Map.of(), Map.of(), Map.of(), 0L);
    }

    // ===== ProductOrderDetailBundle Fixtures =====

    public static ProductOrderDetailBundle productOrderDetailBundle() {
        return new ProductOrderDetailBundle(
                orderItemResult(),
                orderListResult(),
                paymentResult(),
                List.of(),
                List.of(),
                List.of());
    }

    public static ProductOrderDetailBundle productOrderDetailBundleWithNullPayment() {
        return new ProductOrderDetailBundle(
                orderItemResult(), orderListResult(), null, List.of(), List.of(), List.of());
    }

    public static ProductOrderDetailBundle productOrderDetailBundleWithHistories() {
        return new ProductOrderDetailBundle(
                orderItemResult(),
                orderListResult(),
                paymentResult(),
                List.of(completedCancelResult(1001L)),
                List.of(completedClaimResult(1001L)),
                List.of(orderHistoryResult()));
    }

    // ===== OrderHistoryResult Fixtures =====

    public static OrderHistoryResult orderHistoryResult() {
        return new OrderHistoryResult(
                1L, null, "ORDERED", "system", null, Instant.parse("2026-02-18T10:06:00Z"));
    }

    // ===== OrderSummaryResult Fixtures =====

    public static Map<OrderItemStatus, Long> orderItemStatusCounts() {
        Map<OrderItemStatus, Long> counts = new EnumMap<>(OrderItemStatus.class);
        counts.put(OrderItemStatus.READY, 10L);
        counts.put(OrderItemStatus.CONFIRMED, 5L);
        counts.put(OrderItemStatus.CANCELLED, 2L);
        counts.put(OrderItemStatus.RETURN_REQUESTED, 1L);
        counts.put(OrderItemStatus.RETURNED, 0L);
        return counts;
    }

    public static OrderSummaryResult orderSummaryResult() {
        return new OrderSummaryResult(10L, 5L, 2L, 1L, 0L);
    }
}
