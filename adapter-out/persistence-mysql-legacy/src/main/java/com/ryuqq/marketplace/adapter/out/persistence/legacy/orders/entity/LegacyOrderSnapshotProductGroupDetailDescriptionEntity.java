package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyOrderSnapshotProductGroupDetailDescriptionEntity - 주문 스냅샷 상세설명 엔티티.
 *
 * <p>레거시 DB의 order_snapshot_product_group_detail_description 테이블 매핑.
 */
@Entity
@Table(name = "order_snapshot_product_group_detail_description")
public class LegacyOrderSnapshotProductGroupDetailDescriptionEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_snapshot_product_group_detail_description_id")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "PRODUCT_GROUP_ID")
    private Long productGroupId;

    @Column(name = "PRODUCT_GROUP_IMAGE_TYPE")
    private String productGroupImageType;

    @Column(name = "IMAGE_URL", columnDefinition = "MEDIUMTEXT")
    private String imageUrl;

    @Column(name = "ORIGIN_URL")
    private String originUrl;

    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOrderSnapshotProductGroupDetailDescriptionEntity() {}

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

    public String getOriginUrl() {
        return originUrl;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
