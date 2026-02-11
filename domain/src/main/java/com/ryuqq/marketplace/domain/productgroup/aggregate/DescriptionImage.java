package com.ryuqq.marketplace.domain.productgroup.aggregate;

import com.ryuqq.marketplace.domain.productgroup.id.DescriptionImageId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;

/**
 * 상세설명 내 이미지 (Child Entity of ProductGroupDescription). HTML 상세설명에 포함된 이미지의 원본 URL과 S3 업로드 URL을
 * 관리한다.
 */
public class DescriptionImage {

    private final DescriptionImageId id;
    private final ImageUrl originUrl;
    private ImageUrl uploadedUrl;
    private int sortOrder;

    private DescriptionImage(
            DescriptionImageId id, ImageUrl originUrl, ImageUrl uploadedUrl, int sortOrder) {
        this.id = id;
        this.originUrl = originUrl;
        this.uploadedUrl = uploadedUrl;
        this.sortOrder = sortOrder;
    }

    /** 신규 상세설명 이미지 생성. */
    public static DescriptionImage forNew(ImageUrl originUrl, int sortOrder) {
        return new DescriptionImage(DescriptionImageId.forNew(), originUrl, null, sortOrder);
    }

    /** 영속성에서 복원 시 사용. */
    public static DescriptionImage reconstitute(
            DescriptionImageId id, ImageUrl originUrl, ImageUrl uploadedUrl, int sortOrder) {
        return new DescriptionImage(id, originUrl, uploadedUrl, sortOrder);
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
}
