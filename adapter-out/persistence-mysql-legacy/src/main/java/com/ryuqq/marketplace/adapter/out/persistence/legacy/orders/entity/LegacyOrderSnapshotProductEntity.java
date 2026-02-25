package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyOrderSnapshotProductEntity - 주문 스냅샷 상품 엔티티.
 *
 * <p>레거시 DB의 order_snapshot_product 테이블 매핑.
 */
@Entity
@Table(name = "order_snapshot_product")
public class LegacyOrderSnapshotProductEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_snapshot_product_id")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "PRODUCT_GROUP_ID")
    private Long productGroupId;

    @Column(name = "SOLD_OUT_YN")
    private String soldOutYn;

    @Column(name = "DISPLAY_YN")
    private String displayYn;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOrderSnapshotProductEntity() {}

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public String getSoldOutYn() {
        return soldOutYn;
    }

    public String getDisplayYn() {
        return displayYn;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
