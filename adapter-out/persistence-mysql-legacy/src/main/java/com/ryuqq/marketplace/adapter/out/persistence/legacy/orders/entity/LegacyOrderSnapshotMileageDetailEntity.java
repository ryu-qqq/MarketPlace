package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyOrderSnapshotMileageDetailEntity - 주문 스냅샷 마일리지 상세 엔티티.
 *
 * <p>레거시 DB의 order_snapshot_mileage_detail 테이블 매핑.
 */
@Entity
@Table(name = "order_snapshot_mileage_detail")
public class LegacyOrderSnapshotMileageDetailEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_snapshot_mileage_detail_id")
    private Long id;

    @Column(name = "ORDER_SNAPSHOT_MILEAGE_ID")
    private Long orderSnapshotMileageId;

    @Column(name = "MILEAGE_ID")
    private Long mileageId;

    @Column(name = "USED_AMOUNT")
    private Long usedAmount;

    @Column(name = "MILEAGE_BALANCE")
    private Long mileageBalance;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOrderSnapshotMileageDetailEntity() {}

    public Long getId() {
        return id;
    }

    public Long getOrderSnapshotMileageId() {
        return orderSnapshotMileageId;
    }

    public Long getMileageId() {
        return mileageId;
    }

    public Long getUsedAmount() {
        return usedAmount;
    }

    public Long getMileageBalance() {
        return mileageBalance;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
