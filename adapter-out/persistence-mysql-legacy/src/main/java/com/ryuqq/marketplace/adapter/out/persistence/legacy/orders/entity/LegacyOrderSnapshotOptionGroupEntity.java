package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyOrderSnapshotOptionGroupEntity - 주문 스냅샷 옵션 그룹 엔티티.
 *
 * <p>레거시 DB의 order_snapshot_option_group 테이블 매핑.
 */
@Entity
@Table(name = "order_snapshot_option_group")
public class LegacyOrderSnapshotOptionGroupEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_snapshot_option_group_id")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "OPTION_GROUP_ID")
    private Long optionGroupId;

    @Column(name = "OPTION_NAME")
    private String optionName;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOrderSnapshotOptionGroupEntity() {}

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getOptionGroupId() {
        return optionGroupId;
    }

    public String getOptionName() {
        return optionName;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
