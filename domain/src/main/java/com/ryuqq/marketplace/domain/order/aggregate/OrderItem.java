package com.ryuqq.marketplace.domain.order.aggregate;

import com.ryuqq.marketplace.domain.order.exception.OrderErrorCode;
import com.ryuqq.marketplace.domain.order.exception.OrderException;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.id.OrderItemNumber;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderItemPrice;
import com.ryuqq.marketplace.domain.order.vo.ExternalProductSnapshot;
import com.ryuqq.marketplace.domain.order.vo.InternalProductReference;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import com.ryuqq.marketplace.domain.order.vo.ReceiverInfo;
import com.ryuqq.marketplace.domain.order.vo.SettlementInfo;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 주문 상품. Order Aggregate 내부 구성 요소. */
public class OrderItem {

    private final OrderItemId id;
    private final OrderItemNumber orderItemNumber;
    private final InternalProductReference internalProduct;
    private final ExternalProductSnapshot externalProduct;
    private final ExternalOrderItemPrice price;
    private final ReceiverInfo receiverInfo;
    private OrderItemStatus status;
    private SettlementInfo settlementInfo;

    private final List<OrderItemHistory> histories = new ArrayList<>();

    private OrderItem(
            OrderItemId id,
            OrderItemNumber orderItemNumber,
            InternalProductReference internalProduct,
            ExternalProductSnapshot externalProduct,
            ExternalOrderItemPrice price,
            ReceiverInfo receiverInfo,
            OrderItemStatus status,
            SettlementInfo settlementInfo) {
        this.id = id;
        this.orderItemNumber = orderItemNumber;
        this.internalProduct = internalProduct;
        this.externalProduct = externalProduct;
        this.price = price;
        this.receiverInfo = receiverInfo;
        this.status = status;
        this.settlementInfo = settlementInfo;
    }

    public static OrderItem forNew(
            OrderItemId id,
            OrderItemNumber orderItemNumber,
            InternalProductReference internalProduct,
            ExternalProductSnapshot externalProduct,
            ExternalOrderItemPrice price,
            ReceiverInfo receiverInfo) {
        return new OrderItem(
                id,
                orderItemNumber,
                internalProduct,
                externalProduct,
                price,
                receiverInfo,
                OrderItemStatus.READY,
                null);
    }

    public static OrderItem reconstitute(
            OrderItemId id,
            OrderItemNumber orderItemNumber,
            InternalProductReference internalProduct,
            ExternalProductSnapshot externalProduct,
            ExternalOrderItemPrice price,
            ReceiverInfo receiverInfo,
            OrderItemStatus status,
            SettlementInfo settlementInfo,
            List<OrderItemHistory> histories) {
        OrderItem item = new OrderItem(
                id, orderItemNumber, internalProduct, externalProduct, price, receiverInfo, status,
                settlementInfo);
        if (histories != null) {
            item.histories.addAll(histories);
        }
        return item;
    }

    public void confirm(String changedBy, Instant now) {
        OrderItemStatus from = this.status;
        validateTransition(OrderItemStatus.CONFIRMED);
        this.status = OrderItemStatus.CONFIRMED;
        this.histories.add(OrderItemHistory.of(this.id, from, OrderItemStatus.CONFIRMED, changedBy, null, now));
    }

    public void cancel(String changedBy, String reason, Instant now) {
        OrderItemStatus from = this.status;
        validateTransition(OrderItemStatus.CANCELLED);
        this.status = OrderItemStatus.CANCELLED;
        this.histories.add(OrderItemHistory.of(this.id, from, OrderItemStatus.CANCELLED, changedBy, reason, now));
    }

    public void requestReturn(String changedBy, String reason, Instant now) {
        OrderItemStatus from = this.status;
        validateTransition(OrderItemStatus.RETURN_REQUESTED);
        this.status = OrderItemStatus.RETURN_REQUESTED;
        this.histories.add(OrderItemHistory.of(this.id, from, OrderItemStatus.RETURN_REQUESTED, changedBy, reason, now));
    }

    public void completeReturn(String changedBy, Instant now) {
        OrderItemStatus from = this.status;
        validateTransition(OrderItemStatus.RETURNED);
        this.status = OrderItemStatus.RETURNED;
        this.histories.add(OrderItemHistory.of(this.id, from, OrderItemStatus.RETURNED, changedBy, null, now));
    }

    public boolean isConfirmable() {
        return status.canTransitionTo(OrderItemStatus.CONFIRMED);
    }

    public void updateSettlementInfo(SettlementInfo settlementInfo) {
        this.settlementInfo = settlementInfo;
    }

    private void validateTransition(OrderItemStatus target) {
        if (!this.status.canTransitionTo(target)) {
            throw new OrderException(
                    OrderErrorCode.INVALID_STATUS_TRANSITION,
                    String.format(
                            "주문상품 %s: %s 상태에서 %s 상태로 변경할 수 없습니다", id.value(), this.status, target));
        }
    }

    public OrderItemId id() {
        return id;
    }

    public String idValue() {
        return id.value();
    }

    public OrderItemNumber orderItemNumber() {
        return orderItemNumber;
    }

    public String orderItemNumberValue() {
        return orderItemNumber.value();
    }

    public InternalProductReference internalProduct() {
        return internalProduct;
    }

    public ExternalProductSnapshot externalProduct() {
        return externalProduct;
    }

    public ExternalOrderItemPrice price() {
        return price;
    }

    public int quantity() {
        return price.quantity();
    }

    public long sellerId() {
        return internalProduct.sellerId();
    }

    public ReceiverInfo receiverInfo() {
        return receiverInfo;
    }

    public OrderItemStatus status() {
        return status;
    }

    public SettlementInfo settlementInfo() {
        return settlementInfo;
    }

    public List<OrderItemHistory> histories() {
        return Collections.unmodifiableList(histories);
    }
}
