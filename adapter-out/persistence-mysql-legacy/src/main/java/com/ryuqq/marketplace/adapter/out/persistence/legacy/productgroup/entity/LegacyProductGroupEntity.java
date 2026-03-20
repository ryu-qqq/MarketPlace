package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyProductGroupEntity - 레거시 상품그룹 엔티티.
 *
 * <p>레거시 DB의 product_group 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "product_group")
public class LegacyProductGroupEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_group_id")
    private Long id;

    @Column(name = "EXTERNAL_PRODUCT_UUID")
    private String externalProductUuid;

    @Column(name = "PRODUCT_GROUP_NAME")
    private String productGroupName;

    @Column(name = "SELLER_ID")
    private Long sellerId;

    @Column(name = "BRAND_ID")
    private Long brandId;

    @Column(name = "CATEGORY_ID")
    private Long categoryId;

    @Column(name = "OPTION_TYPE")
    private String optionType;

    @Column(name = "REGULAR_PRICE")
    private Long regularPrice;

    @Column(name = "CURRENT_PRICE")
    private Long currentPrice;

    @Column(name = "SALE_PRICE")
    private Long salePrice;

    @Column(name = "DIRECT_DISCOUNT_RATE")
    private Integer directDiscountRate;

    @Column(name = "DIRECT_DISCOUNT_PRICE")
    private Long directDiscountPrice;

    @Column(name = "DISCOUNT_RATE")
    private Integer discountRate;

    @Column(name = "SOLD_OUT_YN")
    private String soldOutYn;

    @Column(name = "DISPLAY_YN")
    private String displayYn;

    @Column(name = "PRODUCT_CONDITION")
    private String productCondition;

    @Column(name = "ORIGIN")
    private String origin;

    @Column(name = "STYLE_CODE")
    private String styleCode;

    @Column(name = "MANAGEMENT_TYPE")
    private String managementType;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyProductGroupEntity() {}

    private LegacyProductGroupEntity(
            Long id,
            String productGroupName,
            Long sellerId,
            Long brandId,
            Long categoryId,
            String optionType,
            String managementType,
            Long regularPrice,
            Long currentPrice,
            String soldOutYn,
            String displayYn,
            String productCondition,
            String origin,
            String styleCode) {
        this.id = id;
        this.productGroupName = productGroupName;
        this.sellerId = sellerId;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.optionType = optionType;
        this.managementType = managementType;
        this.regularPrice = regularPrice;
        this.currentPrice = currentPrice;
        this.salePrice = currentPrice;
        this.directDiscountRate = 0;
        this.directDiscountPrice = 0L;
        this.discountRate = 0;
        this.soldOutYn = soldOutYn;
        this.displayYn = displayYn;
        this.productCondition = productCondition;
        this.origin = origin;
        this.styleCode = styleCode;
        this.deleteYn = "N";
    }

    public static LegacyProductGroupEntity create(
            Long id,
            String productGroupName,
            long sellerId,
            long brandId,
            long categoryId,
            String optionType,
            String managementType,
            long regularPrice,
            long currentPrice,
            String soldOutYn,
            String displayYn,
            String productCondition,
            String origin,
            String styleCode) {
        return new LegacyProductGroupEntity(
                id,
                productGroupName,
                sellerId,
                brandId,
                categoryId,
                optionType,
                managementType,
                regularPrice,
                currentPrice,
                soldOutYn,
                displayYn,
                productCondition,
                origin,
                styleCode);
    }

    public Long getId() {
        return id;
    }

    public String getExternalProductUuid() {
        return externalProductUuid;
    }

    public String getProductGroupName() {
        return productGroupName;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getOptionType() {
        return optionType;
    }

    public Long getRegularPrice() {
        return regularPrice;
    }

    public Long getCurrentPrice() {
        return currentPrice;
    }

    public Long getSalePrice() {
        return salePrice;
    }

    public Integer getDirectDiscountRate() {
        return directDiscountRate;
    }

    public Long getDirectDiscountPrice() {
        return directDiscountPrice;
    }

    public Integer getDiscountRate() {
        return discountRate;
    }

    public String getSoldOutYn() {
        return soldOutYn;
    }

    public String getDisplayYn() {
        return displayYn;
    }

    public String getProductCondition() {
        return productCondition;
    }

    public String getOrigin() {
        return origin;
    }

    public String getStyleCode() {
        return styleCode;
    }

    public String getManagementType() {
        return managementType;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
