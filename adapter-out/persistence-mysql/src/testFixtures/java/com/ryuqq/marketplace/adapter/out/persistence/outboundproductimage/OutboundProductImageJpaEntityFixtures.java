package com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.entity.OutboundProductImageJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * OutboundProductImageJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 OutboundProductImageJpaEntity 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class OutboundProductImageJpaEntityFixtures {

    private OutboundProductImageJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_OUTBOUND_PRODUCT_ID = 100L;
    public static final Long DEFAULT_PRODUCT_GROUP_IMAGE_ID = 200L;
    public static final String DEFAULT_ORIGIN_URL =
            "https://s3.amazonaws.com/bucket/images/thumbnail.jpg";
    public static final String DEFAULT_EXTERNAL_URL =
            "https://shop-phinf.pstatic.net/images/thumbnail.jpg";
    public static final String IMAGE_TYPE_THUMBNAIL = "THUMBNAIL";
    public static final String IMAGE_TYPE_DETAIL = "DETAIL";
    public static final int DEFAULT_SORT_ORDER = 0;

    // ===== Entity Fixtures =====

    /** THUMBNAIL 타입의 신규 활성 Entity (ID null). */
    public static OutboundProductImageJpaEntity newThumbnailEntity() {
        Instant now = Instant.now();
        return OutboundProductImageJpaEntity.of(
                null,
                DEFAULT_OUTBOUND_PRODUCT_ID,
                DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                DEFAULT_ORIGIN_URL,
                DEFAULT_EXTERNAL_URL,
                IMAGE_TYPE_THUMBNAIL,
                DEFAULT_SORT_ORDER,
                false,
                null,
                now,
                now);
    }

    /** DETAIL 타입의 신규 활성 Entity (ID null). */
    public static OutboundProductImageJpaEntity newDetailEntity() {
        Instant now = Instant.now();
        return OutboundProductImageJpaEntity.of(
                null,
                DEFAULT_OUTBOUND_PRODUCT_ID,
                DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                "https://s3.amazonaws.com/bucket/images/detail.jpg",
                "https://shop-phinf.pstatic.net/images/detail.jpg",
                IMAGE_TYPE_DETAIL,
                1,
                false,
                null,
                now,
                now);
    }

    /** externalUrl이 null인 신규 THUMBNAIL Entity (업로드 전 상태). */
    public static OutboundProductImageJpaEntity newThumbnailEntityWithoutExternalUrl() {
        Instant now = Instant.now();
        return OutboundProductImageJpaEntity.of(
                null,
                DEFAULT_OUTBOUND_PRODUCT_ID,
                DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                DEFAULT_ORIGIN_URL,
                null,
                IMAGE_TYPE_THUMBNAIL,
                DEFAULT_SORT_ORDER,
                false,
                null,
                now,
                now);
    }

    /** 삭제된 THUMBNAIL Entity (deleted=true). */
    public static OutboundProductImageJpaEntity deletedThumbnailEntity() {
        Instant now = Instant.now();
        Instant deletedAt = now.minusSeconds(3600);
        return OutboundProductImageJpaEntity.of(
                null,
                DEFAULT_OUTBOUND_PRODUCT_ID,
                DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                DEFAULT_ORIGIN_URL,
                DEFAULT_EXTERNAL_URL,
                IMAGE_TYPE_THUMBNAIL,
                DEFAULT_SORT_ORDER,
                true,
                deletedAt,
                now.minusSeconds(7200),
                now);
    }

    /** ID를 지정한 활성 THUMBNAIL Entity. */
    public static OutboundProductImageJpaEntity entityWithId(Long id) {
        Instant now = Instant.now();
        return OutboundProductImageJpaEntity.of(
                id,
                DEFAULT_OUTBOUND_PRODUCT_ID,
                DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                DEFAULT_ORIGIN_URL,
                DEFAULT_EXTERNAL_URL,
                IMAGE_TYPE_THUMBNAIL,
                DEFAULT_SORT_ORDER,
                false,
                null,
                now,
                now);
    }

    /** outboundProductId를 지정한 신규 활성 THUMBNAIL Entity (ID null). */
    public static OutboundProductImageJpaEntity newEntityWithOutboundProductId(
            Long outboundProductId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return OutboundProductImageJpaEntity.of(
                null,
                outboundProductId,
                DEFAULT_PRODUCT_GROUP_IMAGE_ID + seq,
                DEFAULT_ORIGIN_URL,
                DEFAULT_EXTERNAL_URL,
                IMAGE_TYPE_THUMBNAIL,
                DEFAULT_SORT_ORDER,
                false,
                null,
                now,
                now);
    }

    /** sortOrder를 지정한 DETAIL Entity. */
    public static OutboundProductImageJpaEntity detailEntityWithSortOrder(int sortOrder) {
        Instant now = Instant.now();
        return OutboundProductImageJpaEntity.of(
                null,
                DEFAULT_OUTBOUND_PRODUCT_ID,
                DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                "https://s3.amazonaws.com/bucket/images/detail_" + sortOrder + ".jpg",
                "https://shop-phinf.pstatic.net/images/detail_" + sortOrder + ".jpg",
                IMAGE_TYPE_DETAIL,
                sortOrder,
                false,
                null,
                now,
                now);
    }
}
