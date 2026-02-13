package com.ryuqq.marketplace.domain.order.aggregate;

import com.ryuqq.marketplace.domain.order.id.OrderHistoryId;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
import java.time.Instant;

/** 주문 변경 이력. 누가, 언제, 어떻게 변경했는지 기록합니다. */
public class OrderHistory {

    private final OrderHistoryId id;
    private final OrderId orderId;
    private final OrderStatus fromStatus;
    private final OrderStatus toStatus;
    private final String changedBy;
    private final String reason;
    private final Instant changedAt;

    private OrderHistory(
            OrderHistoryId id,
            OrderId orderId,
            OrderStatus fromStatus,
            OrderStatus toStatus,
            String changedBy,
            String reason,
            Instant changedAt) {
        this.id = id;
        this.orderId = orderId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.reason = reason;
        this.changedAt = changedAt;
    }

    public static OrderHistory of(
            OrderId orderId,
            OrderStatus fromStatus,
            OrderStatus toStatus,
            String changedBy,
            String reason,
            Instant changedAt) {
        return new OrderHistory(
                OrderHistoryId.forNew(),
                orderId,
                fromStatus,
                toStatus,
                changedBy,
                reason,
                changedAt);
    }

    public static OrderHistory reconstitute(
            OrderHistoryId id,
            OrderId orderId,
            OrderStatus fromStatus,
            OrderStatus toStatus,
            String changedBy,
            String reason,
            Instant changedAt) {
        return new OrderHistory(id, orderId, fromStatus, toStatus, changedBy, reason, changedAt);
    }

    public OrderHistoryId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public OrderId orderId() {
        return orderId;
    }

    public OrderStatus fromStatus() {
        return fromStatus;
    }

    public OrderStatus toStatus() {
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
