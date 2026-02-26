package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyOrderSnapshotProductGroupEntity - 주문 스냅샷 상품그룹 엔티티.
 *
 * <p>레거시 DB의 order_snapshot_product_group 테이블 매핑.
 */
@Entity
@Table(name = "order_snapshot_product_group")
public class LegacyOrderSnapshotProductGroupEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_snapshot_product_group_id")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "PRODUCT_GROUP_ID")
    private Long productGroupId;

    @Column(name = "PRODUCT_GROUP_NAME")
    private String productGroupName;

    @Column(name = "SELLER_ID")
    private String sellerId;

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

    @Column(name = "MANAGEMENT_TYPE")
    private String managementType;

    @Column(name = "COMMISSION_RATE")
    private Long commissionRate;

    @Column(name = "SHARE_RATIO")
    private Long shareRatio;

    @Column(name = "STYLE_CODE")
    private String styleCode;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOrderSnapshotProductGroupEntity() {}

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public String getProductGroupName() {
        return productGroupName;
    }

    public String getSellerId() {
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

    public String getManagementType() {
        return managementType;
    }

    public Long getCommissionRate() {
        return commissionRate;
    }

    public Long getShareRatio() {
        return shareRatio;
    }

    public String getStyleCode() {
        return styleCode;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
