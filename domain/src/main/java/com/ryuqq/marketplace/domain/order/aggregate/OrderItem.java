package com.ryuqq.marketplace.domain.order.aggregate;

import com.ryuqq.marketplace.domain.order.exception.OrderErrorCode;
import com.ryuqq.marketplace.domain.order.exception.OrderException;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderItemPrice;
import com.ryuqq.marketplace.domain.order.vo.ExternalProductSnapshot;
import com.ryuqq.marketplace.domain.order.vo.InternalProductReference;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import com.ryuqq.marketplace.domain.order.vo.ReceiverInfo;
import com.ryuqq.marketplace.domain.order.vo.SettlementInfo;

/** 주문 상품. Order Aggregate 내부 구성 요소. */
public class OrderItem {

    private final OrderItemId id;
    private final InternalProductReference internalProduct;
    private final ExternalProductSnapshot externalProduct;
    private final ExternalOrderItemPrice price;
    private final ReceiverInfo receiverInfo;
    private OrderItemStatus status;
    private SettlementInfo settlementInfo;

    private OrderItem(
            OrderItemId id,
            InternalProductReference internalProduct,
            ExternalProductSnapshot externalProduct,
            ExternalOrderItemPrice price,
            ReceiverInfo receiverInfo,
            OrderItemStatus status,
            SettlementInfo settlementInfo) {
        this.id = id;
        this.internalProduct = internalProduct;
        this.externalProduct = externalProduct;
        this.price = price;
        this.receiverInfo = receiverInfo;
        this.status = status;
        this.settlementInfo = settlementInfo;
    }

    public static OrderItem forNew(
            InternalProductReference internalProduct,
            ExternalProductSnapshot externalProduct,
            ExternalOrderItemPrice price,
            ReceiverInfo receiverInfo) {
        return new OrderItem(
                OrderItemId.forNew(),
                internalProduct,
                externalProduct,
                price,
                receiverInfo,
                OrderItemStatus.READY,
                null);
    }

    public static OrderItem reconstitute(
            OrderItemId id,
            InternalProductReference internalProduct,
            ExternalProductSnapshot externalProduct,
            ExternalOrderItemPrice price,
            ReceiverInfo receiverInfo,
            OrderItemStatus status,
            SettlementInfo settlementInfo) {
        return new OrderItem(
                id, internalProduct, externalProduct, price, receiverInfo, status, settlementInfo);
    }

    public void confirm() {
        validateTransition(OrderItemStatus.CONFIRMED);
        this.status = OrderItemStatus.CONFIRMED;
    }

    public void cancel() {
        validateTransition(OrderItemStatus.CANCELLED);
        this.status = OrderItemStatus.CANCELLED;
    }

    public void requestReturn() {
        validateTransition(OrderItemStatus.RETURN_REQUESTED);
        this.status = OrderItemStatus.RETURN_REQUESTED;
    }

    public void completeReturn() {
        validateTransition(OrderItemStatus.RETURNED);
        this.status = OrderItemStatus.RETURNED;
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

    public Long idValue() {
        return id.value();
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
}
