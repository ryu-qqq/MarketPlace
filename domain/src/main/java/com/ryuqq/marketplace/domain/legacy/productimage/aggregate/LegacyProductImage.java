package com.ryuqq.marketplace.domain.legacy.productimage.aggregate;

import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productimage.id.LegacyProductImageId;
import com.ryuqq.marketplace.domain.legacy.productimage.vo.ProductGroupImageType;
import java.time.Instant;

/**
 * 레거시(세토프) 상품 그룹 이미지 Aggregate Root.
 *
 * <p>세토프 DB의 product_group_image 테이블에 대응합니다.
 */
public class LegacyProductImage {

    private final LegacyProductImageId id;
    private final LegacyProductGroupId productGroupId;
    private ProductGroupImageType imageType;
    private String imageUrl;
    private String originUrl;
    private int displayOrder;
    private DeletionStatus deletionStatus;

    private LegacyProductImage(
            LegacyProductImageId id,
            LegacyProductGroupId productGroupId,
            ProductGroupImageType imageType,
            String imageUrl,
            String originUrl,
            int displayOrder,
            DeletionStatus deletionStatus) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.imageType = imageType;
        this.imageUrl = imageUrl;
        this.originUrl = originUrl;
        this.displayOrder = displayOrder;
        this.deletionStatus = deletionStatus;
    }

    /** 신규 레거시 상품 이미지 생성. */
    public static LegacyProductImage forNew(
            LegacyProductGroupId productGroupId,
            ProductGroupImageType imageType,
            String imageUrl,
            String originUrl,
            int displayOrder) {
        return new LegacyProductImage(
                LegacyProductImageId.forNew(),
                productGroupId,
                imageType,
                imageUrl,
                originUrl,
                displayOrder,
                DeletionStatus.active());
    }

    /** DB에서 복원. */
    public static LegacyProductImage reconstitute(
            Long id,
            Long productGroupId,
            ProductGroupImageType imageType,
            String imageUrl,
            String originUrl,
            int displayOrder,
            DeletionStatus deletionStatus) {
        return new LegacyProductImage(
                LegacyProductImageId.of(id),
                LegacyProductGroupId.of(productGroupId),
                imageType,
                imageUrl,
                originUrl,
                displayOrder,
                deletionStatus);
    }

    /** soft-delete 처리. */
    public void delete(Instant occurredAt) {
        this.deletionStatus = DeletionStatus.deletedAt(occurredAt);
    }

    public LegacyProductImageId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public LegacyProductGroupId productGroupId() {
        return productGroupId;
    }

    public Long productGroupIdValue() {
        return productGroupId.value();
    }

    public ProductGroupImageType imageType() {
        return imageType;
    }

    public String imageUrl() {
        return imageUrl;
    }

    public String originUrl() {
        return originUrl;
    }

    public int displayOrder() {
        return displayOrder;
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }

    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }
}
