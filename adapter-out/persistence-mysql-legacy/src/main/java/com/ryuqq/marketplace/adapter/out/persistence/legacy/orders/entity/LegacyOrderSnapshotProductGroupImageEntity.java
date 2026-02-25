package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyOrderSnapshotProductGroupImageEntity - 주문 스냅샷 상품그룹 이미지 엔티티.
 *
 * <p>레거시 DB의 order_snapshot_product_group_image 테이블 매핑.
 */
@Entity
@Table(name = "order_snapshot_product_group_image")
public class LegacyOrderSnapshotProductGroupImageEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_snapshot_product_group_image_id")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "PRODUCT_GROUP_ID")
    private Long productGroupId;

    @Column(name = "PRODUCT_GROUP_IMAGE_TYPE")
    private String productGroupImageType;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder;

    @Column(name = "origin_url")
    private String originUrl;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOrderSnapshotProductGroupImageEntity() {}

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public String getProductGroupImageType() {
        return productGroupImageType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
