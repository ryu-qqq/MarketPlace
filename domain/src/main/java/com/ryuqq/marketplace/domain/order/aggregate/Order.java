package com.ryuqq.marketplace.domain.order.aggregate;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.event.OrderCreatedEvent;
import com.ryuqq.marketplace.domain.order.exception.OrderErrorCode;
import com.ryuqq.marketplace.domain.order.exception.OrderException;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.id.OrderNumber;
import com.ryuqq.marketplace.domain.order.vo.BuyerInfo;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderReference;
import com.ryuqq.marketplace.domain.order.vo.PaymentInfo;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 주문 Aggregate Root. 묶음 컨테이너 역할만 담당하며 상태는 관리하지 않습니다. */
public class Order {

    private final OrderId id;
    private final OrderNumber orderNumber;
    private final BuyerInfo buyerInfo;
    private final PaymentInfo paymentInfo;
    private final ExternalOrderReference externalOrderReference;
    private final Instant createdAt;
    private Instant updatedAt;

    private final List<OrderItem> items = new ArrayList<>();
    private final List<DomainEvent> events = new ArrayList<>();

    private Order(
            OrderId id,
            OrderNumber orderNumber,
            BuyerInfo buyerInfo,
            PaymentInfo paymentInfo,
            ExternalOrderReference externalOrderReference,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.buyerInfo = buyerInfo;
        this.paymentInfo = paymentInfo;
        this.externalOrderReference = externalOrderReference;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Order forNew(
            OrderId id,
            OrderNumber orderNumber,
            BuyerInfo buyerInfo,
            PaymentInfo paymentInfo,
            ExternalOrderReference externalOrderReference,
            List<OrderItem> items,
            Instant now) {
        Order order =
                new Order(
                        id,
                        orderNumber,
                        buyerInfo,
                        paymentInfo,
                        externalOrderReference,
                        now,
                        now);
        if (items == null || items.isEmpty()) {
            throw new OrderException(OrderErrorCode.EMPTY_ORDER_ITEMS, "주문 상품은 최소 1개 이상이어야 합니다");
        }
        order.items.addAll(items);
        order.registerEvent(new OrderCreatedEvent(id, orderNumber, now));
        return order;
    }

    public static Order reconstitute(
            OrderId id,
            OrderNumber orderNumber,
            BuyerInfo buyerInfo,
            PaymentInfo paymentInfo,
            ExternalOrderReference externalOrderReference,
            Instant createdAt,
            Instant updatedAt,
            List<OrderItem> items) {
        Order order =
                new Order(
                        id,
                        orderNumber,
                        buyerInfo,
                        paymentInfo,
                        externalOrderReference,
                        createdAt,
                        updatedAt);
        if (items != null) {
            order.items.addAll(items);
        }
        return order;
    }

    protected void registerEvent(DomainEvent event) {
        this.events.add(event);
    }

    public List<DomainEvent> pollEvents() {
        List<DomainEvent> polled = new ArrayList<>(this.events);
        this.events.clear();
        return Collections.unmodifiableList(polled);
    }

    public OrderId id() {
        return id;
    }

    public String idValue() {
        return id.value();
    }

    public OrderNumber orderNumber() {
        return orderNumber;
    }

    public String orderNumberValue() {
        return orderNumber.value();
    }

    public BuyerInfo buyerInfo() {
        return buyerInfo;
    }

    public PaymentInfo paymentInfo() {
        return paymentInfo;
    }

    public ExternalOrderReference externalOrderReference() {
        return externalOrderReference;
    }

    public Instant orderedAt() {
        return externalOrderReference.externalOrderedAt();
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public List<OrderItem> items() {
        return Collections.unmodifiableList(items);
    }
}
