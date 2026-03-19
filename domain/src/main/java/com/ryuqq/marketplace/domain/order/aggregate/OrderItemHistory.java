package com.ryuqq.marketplace.domain.order.aggregate;

import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import java.time.Instant;

/** 주문상품 변경 이력. 누가, 언제, 어떻게 변경했는지 기록합니다. */
public class OrderItemHistory {

    private final Long id;
    private final OrderItemId orderItemId;
    private final OrderItemStatus fromStatus;
    private final OrderItemStatus toStatus;
    private final String changedBy;
    private final String reason;
    private final Instant changedAt;

    private OrderItemHistory(
            Long id,
            OrderItemId orderItemId,
            OrderItemStatus fromStatus,
            OrderItemStatus toStatus,
            String changedBy,
            String reason,
            Instant changedAt) {
        this.id = id;
        this.orderItemId = orderItemId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.reason = reason;
        this.changedAt = changedAt;
    }

    public static OrderItemHistory of(
            OrderItemId orderItemId,
            OrderItemStatus fromStatus,
            OrderItemStatus toStatus,
            String changedBy,
            String reason,
            Instant changedAt) {
        return new OrderItemHistory(
                null, orderItemId, fromStatus, toStatus, changedBy, reason, changedAt);
    }

    public static OrderItemHistory reconstitute(
            Long id,
            OrderItemId orderItemId,
            OrderItemStatus fromStatus,
            OrderItemStatus toStatus,
            String changedBy,
            String reason,
            Instant changedAt) {
        return new OrderItemHistory(
                id, orderItemId, fromStatus, toStatus, changedBy, reason, changedAt);
    }

    public Long id() {
        return id;
    }

    public OrderItemId orderItemId() {
        return orderItemId;
    }

    public String orderItemIdValue() {
        return orderItemId.value();
    }

    public OrderItemStatus fromStatus() {
        return fromStatus;
    }

    public OrderItemStatus toStatus() {
        return toStatus;
    }

    public String changedBy() {
        return changedBy;
    }

    public String reason() {
        return reason;
    }

    public Instant changedAt() {
        return changedAt;
    }
}
