package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyOrderSnapshotOptionDetailEntity - 주문 스냅샷 옵션 상세 엔티티.
 *
 * <p>레거시 DB의 order_snapshot_option_detail 테이블 매핑.
 */
@Entity
@Table(name = "order_snapshot_option_detail")
public class LegacyOrderSnapshotOptionDetailEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_snapshot_option_detail_id")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "OPTION_DETAIL_ID")
    private Long optionDetailId;

    @Column(name = "OPTION_GROUP_ID")
    private Long optionGroupId;

    @Column(name = "OPTION_VALUE")
    private String optionValue;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOrderSnapshotOptionDetailEntity() {}

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getOptionDetailId() {
        return optionDetailId;
    }

    public Long getOptionGroupId() {
        return optionGroupId;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
