package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyOrderSnapshotProductOptionEntity - 주문 스냅샷 상품 옵션 엔티티.
 *
 * <p>레거시 DB의 order_snapshot_product_option 테이블 매핑.
 */
@Entity
@Table(name = "order_snapshot_product_option")
public class LegacyOrderSnapshotProductOptionEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_snapshot_product_option_id")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "PRODUCT_OPTION_ID")
    private Long productOptionId;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "OPTION_GROUP_ID")
    private Long optionGroupId;

    @Column(name = "OPTION_DETAIL_ID")
    private String optionDetailId;

    @Column(name = "ADDITIONAL_PRICE")
    private Long additionalPrice;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOrderSnapshotProductOptionEntity() {}

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductOptionId() {
        return productOptionId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getOptionGroupId() {
        return optionGroupId;
    }

    public String getOptionDetailId() {
        return optionDetailId;
    }

    public Long getAdditionalPrice() {
        return additionalPrice;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
