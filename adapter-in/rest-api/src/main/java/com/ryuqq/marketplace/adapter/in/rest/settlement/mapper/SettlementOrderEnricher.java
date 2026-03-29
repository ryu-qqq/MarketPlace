package com.ryuqq.marketplace.adapter.in.rest.settlement.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse.BrandV4;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse.BuyerInfoV4;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse.OrderProductV4;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse.PaymentInfoV4;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse.PriceV4;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse.SellerInfoV4;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.manager.OrderCompositionReadManager;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 정산 건별 목록을 V4 스펙에 맞추기 위한 주문 데이터 보강기.
 *
 * <p>정산 원장(Entry)에는 orderItemId 만 존재하므로, 주문 상품/주문 기본 정보를 배치 조회하여 orderProduct / buyer / seller /
 * payment 중첩 필드를 생성한다.
 */
@Component
public class SettlementOrderEnricher {

    private final OrderCompositionReadManager orderReadManager;

    public SettlementOrderEnricher(OrderCompositionReadManager orderReadManager) {
        this.orderReadManager = orderReadManager;
    }

    /** orderItemId 목록으로 OrderItem + Order 정보를 배치 조회한다. */
    public OrderContext loadOrderContext(List<Long> orderItemIds) {
        List<Long> validIds = orderItemIds.stream().filter(id -> id != null && id != 0L).toList();
        if (validIds.isEmpty()) {
            return new OrderContext(Map.of(), Map.of());
        }

        Map<Long, OrderItemResult> orderItems = orderReadManager.findOrderItemsByIds(validIds);

        List<String> orderIds =
                orderItems.values().stream()
                        .map(OrderItemResult::orderId)
                        .filter(id -> id != null && !id.isEmpty())
                        .distinct()
                        .toList();

        Map<String, OrderListResult> orders =
                orderIds.isEmpty() ? Map.of() : orderReadManager.findOrdersByIds(orderIds);

        return new OrderContext(orderItems, orders);
    }

    /** 주문 데이터 컨텍스트. */
    public record OrderContext(
            Map<Long, OrderItemResult> orderItems, Map<String, OrderListResult> orders) {

        public OrderItemResult getItem(Long orderItemId) {
            return orderItems.get(orderItemId);
        }

        public OrderListResult getOrder(Long orderItemId) {
            OrderItemResult item = orderItems.get(orderItemId);
            return item != null ? orders.get(item.orderId()) : null;
        }
    }

    // ==================== V4 중첩 필드 생성 ====================

    public OrderProductV4 toOrderProductV4(OrderItemResult item) {
        if (item == null) {
            return emptyOrderProduct();
        }
        int unitPrice = item.unitPrice();
        int discountRate = unitPrice > 0 ? (item.discountAmount() * 100 / unitPrice) : 0;
        return new OrderProductV4(
                item.orderItemId() != null ? String.valueOf(item.orderItemId()) : "",
                nullToEmpty(item.orderItemNumber()),
                nullToEmpty(item.productGroupName()),
                new PriceV4(
                        unitPrice,
                        unitPrice,
                        unitPrice,
                        item.discountAmount(),
                        discountRate,
                        discountRate),
                new BrandV4(0L, nullToEmpty(item.brandName())),
                item.productGroupId(),
                item.productId(),
                nullToEmpty(item.sellerName()),
                nullToEmpty(item.mainImageUrl()),
                "",
                item.quantity(),
                nullToEmpty(item.orderItemStatus()),
                unitPrice,
                item.totalAmount(),
                0,
                nullToEmpty(item.externalOptionName()),
                nullToEmpty(item.skuCode()),
                List.of());
    }

    public BuyerInfoV4 toBuyerInfoV4(OrderListResult order) {
        if (order == null) {
            return new BuyerInfoV4("", "");
        }
        return new BuyerInfoV4(nullToEmpty(order.buyerName()), nullToEmpty(order.buyerPhone()));
    }

    public SellerInfoV4 toSellerInfoV4(long sellerId, OrderItemResult item) {
        String sellerName = item != null ? nullToEmpty(item.sellerName()) : "";
        return new SellerInfoV4(sellerId, sellerName);
    }

    public PaymentInfoV4 toPaymentInfoV4(OrderListResult order) {
        if (order == null) {
            return new PaymentInfoV4("", "", 0, "");
        }
        return new PaymentInfoV4(
                nullToEmpty(order.paymentNumber()),
                formatInstant(order.paidAt()),
                order.paymentAmount(),
                nullToEmpty(order.paymentMethod()));
    }

    // ==================== 유틸 ====================

    public OrderProductV4 emptyOrderProduct() {
        return new OrderProductV4(
                "",
                "",
                "",
                new PriceV4(0, 0, 0, 0, 0, 0),
                new BrandV4(0L, ""),
                0L,
                0L,
                "",
                "",
                "",
                0,
                "",
                0,
                0,
                0,
                "",
                "",
                List.of());
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    private String formatInstant(Instant instant) {
        String formatted = DateTimeFormatUtils.formatDisplay(instant);
        return formatted != null ? formatted : "";
    }
}
