package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyOrderSnapshotMileageEntity - 주문 스냅샷 마일리지 엔티티.
 *
 * <p>레거시 DB의 order_snapshot_mileage 테이블 매핑.
 */
@Entity
@Table(name = "order_snapshot_mileage")
public class LegacyOrderSnapshotMileageEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_snapshot_mileage_id")
    private Long id;

    @Column(name = "PAYMENT_ID")
    private Long paymentId;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOrderSnapshotMileageEntity() {}

    public Long getId() {
        return id;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
