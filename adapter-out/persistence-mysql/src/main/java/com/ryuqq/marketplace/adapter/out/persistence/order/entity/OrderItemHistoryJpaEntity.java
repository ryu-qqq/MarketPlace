package com.ryuqq.marketplace.adapter.out.persistence.order.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** OrderItemHistory JPA 엔티티. order_item_histories 테이블에 매핑됩니다. */
@Entity
@Table(name = "order_item_histories")
public class OrderItemHistoryJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    @Column(name = "from_status", length = 30)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 30)
    private String toStatus;

    @Column(name = "changed_by", nullable = false, length = 100)
    private String changedBy;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    protected OrderItemHistoryJpaEntity() {
        super();
    }

    private OrderItemHistoryJpaEntity(
            Long id,
            Long orderItemId,
            String fromStatus,
            String toStatus,
            String changedBy,
            String reason,
            int quantity,
            Instant changedAt,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.orderItemId = orderItemId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.reason = reason;
        this.quantity = quantity;
        this.changedAt = changedAt;
    }

    public static OrderItemHistoryJpaEntity create(
            Long id,
            Long orderItemId,
            String fromStatus,
            String toStatus,
            String changedBy,
            String reason,
            int quantity,
            Instant changedAt,
            Instant createdAt,
            Instant updatedAt) {
        return new OrderItemHistoryJpaEntity(
                id,
                orderItemId,
                fromStatus,
                toStatus,
                changedBy,
                reason,
                quantity,
                changedAt,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public String getReason() {
        return reason;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public int getQuantity() {
        return quantity;
    }
}
