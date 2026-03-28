package com.ryuqq.marketplace.adapter.in.rest.common.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4.BrandV4;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4.BuyerInfoV4;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4.CollectShipmentV4;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4.ExternalOrderInfoV4;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4.OrderProductV4;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4.PaymentV4;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4.PriceV4;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4.ReceiverInfoV4;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4.RefundInfoV4;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.manager.OrderCompositionReadManager;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 클레임(반품/취소/교환) 리스트를 V4 프론트 스펙에 맞추기 위한 주문 데이터 보강기.
 *
 * <p>클레임 결과에서 orderItemId 목록을 추출하여 주문 상품/주문 기본 정보를 배치 조회하고, V4 중첩 구조의 공통 필드(orderProduct,
 * buyerInfo, payment, receiverInfo, externalOrderInfo)를 생성한다.
 */
@Component
public class ClaimOrderEnricher {

    private final OrderCompositionReadManager orderReadManager;

    public ClaimOrderEnricher(OrderCompositionReadManager orderReadManager) {
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

    // ==================== V4 공통 필드 생성 ====================

    public OrderProductV4 toOrderProductV4(Long orderItemId, OrderContext ctx) {
        OrderItemResult item = ctx.getItem(orderItemId);
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

    public BuyerInfoV4 toBuyerInfoV4(Long orderItemId, OrderContext ctx) {
        OrderListResult order = ctx.getOrder(orderItemId);
        if (order == null) {
            return new BuyerInfoV4("", "");
        }
        return new BuyerInfoV4(nullToEmpty(order.buyerName()), nullToEmpty(order.buyerPhone()));
    }

    public PaymentV4 toPaymentV4(Long orderItemId, OrderContext ctx) {
        OrderListResult order = ctx.getOrder(orderItemId);
        if (order == null) {
            return new PaymentV4("", "", 0, "");
        }
        return new PaymentV4(
                nullToEmpty(order.paymentNumber()),
                formatInstant(order.paidAt()),
                order.paymentAmount(),
                nullToEmpty(order.paymentMethod()));
    }

    public ReceiverInfoV4 toReceiverInfoV4(Long orderItemId, OrderContext ctx) {
        OrderItemResult item = ctx.getItem(orderItemId);
        if (item == null) {
            return new ReceiverInfoV4("", "", "", "", "");
        }
        return new ReceiverInfoV4(
                nullToEmpty(item.receiverName()),
                nullToEmpty(item.receiverPhone()),
                nullToEmpty(item.receiverAddress()),
                nullToEmpty(item.receiverAddressDetail()),
                nullToEmpty(item.receiverZipcode()));
    }

    public ExternalOrderInfoV4 toExternalOrderInfoV4(Long orderItemId, OrderContext ctx) {
        OrderListResult order = ctx.getOrder(orderItemId);
        if (order == null) {
            return new ExternalOrderInfoV4("", "");
        }
        return new ExternalOrderInfoV4(
                nullToEmpty(order.shopCode()), nullToEmpty(order.externalOrderNo()));
    }

    public ClaimListItemApiResponseV4.ClaimInfoV4 toClaimInfoV4(
            String claimId,
            String claimNumber,
            String status,
            int qty,
            String reasonType,
            String reasonDetail,
            Integer originalAmount,
            Integer finalAmount,
            String refundMethod,
            String holdReason,
            boolean isHold,
            Instant requestedAt,
            Instant createdAt,
            ClaimListItemApiResponseV4.ExchangeOptionV4 exchangeOption,
            ClaimListItemApiResponseV4.AmountAdjustmentV4 amountAdjustment) {
        return new ClaimListItemApiResponseV4.ClaimInfoV4(
                nullToEmpty(claimId),
                nullToEmpty(claimNumber),
                nullToEmpty(status),
                qty,
                nullToEmpty(reasonDetail),
                new RefundInfoV4(
                        originalAmount != null ? originalAmount : 0,
                        0,
                        "",
                        finalAmount != null ? finalAmount : 0,
                        nullToEmpty(refundMethod),
                        ""),
                new CollectShipmentV4(
                        new ClaimListItemApiResponseV4.MethodV4("", ""),
                        "",
                        new ClaimListItemApiResponseV4.FeeInfoV4("SELLER", 0),
                        ""),
                nullToEmpty(holdReason),
                isHold,
                formatInstant(requestedAt),
                formatInstant(requestedAt),
                exchangeOption,
                amountAdjustment);
    }

    // ==================== 유틸 ====================

    private OrderProductV4 emptyOrderProduct() {
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

    public String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    public String formatInstant(Instant instant) {
        String formatted = DateTimeFormatUtils.formatDisplay(instant);
        return formatted != null ? formatted : "";
    }
}
