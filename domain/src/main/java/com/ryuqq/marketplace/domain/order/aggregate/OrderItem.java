package com.ryuqq.marketplace.domain.order.aggregate;

import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderItemPrice;
import com.ryuqq.marketplace.domain.order.vo.ExternalProductSnapshot;
import com.ryuqq.marketplace.domain.order.vo.InternalProductReference;
import com.ryuqq.marketplace.domain.order.vo.PaymentShipmentInfo;
import com.ryuqq.marketplace.domain.order.vo.ReceiverInfo;
import com.ryuqq.marketplace.domain.order.vo.SettlementInfo;

/** 주문 상품. Order Aggregate 내부 구성 요소. */
public class OrderItem {

    private final OrderItemId id;
    private final InternalProductReference internalProduct;
    private final ExternalProductSnapshot externalProduct;
    private final ExternalOrderItemPrice price;
    private final ReceiverInfo receiverInfo;
    private PaymentShipmentInfo shipmentInfo;
    private SettlementInfo settlementInfo;

    private OrderItem(
            OrderItemId id,
            InternalProductReference internalProduct,
            ExternalProductSnapshot externalProduct,
            ExternalOrderItemPrice price,
            ReceiverInfo receiverInfo,
            PaymentShipmentInfo shipmentInfo,
            SettlementInfo settlementInfo) {
        this.id = id;
        this.internalProduct = internalProduct;
        this.externalProduct = externalProduct;
        this.price = price;
        this.receiverInfo = receiverInfo;
        this.shipmentInfo = shipmentInfo;
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
                null,
                null);
    }

    public static OrderItem reconstitute(
            OrderItemId id,
            InternalProductReference internalProduct,
            ExternalProductSnapshot externalProduct,
            ExternalOrderItemPrice price,
            ReceiverInfo receiverInfo,
            PaymentShipmentInfo shipmentInfo,
            SettlementInfo settlementInfo) {
        return new OrderItem(
                id,
                internalProduct,
                externalProduct,
                price,
                receiverInfo,
                shipmentInfo,
                settlementInfo);
    }

    public void updateShipmentInfo(PaymentShipmentInfo shipmentInfo) {
        this.shipmentInfo = shipmentInfo;
    }

    public void updateSettlementInfo(SettlementInfo settlementInfo) {
        this.settlementInfo = settlementInfo;
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

    public ReceiverInfo receiverInfo() {
        return receiverInfo;
    }

    public PaymentShipmentInfo shipmentInfo() {
        return shipmentInfo;
    }

    public SettlementInfo settlementInfo() {
        return settlementInfo;
    }
}
