package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * LegacyProductGroupDetailDescriptionEntity - 레거시 상품그룹 상세 설명 엔티티.
 *
 * <p>레거시 DB의 product_group_detail_description 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "product_group_detail_description")
public class LegacyProductGroupDetailDescriptionEntity extends LegacyBaseEntity {

    @Id
    @Column(name = "product_group_id")
    private Long productGroupId;

    @Column(name = "PRODUCT_GROUP_IMAGE_TYPE")
    private String productGroupImageType;

    @Lob
    @Column(name = "IMAGE_URL", columnDefinition = "mediumtext")
    private String imageUrl;

    @Lob
    @Column(name = "IMAGE_URLS", columnDefinition = "mediumtext")
    private String imageUrls;

    @Column(name = "delete_yn")
    private String deleteYn;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "origin_url")
    private String originUrl;

    @Lob
    @Column(name = "content", columnDefinition = "mediumtext")
    private String content;

    @Column(name = "cdn_path", length = 500)
    private String cdnPath;

    @Column(name = "publish_status", length = 20)
    private String publishStatus;

    protected LegacyProductGroupDetailDescriptionEntity() {}

    private LegacyProductGroupDetailDescriptionEntity(Long productGroupId, String imageUrl) {
        this.productGroupId = productGroupId;
        this.imageUrl = imageUrl;
        this.deleteYn = "N";
    }

    private LegacyProductGroupDetailDescriptionEntity(
            Long productGroupId, String content, String cdnPath, String publishStatus) {
        this.productGroupId = productGroupId;
        this.content = content;
        this.imageUrl = content;
        this.cdnPath = cdnPath;
        this.publishStatus = publishStatus;
        this.deleteYn = "N";
    }

    public static LegacyProductGroupDetailDescriptionEntity create(
            long productGroupId, String detailDescription) {
        return new LegacyProductGroupDetailDescriptionEntity(productGroupId, detailDescription);
    }

    /** content/cdnPath/publishStatus를 포함한 전체 필드 생성. */
    public static LegacyProductGroupDetailDescriptionEntity createFull(
            long productGroupId, String content, String cdnPath, String publishStatus) {
        return new LegacyProductGroupDetailDescriptionEntity(
                productGroupId, content, cdnPath, publishStatus);
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

    public String getImageUrls() {
        return imageUrls;
    }

    public String getDeleteYn() {
        return deleteYn;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public String getContent() {
        return content;
    }

    public String getCdnPath() {
        return cdnPath;
    }

    public String getPublishStatus() {
        return publishStatus;
    }
}
