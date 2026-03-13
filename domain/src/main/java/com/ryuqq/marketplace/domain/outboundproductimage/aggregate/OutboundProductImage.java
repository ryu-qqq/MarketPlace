package com.ryuqq.marketplace.domain.outboundproductimage.aggregate;

import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.outboundproduct.id.OutboundProductId;
import com.ryuqq.marketplace.domain.outboundproductimage.id.OutboundProductImageId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroupimage.id.ProductGroupImageId;
import java.time.Instant;

/**
 * OutboundProductImage Aggregate.
 *
 * <p>외부 채널(네이버 등)에 업로드된 이미지의 URL 매핑을 관리합니다.
 * originUrl(우리 S3 URL)과 externalUrl(채널 CDN URL)을 매핑하여,
 * 상품 수정 시 이미 업로드된 이미지를 재사용할 수 있도록 캐싱합니다.
 */
public class OutboundProductImage {

    private final OutboundProductImageId id;
    private final OutboundProductId outboundProductId;
    private final ProductGroupImageId productGroupImageId;
    private final String originUrl;
    private String externalUrl;
    private final ImageType imageType;
    private int sortOrder;
    private DeletionStatus deletionStatus;

    private OutboundProductImage(
            OutboundProductImageId id,
            OutboundProductId outboundProductId,
            ProductGroupImageId productGroupImageId,
            String originUrl,
            String externalUrl,
            ImageType imageType,
            int sortOrder,
            DeletionStatus deletionStatus) {
        this.id = id;
        this.outboundProductId = outboundProductId;
        this.productGroupImageId = productGroupImageId;
        this.originUrl = originUrl;
        this.externalUrl = externalUrl;
        this.imageType = imageType;
        this.sortOrder = sortOrder;
        this.deletionStatus = deletionStatus;
    }

    /** 신규 생성. externalUrl은 업로드 후 설정. */
    public static OutboundProductImage forNew(
            Long outboundProductId,
            Long productGroupImageId,
            String originUrl,
            ImageType imageType,
            int sortOrder) {
        return new OutboundProductImage(
                OutboundProductImageId.forNew(),
                OutboundProductId.of(outboundProductId),
                productGroupImageId != null
                        ? ProductGroupImageId.of(productGroupImageId) : null,
                originUrl,
                null,
                imageType,
                sortOrder,
                DeletionStatus.active());
    }

    /** 영속성에서 복원 시 사용. */
    public static OutboundProductImage reconstitute(
            OutboundProductImageId id,
            Long outboundProductId,
            Long productGroupImageId,
            String originUrl,
            String externalUrl,
            ImageType imageType,
            int sortOrder,
            DeletionStatus deletionStatus) {
        return new OutboundProductImage(
                id,
                OutboundProductId.of(outboundProductId),
                productGroupImageId != null
                        ? ProductGroupImageId.of(productGroupImageId) : null,
                originUrl, externalUrl, imageType, sortOrder, deletionStatus);
    }

    /** sortOrder와 externalUrl을 유지하면서 새 인스턴스를 반환 (diff retained용). */
    public OutboundProductImage withSortOrder(int newSortOrder) {
        return new OutboundProductImage(
                id, outboundProductId, productGroupImageId,
                originUrl, externalUrl, imageType, newSortOrder, deletionStatus);
    }

    /** soft delete된 새 인스턴스를 반환 (diff removed용). */
    public OutboundProductImage asDeleted(Instant occurredAt) {
        return new OutboundProductImage(
                id, outboundProductId, productGroupImageId,
                originUrl, externalUrl, imageType, sortOrder,
                DeletionStatus.deletedAt(occurredAt));
    }

    /** 외부 채널 업로드 완료 후 external URL 설정. */
    public void assignExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public boolean isThumbnail() {
        return imageType == ImageType.THUMBNAIL;
    }

    public boolean hasExternalUrl() {
        return externalUrl != null;
    }

    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    /** diff 비교 키: originUrl + imageType */
    public String imageKey() {
        return originUrl + "::" + imageType.name();
    }

    // Getters

    public OutboundProductImageId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public OutboundProductId outboundProductId() {
        return outboundProductId;
    }

    public Long outboundProductIdValue() {
        return outboundProductId.value();
    }

    public ProductGroupImageId productGroupImageId() {
        return productGroupImageId;
    }

    public Long productGroupImageIdValue() {
        return productGroupImageId != null ? productGroupImageId.value() : null;
    }

    public String originUrl() {
        return originUrl;
    }

    public String externalUrl() {
        return externalUrl;
    }

    public ImageType imageType() {
        return imageType;
    }

    public int sortOrder() {
        return sortOrder;
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }
}
