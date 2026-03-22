package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyProductEntity - 레거시 상품 엔티티.
 *
 * <p>레거시 DB의 product 테이블 매핑. stock_quantity 포함.
 */
@Entity
@Table(name = "product")
public class LegacyProductEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "PRODUCT_GROUP_ID")
    private Long productGroupId;

    @Column(name = "SOLD_OUT_YN")
    private String soldOutYn;

    @Column(name = "DISPLAY_YN")
    private String displayYn;

    @Column(name = "STOCK_QUANTITY")
    private Integer stockQuantity;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyProductEntity() {}

    private LegacyProductEntity(
            Long id,
            Long productGroupId,
            String soldOutYn,
            String displayYn,
            Integer stockQuantity,
            String deleteYn) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.soldOutYn = soldOutYn;
        this.displayYn = displayYn;
        this.stockQuantity = stockQuantity;
        this.deleteYn = deleteYn;
    }

    public static LegacyProductEntity create(
            long productGroupId, String soldOutYn, String displayYn, int stockQuantity) {
        return new LegacyProductEntity(
                null, productGroupId, soldOutYn, displayYn, stockQuantity, "N");
    }

    public static LegacyProductEntity create(
            Long id,
            long productGroupId,
            String soldOutYn,
            String displayYn,
            int stockQuantity,
            String deleteYn) {
        return new LegacyProductEntity(
                id, productGroupId, soldOutYn, displayYn, stockQuantity, deleteYn);
    }

    public Long getId() {
        return id;
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

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
