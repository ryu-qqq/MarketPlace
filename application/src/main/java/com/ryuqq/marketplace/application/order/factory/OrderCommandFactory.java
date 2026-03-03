package com.ryuqq.marketplace.application.order.factory;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.order.dto.command.CreateOrderCommand;
import com.ryuqq.marketplace.application.order.dto.command.CreateOrderItemCommand;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.common.vo.Email;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.id.OrderNumber;
import com.ryuqq.marketplace.domain.order.vo.BuyerInfo;
import com.ryuqq.marketplace.domain.order.vo.BuyerName;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderItemPrice;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderReference;
import com.ryuqq.marketplace.domain.order.vo.ExternalProductSnapshot;
import com.ryuqq.marketplace.domain.order.vo.InternalProductReference;
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

        BuyerInfo buyerInfo =
                BuyerInfo.of(
                        BuyerName.of(command.buyerName()),
                        Email.of(command.buyerEmail()),
                        PhoneNumber.of(command.buyerPhone()));

        ExternalOrderReference externalOrderRef =
                ExternalOrderReference.of(
                        command.salesChannelId(),
                        command.shopId(),
                        command.externalOrderNo(),
                        command.externalOrderedAt());

        List<OrderItem> items = command.items().stream().map(this::createOrderItem).toList();

        return Order.forNew(
                orderId, orderNumber, buyerInfo, externalOrderRef, items, command.changedBy(), now);
    }

    /**
     * 단순 상태 전이 컨텍스트 생성.
     *
     * @param orderId 주문 ID 문자열
     * @return StatusChangeContext
     */
    public StatusChangeContext<OrderId> createStatusContext(String orderId) {
        return new StatusChangeContext<>(OrderId.of(orderId), timeProvider.now());
    }

    /**
     * 사유 포함 상태 전이 컨텍스트 생성.
     *
     * @param orderId 주문 ID 문자열
     * @param reason 변경 사유
     * @param changedBy 변경자
     * @return OrderStatusChangeWithReasonContext
     */
    public OrderStatusChangeWithReasonContext createStatusContextWithReason(
            String orderId, String reason, String changedBy) {
        return new OrderStatusChangeWithReasonContext(
                OrderId.of(orderId), reason, changedBy, timeProvider.now());
    }

    /**
     * 사유 포함 상태 변경 컨텍스트.
     *
     * @param orderId 주문 ID
     * @param reason 변경 사유
     * @param changedBy 변경자
     * @param changedAt 변경 시간
     */
    public record OrderStatusChangeWithReasonContext(
            OrderId orderId, String reason, String changedBy, Instant changedAt) {}

    private OrderItem createOrderItem(CreateOrderItemCommand cmd) {
        InternalProductReference internalProduct =
                InternalProductReference.of(
                        cmd.productGroupId(),
                        cmd.productId(),
                        cmd.sellerId(),
                        cmd.brandId(),
                        cmd.skuCode());

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

        return OrderItem.forNew(internalProduct, externalProduct, price, receiverInfo);
    }
}
