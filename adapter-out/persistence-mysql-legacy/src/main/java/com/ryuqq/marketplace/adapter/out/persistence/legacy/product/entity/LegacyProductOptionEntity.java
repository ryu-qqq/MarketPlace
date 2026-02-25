package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyProductOptionEntity - 레거시 상품 옵션 엔티티.
 *
 * <p>레거시 DB의 product_option 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "product_option")
public class LegacyProductOptionEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_option_id")
    private Long id;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "OPTION_GROUP_ID")
    private Long optionGroupId;

    @Column(name = "OPTION_DETAIL_ID")
    private Long optionDetailId;

    @Column(name = "ADDITIONAL_PRICE")
    private Long additionalPrice;

    @Column(name = "DELETE_YN")
    private String deleteYn;

    protected LegacyProductOptionEntity() {}

    private LegacyProductOptionEntity(
            Long productId, Long optionGroupId, Long optionDetailId, Long additionalPrice) {
        this.productId = productId;
        this.optionGroupId = optionGroupId;
        this.optionDetailId = optionDetailId;
        this.additionalPrice = additionalPrice;
        this.deleteYn = "N";
    }

    private LegacyProductOptionEntity(
            Long id,
            Long productId,
            Long optionGroupId,
            Long optionDetailId,
            Long additionalPrice,
            String deleteYn) {
        this.id = id;
        this.productId = productId;
        this.optionGroupId = optionGroupId;
        this.optionDetailId = optionDetailId;
        this.additionalPrice = additionalPrice;
        this.deleteYn = deleteYn;
    }

    public static LegacyProductOptionEntity create(
            long productId, long optionGroupId, long optionDetailId, long additionalPrice) {
        return new LegacyProductOptionEntity(
                productId, optionGroupId, optionDetailId, additionalPrice);
    }

    public static LegacyProductOptionEntity create(
            Long id,
            long productId,
            long optionGroupId,
            long optionDetailId,
            long additionalPrice,
            String deleteYn) {
        return new LegacyProductOptionEntity(
                id, productId, optionGroupId, optionDetailId, additionalPrice, deleteYn);
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getOptionGroupId() {
        return optionGroupId;
    }

    public Long getOptionDetailId() {
        return optionDetailId;
    }

    public Long getAdditionalPrice() {
        return additionalPrice;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
