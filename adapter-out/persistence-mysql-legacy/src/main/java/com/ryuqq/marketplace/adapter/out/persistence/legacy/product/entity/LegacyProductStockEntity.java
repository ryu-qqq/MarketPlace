package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyProductStockEntity - 레거시 상품 재고 엔티티.
 *
 * <p>레거시 DB의 product_stock 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "product_stock")
public class LegacyProductStockEntity extends LegacyBaseEntity {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "STOCK_QUANTITY")
    private Integer stockQuantity;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyProductStockEntity() {}

    private LegacyProductStockEntity(Long productId, Integer stockQuantity) {
        this.productId = productId;
        this.stockQuantity = stockQuantity;
        this.deleteYn = "N";
    }

    private LegacyProductStockEntity(Long productId, Integer stockQuantity, String deleteYn) {
        this.productId = productId;
        this.stockQuantity = stockQuantity;
        this.deleteYn = deleteYn;
    }

    public static LegacyProductStockEntity create(long productId, int stockQuantity) {
        return new LegacyProductStockEntity(productId, stockQuantity);
    }

    public static LegacyProductStockEntity create(
            long productId, int stockQuantity, String deleteYn) {
        return new LegacyProductStockEntity(productId, stockQuantity, deleteYn);
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
