package com.ryuqq.marketplace.domain.order.aggregate;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.event.OrderCancelledEvent;
import com.ryuqq.marketplace.domain.order.event.OrderClaimStartedEvent;
import com.ryuqq.marketplace.domain.order.event.OrderConfirmedEvent;
import com.ryuqq.marketplace.domain.order.event.OrderCreatedEvent;
import com.ryuqq.marketplace.domain.order.event.OrderDeliveredEvent;
import com.ryuqq.marketplace.domain.order.event.OrderExchangeCompletedEvent;
import com.ryuqq.marketplace.domain.order.event.OrderPreparedEvent;
import com.ryuqq.marketplace.domain.order.event.OrderRefundCompletedEvent;
import com.ryuqq.marketplace.domain.order.event.OrderShippedEvent;
import com.ryuqq.marketplace.domain.order.event.OrderStatusChangedEvent;
import com.ryuqq.marketplace.domain.order.exception.OrderErrorCode;
import com.ryuqq.marketplace.domain.order.exception.OrderException;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.id.OrderNumber;
import com.ryuqq.marketplace.domain.order.vo.BuyerInfo;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderReference;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 주문 Aggregate Root. */
public class Order {

    private final OrderId id;
    private final OrderNumber orderNumber;
    private OrderStatus status;
    private final BuyerInfo buyerInfo;
    private final ExternalOrderReference externalOrderReference;
    private final Instant createdAt;
    private Instant updatedAt;

    private final List<OrderItem> items = new ArrayList<>();
    private final List<OrderHistory> histories = new ArrayList<>();
    private final List<DomainEvent> events = new ArrayList<>();

