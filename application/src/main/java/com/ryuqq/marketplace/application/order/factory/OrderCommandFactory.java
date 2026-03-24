package com.ryuqq.marketplace.application.order.factory;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.order.dto.command.CreateOrderCommand;
import com.ryuqq.marketplace.application.order.dto.command.CreateOrderItemCommand;
import com.ryuqq.marketplace.application.order.dto.command.OrderItemCancelCommand;
import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;
import com.ryuqq.marketplace.application.order.dto.command.StartClaimCommand;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.common.vo.Email;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.id.OrderItemNumber;
import com.ryuqq.marketplace.domain.order.id.OrderNumber;
import com.ryuqq.marketplace.domain.order.id.PaymentNumber;
import com.ryuqq.marketplace.domain.order.vo.BuyerInfo;
import com.ryuqq.marketplace.domain.order.vo.BuyerName;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderItemPrice;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderReference;
import com.ryuqq.marketplace.domain.order.vo.ExternalProductSnapshot;
import com.ryuqq.marketplace.domain.order.vo.InternalProductReference;
import com.ryuqq.marketplace.domain.order.vo.PaymentInfo;
import com.ryuqq.marketplace.domain.order.vo.ReceiverInfo;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Order Command Factory.
 *
 * <p>Command DTO를 Domain 객체 및 Context로 변환합니다.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 */
@Component
public class OrderCommandFactory {

    private final TimeProvider timeProvider;
    private final IdGeneratorPort idGeneratorPort;

    public OrderCommandFactory(TimeProvider timeProvider, IdGeneratorPort idGeneratorPort) {
        this.timeProvider = timeProvider;
        this.idGeneratorPort = idGeneratorPort;
    }

    /**
     * 주문 생성.
     *
     * @param command 주문 생성 Command
     * @return Order 도메인 객체
     */
    public Order createOrder(CreateOrderCommand command) {
        Instant now = timeProvider.now();
        OrderId orderId = OrderId.of(idGeneratorPort.generate());
        OrderNumber orderNumber = OrderNumber.generate();

        Email buyerEmail =
                command.buyerEmail() != null && !command.buyerEmail().isBlank()
                        ? Email.of(command.buyerEmail())
                        : null;

        BuyerInfo buyerInfo =
                BuyerInfo.of(
                        BuyerName.of(command.buyerName()),
                        buyerEmail,
                        PhoneNumber.of(command.buyerPhone()));

        PaymentNumber paymentNumber = PaymentNumber.generate();
        PaymentInfo paymentInfo =
                PaymentInfo.of(
                        paymentNumber,
                        command.paymentMethod(),
                        Money.of(command.totalPaymentAmount()),
                        command.paidAt());

        ExternalOrderReference externalOrderRef =
                ExternalOrderReference.of(
                        command.salesChannelId(),
                        command.shopId(),
                        command.shopCode(),
                        command.shopName(),
                        command.externalOrderNo(),
                        command.externalOrderedAt());

        List<OrderItem> items = new java.util.ArrayList<>();
        for (int i = 0; i < command.items().size(); i++) {
            items.add(createOrderItem(command.items().get(i), orderNumber, i + 1));
        }

        return Order.forNew(
                orderId, orderNumber, buyerInfo, paymentInfo, externalOrderRef, items, now);
    }

    /** 취소 Command → StatusChangeContext 생성. */
    public StatusChangeContext<List<OrderItemId>> createCancelContext(
            OrderItemCancelCommand command) {
        return new StatusChangeContext<>(
                toOrderItemIds(command.orderItemIds()), timeProvider.now());
    }

    /** 상태 변경 Command → StatusChangeContext 생성. (확정/발주/출고/배송완료/교환완료/환불완료) */
    public StatusChangeContext<List<OrderItemId>> createStatusChangeContext(
            OrderItemStatusCommand command) {
        return new StatusChangeContext<>(
                toOrderItemIds(command.orderItemIds()), timeProvider.now());
    }

    /** 클레임 시작 Command → StatusChangeContext 생성. */
    public StatusChangeContext<List<OrderItemId>> createClaimContext(StartClaimCommand command) {
        return new StatusChangeContext<>(
                toOrderItemIds(command.orderItemIds()), timeProvider.now());
    }

    private List<OrderItemId> toOrderItemIds(List<String> ids) {
        return ids.stream().map(OrderItemId::of).toList();
    }

    private OrderItem createOrderItem(
            CreateOrderItemCommand cmd, OrderNumber orderNumber, int sequence) {
        InternalProductReference internalProduct =
                InternalProductReference.of(
                        cmd.productGroupId(),
                        cmd.productId(),
                        cmd.sellerId(),
                        cmd.brandId(),
                        cmd.skuCode(),
                        cmd.productGroupName(),
                        cmd.brandName(),
                        cmd.sellerName(),
                        cmd.mainImageUrl());

        ExternalProductSnapshot externalProduct =
                ExternalProductSnapshot.of(
                        cmd.externalProductId(),
                        cmd.externalOptionId(),
                        cmd.externalProductName(),
                        cmd.externalOptionName(),
                        cmd.externalImageUrl());

        ExternalOrderItemPrice price =
                ExternalOrderItemPrice.of(
                        Money.of(cmd.unitPrice()),
                        cmd.quantity(),
                        Money.of(cmd.totalAmount()),
                        Money.of(cmd.discountAmount()),
                        Money.of(cmd.sellerBurdenDiscountAmount()),
                        Money.of(cmd.paymentAmount()));

        ReceiverInfo receiverInfo =
                ReceiverInfo.of(
                        cmd.receiverName(),
                        PhoneNumber.of(cmd.receiverPhone()),
                        Address.of(
                                cmd.receiverZipCode(),
                                cmd.receiverAddress(),
                                cmd.receiverAddressDetail()),
                        cmd.deliveryRequest());

        return OrderItem.forNew(
                OrderItemId.forNew(idGeneratorPort.generate()),
                OrderItemNumber.generate(orderNumber, sequence),
                internalProduct,
                externalProduct,
                price,
                receiverInfo,
                null);
    }
}
