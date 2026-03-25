package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyOrderHistoryEntity - 레거시 주문 이력 엔티티.
 *
 * <p>레거시 DB의 orders_history 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "orders_history")
public class LegacyOrderHistoryEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_history_id")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "CHANGE_REASON")
    private String changeReason;

    @Column(name = "CHANGE_DETAIL_REASON")
    private String changeDetailReason;

    @Column(name = "ORDER_STATUS")
    private String orderStatus;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOrderHistoryEntity() {}

    public static LegacyOrderHistoryEntity create(
            long orderId, String orderStatus, String changeReason, String changeDetailReason) {
        LegacyOrderHistoryEntity entity = new LegacyOrderHistoryEntity();
        entity.orderId = orderId;
        entity.orderStatus = orderStatus;
        entity.changeReason = changeReason;
        entity.changeDetailReason = changeDetailReason;
        entity.deleteYn = "N";
        entity.initAuditFields("SYSTEM");
        return entity;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public String getChangeDetailReason() {
        return changeDetailReason;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
