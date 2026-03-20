package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyProductGroupImageEntity - 레거시 상품그룹 이미지 엔티티.
 *
 * <p>레거시 DB의 product_group_image 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "product_group_image")
public class LegacyProductGroupImageEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_group_image_id")
    private Long id;

    @Column(name = "PRODUCT_GROUP_ID")
    private Long productGroupId;

    @Column(name = "PRODUCT_GROUP_IMAGE_TYPE")
    private String productGroupImageType;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "ORIGIN_URL")
    private String originUrl;

    @Column(name = "DISPLAY_ORDER")
    private Long displayOrder;

    @Column(name = "DELETE_YN")
    private String deleteYn;

    protected LegacyProductGroupImageEntity() {}

    private LegacyProductGroupImageEntity(
            Long id,
            Long productGroupId,
            String productGroupImageType,
            String imageUrl,
            String originUrl,
            Long displayOrder,
            String deleteYn) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.productGroupImageType = productGroupImageType;
        this.imageUrl = imageUrl;
        this.originUrl = originUrl;
        this.displayOrder = displayOrder;
        this.deleteYn = deleteYn;
    }

    public static LegacyProductGroupImageEntity create(
            Long id,
            long productGroupId,
            String imageType,
            String imageUrl,
            String originUrl,
            long displayOrder,
            String deleteYn) {
        return new LegacyProductGroupImageEntity(
                id, productGroupId, imageType, imageUrl, originUrl, displayOrder, deleteYn);
    }

    public Long getId() {
        return id;
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

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