    private Order(
            OrderId id,
            OrderNumber orderNumber,
            OrderStatus status,
            BuyerInfo buyerInfo,
            ExternalOrderReference externalOrderReference,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.buyerInfo = buyerInfo;
        this.externalOrderReference = externalOrderReference;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Order forNew(
            OrderId id,
            OrderNumber orderNumber,
            BuyerInfo buyerInfo,
            ExternalOrderReference externalOrderReference,
            List<OrderItem> items,
            String changedBy,
            Instant now) {
        Order order =
                new Order(
                        id,
                        orderNumber,
                        OrderStatus.ORDERED,
                        buyerInfo,
                        externalOrderReference,
                        now,
                        now);
        if (items == null || items.isEmpty()) {
            throw new OrderException(OrderErrorCode.EMPTY_ORDER_ITEMS, "주문 상품은 최소 1개 이상이어야 합니다");
        }
        order.items.addAll(items);
        order.addHistory(null, OrderStatus.ORDERED, changedBy, null, now);
        order.registerEvent(new OrderCreatedEvent(id, orderNumber, now));
        return order;
    }

    public static Order reconstitute(
            OrderId id,
            OrderNumber orderNumber,
            OrderStatus status,
            BuyerInfo buyerInfo,
            ExternalOrderReference externalOrderReference,
            Instant createdAt,
            Instant updatedAt,
            List<OrderItem> items,
            List<OrderHistory> histories) {
        Order order =
                new Order(
                        id,
                        orderNumber,
                        status,
                        buyerInfo,
                        externalOrderReference,
                        createdAt,
                        updatedAt);
        if (items != null) {
            order.items.addAll(items);
        }
        if (histories != null) {
            order.histories.addAll(histories);
        }
        return order;
    }

    public void prepare(String changedBy, Instant now) {
        OrderStatus from = this.status;
        validateTransition(OrderStatus.PREPARING);
        this.status = OrderStatus.PREPARING;
        this.updatedAt = now;
        addHistory(from, OrderStatus.PREPARING, changedBy, null, now);
        registerEvent(new OrderPreparedEvent(id, now));
        registerEvent(new OrderStatusChangedEvent(id, from, OrderStatus.PREPARING, now));
    }

    public void ship(String changedBy, Instant now) {
        OrderStatus from = this.status;
        validateTransition(OrderStatus.SHIPPED);
        this.status = OrderStatus.SHIPPED;
        this.updatedAt = now;
        addHistory(from, OrderStatus.SHIPPED, changedBy, null, now);
        registerEvent(new OrderShippedEvent(id, now));
        registerEvent(new OrderStatusChangedEvent(id, from, OrderStatus.SHIPPED, now));
    }

    public void deliver(String changedBy, Instant now) {
        OrderStatus from = this.status;
        validateTransition(OrderStatus.DELIVERED);
        this.status = OrderStatus.DELIVERED;
        this.updatedAt = now;
        addHistory(from, OrderStatus.DELIVERED, changedBy, null, now);
        registerEvent(new OrderDeliveredEvent(id, now));
        registerEvent(new OrderStatusChangedEvent(id, from, OrderStatus.DELIVERED, now));
    }

    public void confirm(String changedBy, Instant now) {
        OrderStatus from = this.status;
        validateTransition(OrderStatus.CONFIRMED);
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = now;
        addHistory(from, OrderStatus.CONFIRMED, changedBy, null, now);
        registerEvent(new OrderConfirmedEvent(id, now));
        registerEvent(new OrderStatusChangedEvent(id, from, OrderStatus.CONFIRMED, now));
    }

    public void cancel(String changedBy, String reason, Instant now) {
        OrderStatus from = this.status;
        validateTransition(OrderStatus.CANCELLED);
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = now;
        addHistory(from, OrderStatus.CANCELLED, changedBy, reason, now);
        registerEvent(new OrderCancelledEvent(id, now));
        registerEvent(new OrderStatusChangedEvent(id, from, OrderStatus.CANCELLED, now));
    }

    public void startClaim(String changedBy, String reason, Instant now) {
        OrderStatus from = this.status;
        validateTransition(OrderStatus.CLAIM_IN_PROGRESS);
        this.status = OrderStatus.CLAIM_IN_PROGRESS;
        this.updatedAt = now;
        addHistory(from, OrderStatus.CLAIM_IN_PROGRESS, changedBy, reason, now);
        registerEvent(new OrderClaimStartedEvent(id, now));
        registerEvent(new OrderStatusChangedEvent(id, from, OrderStatus.CLAIM_IN_PROGRESS, now));
    }

    public void completeRefund(String changedBy, Instant now) {
        OrderStatus from = this.status;
        validateTransition(OrderStatus.REFUNDED);
        this.status = OrderStatus.REFUNDED;
        this.updatedAt = now;
        addHistory(from, OrderStatus.REFUNDED, changedBy, null, now);
        registerEvent(new OrderRefundCompletedEvent(id, now));
        registerEvent(new OrderStatusChangedEvent(id, from, OrderStatus.REFUNDED, now));
    }

    public void completeExchange(String changedBy, Instant now) {
        OrderStatus from = this.status;
        validateTransition(OrderStatus.EXCHANGED);
        this.status = OrderStatus.EXCHANGED;
        this.updatedAt = now;
        addHistory(from, OrderStatus.EXCHANGED, changedBy, null, now);
        registerEvent(new OrderExchangeCompletedEvent(id, now));
        registerEvent(new OrderStatusChangedEvent(id, from, OrderStatus.EXCHANGED, now));
    }

    private void addHistory(
            OrderStatus from, OrderStatus to, String changedBy, String reason, Instant changedAt) {
        this.histories.add(OrderHistory.of(this.id, from, to, changedBy, reason, changedAt));
    }

    protected void registerEvent(DomainEvent event) {
        this.events.add(event);
    }

    public List<DomainEvent> pollEvents() {
        List<DomainEvent> polled = new ArrayList<>(this.events);
        this.events.clear();
        return Collections.unmodifiableList(polled);
    }

    private void validateTransition(OrderStatus target) {
        if (!this.status.canTransitionTo(target)) {
            throw new OrderException(
                    OrderErrorCode.INVALID_STATUS_TRANSITION,
                    String.format("%s 상태에서 %s 상태로 변경할 수 없습니다", this.status, target));
        }
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

    public OrderStatus status() {
        return status;
    }

    public BuyerInfo buyerInfo() {
        return buyerInfo;
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

    public List<OrderHistory> histories() {
        return Collections.unmodifiableList(histories);
    }
}
