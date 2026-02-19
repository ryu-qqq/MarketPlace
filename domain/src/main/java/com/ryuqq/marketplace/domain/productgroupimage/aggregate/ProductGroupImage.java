package com.ryuqq.marketplace.domain.productgroupimage.aggregate;

import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroupimage.id.ProductGroupImageId;
import java.time.Instant;

/** 상품 그룹 이미지 (Child Entity of ProductGroup). */
public class ProductGroupImage {

    private final ProductGroupImageId id;
    private final ProductGroupId productGroupId;
    private final ImageUrl originUrl;
    private ImageUrl uploadedUrl;
    private final ImageType imageType;
    private int sortOrder;
    private DeletionStatus deletionStatus;

    private ProductGroupImage(
            ProductGroupImageId id,
            ProductGroupId productGroupId,
            ImageUrl originUrl,
            ImageUrl uploadedUrl,
            ImageType imageType,
            int sortOrder,
            DeletionStatus deletionStatus) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.originUrl = originUrl;
        this.uploadedUrl = uploadedUrl;
        this.imageType = imageType;
        this.sortOrder = sortOrder;
        this.deletionStatus = deletionStatus;
    }

    /** 신규 이미지 생성. */
    public static ProductGroupImage forNew(
            ProductGroupId productGroupId, ImageUrl originUrl, ImageType imageType, int sortOrder) {
        return new ProductGroupImage(
                ProductGroupImageId.forNew(),
                productGroupId,
                originUrl,
                null,
                imageType,
                sortOrder,
                DeletionStatus.active());
    }

    /** 영속성에서 복원 시 사용. */
    public static ProductGroupImage reconstitute(
            ProductGroupImageId id,
            ProductGroupId productGroupId,
            ImageUrl originUrl,
            ImageUrl uploadedUrl,
            ImageType imageType,
            int sortOrder,
            DeletionStatus deletionStatus) {
        return new ProductGroupImage(
                id, productGroupId, originUrl, uploadedUrl, imageType, sortOrder, deletionStatus);
    }

    /** S3 업로드 URL 설정. */
    public void updateUploadedUrl(ImageUrl uploadedUrl) {
        this.uploadedUrl = uploadedUrl;
    }

    /** 정렬 순서 변경. */
    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isThumbnail() {
        return imageType == ImageType.THUMBNAIL;
    }

    /** S3 업로드 완료 여부. */
    public boolean isUploaded() {
        return uploadedUrl != null;
    }

    public ProductGroupImageId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public ImageUrl originUrl() {
        return originUrl;
    }

    public String originUrlValue() {
        return originUrl.value();
    }

    public ImageUrl uploadedUrl() {
        return uploadedUrl;
    }

    public String uploadedUrlValue() {
        return uploadedUrl != null ? uploadedUrl.value() : null;
    }

    public ImageType imageType() {
        return imageType;
    }

    /** 이미지 타입의 이름 문자열을 반환합니다. */
    public String imageTypeName() {
        return imageType.name();
    }

    public ProductGroupId productGroupId() {
        return productGroupId;
    }

    public Long productGroupIdValue() {
        return productGroupId.value();
    }

    public int sortOrder() {
        return sortOrder;
    }

    /** soft delete 처리. */
    public void delete(Instant occurredAt) {
        this.deletionStatus = DeletionStatus.deletedAt(occurredAt);
    }

    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }
}
