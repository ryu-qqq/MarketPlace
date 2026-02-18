package com.ryuqq.marketplace.domain.productgroup.aggregate;

import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.productgroup.id.DescriptionImageId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupDescriptionId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import java.time.Instant;

/**
 * 상세설명 내 이미지 (Child Entity of ProductGroupDescription). HTML 상세설명에 포함된 이미지의 원본 URL과 S3 업로드 URL을
 * 관리한다.
 */
public class DescriptionImage {

    private final DescriptionImageId id;
    private ProductGroupDescriptionId productGroupDescriptionId;
    private final ImageUrl originUrl;
    private ImageUrl uploadedUrl;
    private int sortOrder;
    private DeletionStatus deletionStatus;

    private DescriptionImage(
            DescriptionImageId id,
            ProductGroupDescriptionId productGroupDescriptionId,
            ImageUrl originUrl,
            ImageUrl uploadedUrl,
            int sortOrder,
            DeletionStatus deletionStatus) {
        this.id = id;
        this.productGroupDescriptionId = productGroupDescriptionId;
        this.originUrl = originUrl;
        this.uploadedUrl = uploadedUrl;
        this.sortOrder = sortOrder;
        this.deletionStatus = deletionStatus;
    }

    /** 신규 상세설명 이미지 생성. */
    public static DescriptionImage forNew(ImageUrl originUrl, int sortOrder) {
        return new DescriptionImage(
                DescriptionImageId.forNew(),
                ProductGroupDescriptionId.forNew(),
                originUrl,
                null,
                sortOrder,
                DeletionStatus.active());
    }

    /** 영속성에서 복원 시 사용. */
    public static DescriptionImage reconstitute(
            DescriptionImageId id,
            ProductGroupDescriptionId productGroupDescriptionId,
            ImageUrl originUrl,
            ImageUrl uploadedUrl,
            int sortOrder,
            DeletionStatus deletionStatus) {
        return new DescriptionImage(
                id, productGroupDescriptionId, originUrl, uploadedUrl, sortOrder, deletionStatus);
    }

    /** 부모 Description 저장 후 ID 할당. */
    public void assignProductGroupDescriptionId(
            ProductGroupDescriptionId productGroupDescriptionId) {
        this.productGroupDescriptionId = productGroupDescriptionId;
    }

    /** S3 업로드 URL 설정. */
    public void updateUploadedUrl(ImageUrl uploadedUrl) {
        this.uploadedUrl = uploadedUrl;
    }

    /** 정렬 순서 변경. */
    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    /** S3 업로드 완료 여부. */
    public boolean isUploaded() {
        return uploadedUrl != null;
    }

    public DescriptionImageId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public ProductGroupDescriptionId productGroupDescriptionId() {
        return productGroupDescriptionId;
    }

    public Long productGroupDescriptionIdValue() {
        return productGroupDescriptionId.value();
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
