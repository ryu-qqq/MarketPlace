package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyOrderSnapshotProductStockEntity - 주문 스냅샷 재고 엔티티.
 *
 * <p>레거시 DB의 order_snapshot_product_stock 테이블 매핑.
 */
@Entity
@Table(name = "order_snapshot_product_stock")
public class LegacyOrderSnapshotProductStockEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_snapshot_product_stock_id")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "PRODUCT_STOCK_ID")
    private Long productStockId;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "STOCK_QUANTITY")
    private Integer stockQuantity;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOrderSnapshotProductStockEntity() {}

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductStockId() {
        return productStockId;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
