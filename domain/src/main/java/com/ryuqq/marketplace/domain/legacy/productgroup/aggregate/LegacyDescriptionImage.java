package com.ryuqq.marketplace.domain.legacy.productgroup.aggregate;

import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import java.time.Instant;

/**
 * 레거시 상세설명 이미지 (LegacyProductGroupDescription의 자식 엔티티).
 *
 * <p>HTML 상세설명에 포함된 이미지의 원본 URL과 업로드 URL을 관리한다.
 */
public class LegacyDescriptionImage {

    private final Long id;
    private final long productGroupId;
    private final String originUrl;
    private String uploadedUrl;
    private int sortOrder;
    private DeletionStatus deletionStatus;

    private LegacyDescriptionImage(
            Long id,
            long productGroupId,
            String originUrl,
            String uploadedUrl,
            int sortOrder,
            DeletionStatus deletionStatus) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.originUrl = originUrl;
        this.uploadedUrl = uploadedUrl;
        this.sortOrder = sortOrder;
        this.deletionStatus = deletionStatus;
    }

    /** 신규 이미지 생성. */
    public static LegacyDescriptionImage forNew(
            long productGroupId, String originUrl, int sortOrder) {
        return new LegacyDescriptionImage(
                null, productGroupId, originUrl, null, sortOrder, DeletionStatus.active());
    }

    /** 영속성에서 복원. */
    public static LegacyDescriptionImage reconstitute(
            Long id,
            long productGroupId,
            String originUrl,
            String uploadedUrl,
            int sortOrder,
            DeletionStatus deletionStatus) {
        return new LegacyDescriptionImage(
                id, productGroupId, originUrl, uploadedUrl, sortOrder, deletionStatus);
    }

    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void updateUploadedUrl(String uploadedUrl) {
        this.uploadedUrl = uploadedUrl;
    }

    public void delete(Instant occurredAt) {
        this.deletionStatus = DeletionStatus.deletedAt(occurredAt);
    }

    public boolean isUploaded() {
        return uploadedUrl != null;
    }

    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    public Long id() {
        return id;
    }

    public long productGroupId() {
        return productGroupId;
    }

    public String originUrl() {
        return originUrl;
    }

    public String uploadedUrl() {
        return uploadedUrl;
    }

    public int sortOrder() {
        return sortOrder;
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }
}
