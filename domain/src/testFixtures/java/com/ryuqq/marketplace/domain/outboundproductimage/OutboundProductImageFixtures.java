package com.ryuqq.marketplace.domain.outboundproductimage;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import com.ryuqq.marketplace.domain.outboundproductimage.id.OutboundProductImageId;
import com.ryuqq.marketplace.domain.outboundproductimage.vo.OutboundProductImageDiff;
import com.ryuqq.marketplace.domain.outboundproductimage.vo.OutboundProductImages;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import java.time.Instant;
import java.util.List;

/**
 * OutboundProductImage 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 OutboundProductImage 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class OutboundProductImageFixtures {

    private OutboundProductImageFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_OUTBOUND_PRODUCT_ID = 100L;
    public static final Long DEFAULT_PRODUCT_GROUP_IMAGE_ID = 10L;
    public static final String DEFAULT_ORIGIN_URL = "https://s3.example.com/image.jpg";
    public static final String DEFAULT_EXTERNAL_URL = "https://shop-phinf.pstatic.net/image.jpg";
    public static final String DEFAULT_DETAIL_ORIGIN_URL = "https://s3.example.com/detail.jpg";
    public static final String DEFAULT_DETAIL_EXTERNAL_URL = "https://shop-phinf.pstatic.net/detail.jpg";

    // ===== ID Fixtures =====

    public static OutboundProductImageId defaultOutboundProductImageId() {
        return OutboundProductImageId.of(DEFAULT_ID);
    }

    public static OutboundProductImageId outboundProductImageId(Long value) {
        return OutboundProductImageId.of(value);
    }

    public static OutboundProductImageId newOutboundProductImageId() {
        return OutboundProductImageId.forNew();
    }

    // ===== Aggregate Fixtures =====

    /** 신규 썸네일 이미지 (externalUrl 없음). */
    public static OutboundProductImage newThumbnailImage() {
        return OutboundProductImage.forNew(
                DEFAULT_OUTBOUND_PRODUCT_ID,
                DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                DEFAULT_ORIGIN_URL,
                ImageType.THUMBNAIL,
                0);
    }

    /** 신규 상세 이미지 (externalUrl 없음). */
    public static OutboundProductImage newDetailImage(int sortOrder) {
        return OutboundProductImage.forNew(
                DEFAULT_OUTBOUND_PRODUCT_ID,
                DEFAULT_PRODUCT_GROUP_IMAGE_ID + sortOrder,
                "https://s3.example.com/detail" + sortOrder + ".jpg",
                ImageType.DETAIL,
                sortOrder);
    }

    /** externalUrl이 할당된 썸네일 이미지. */
    public static OutboundProductImage thumbnailImageWithExternalUrl() {
        OutboundProductImage image = newThumbnailImage();
        image.assignExternalUrl(DEFAULT_EXTERNAL_URL);
        return image;
    }

    /** externalUrl이 할당된 상세 이미지. */
    public static OutboundProductImage detailImageWithExternalUrl(int sortOrder) {
        OutboundProductImage image = newDetailImage(sortOrder);
        image.assignExternalUrl("https://shop-phinf.pstatic.net/detail" + sortOrder + ".jpg");
        return image;
    }

    /** 영속화된 활성 썸네일 이미지. */
    public static OutboundProductImage activeThumbnailImage() {
        return OutboundProductImage.reconstitute(
                OutboundProductImageId.of(DEFAULT_ID),
                DEFAULT_OUTBOUND_PRODUCT_ID,
                DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                DEFAULT_ORIGIN_URL,
                DEFAULT_EXTERNAL_URL,
                ImageType.THUMBNAIL,
                0,
                DeletionStatus.active());
    }

    /** 영속화된 활성 상세 이미지. */
    public static OutboundProductImage activeDetailImage(Long id, int sortOrder) {
        return OutboundProductImage.reconstitute(
                OutboundProductImageId.of(id),
                DEFAULT_OUTBOUND_PRODUCT_ID,
                DEFAULT_PRODUCT_GROUP_IMAGE_ID + sortOrder,
                "https://s3.example.com/detail" + sortOrder + ".jpg",
                "https://shop-phinf.pstatic.net/detail" + sortOrder + ".jpg",
                ImageType.DETAIL,
                sortOrder,
                DeletionStatus.active());
    }

    /** 영속화된 삭제된 이미지. */
    public static OutboundProductImage deletedThumbnailImage() {
        return OutboundProductImage.reconstitute(
                OutboundProductImageId.of(DEFAULT_ID),
                DEFAULT_OUTBOUND_PRODUCT_ID,
                DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                DEFAULT_ORIGIN_URL,
                DEFAULT_EXTERNAL_URL,
                ImageType.THUMBNAIL,
                0,
                DeletionStatus.deletedAt(CommonVoFixtures.yesterday()));
    }

    // ===== OutboundProductImages Fixtures =====

    /** 비어있는 OutboundProductImages. */
    public static OutboundProductImages emptyImages() {
        return OutboundProductImages.empty();
    }

    /** 썸네일 1개만 있는 OutboundProductImages. */
    public static OutboundProductImages thumbnailOnlyImages() {
        return OutboundProductImages.of(List.of(activeThumbnailImage()));
    }

    /** 썸네일 + 상세 이미지 2개를 포함한 OutboundProductImages. */
    public static OutboundProductImages fullImages() {
        return OutboundProductImages.of(
                List.of(activeThumbnailImage(), activeDetailImage(2L, 1), activeDetailImage(3L, 2)));
    }

    /** 삭제된 이미지를 포함한 OutboundProductImages. */
    public static OutboundProductImages imagesWithDeleted() {
        return OutboundProductImages.of(
                List.of(activeThumbnailImage(), deletedThumbnailImage()));
    }

    // ===== OutboundProductImageDiff Fixtures =====

    /** 변경 없는 diff. */
    public static OutboundProductImageDiff noChangeDiff(Instant now) {
        OutboundProductImage retained = activeThumbnailImage();
        return OutboundProductImageDiff.of(List.of(), List.of(), List.of(retained), now);
    }

    /** 추가만 있는 diff. */
    public static OutboundProductImageDiff addedOnlyDiff(Instant now) {
        OutboundProductImage added = newThumbnailImage();
        return OutboundProductImageDiff.of(List.of(added), List.of(), List.of(), now);
    }

    /** 삭제만 있는 diff. */
    public static OutboundProductImageDiff removedOnlyDiff(Instant now) {
        OutboundProductImage removed = activeThumbnailImage().asDeleted(now);
        return OutboundProductImageDiff.of(List.of(), List.of(removed), List.of(), now);
    }
}
