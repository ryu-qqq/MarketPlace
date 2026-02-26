package com.ryuqq.marketplace.adapter.out.persistence.productgroupimage;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.ProductGroupImageJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ProductGroupImageJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ProductGroupImageJpaEntity 관련 객체들을 생성합니다.
 */
public final class ProductGroupImageJpaEntityFixtures {

    private ProductGroupImageJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 1L;
    public static final String DEFAULT_ORIGIN_URL = "https://example.com/image.jpg";
    public static final String DEFAULT_UPLOADED_URL = "https://s3.example.com/uploaded.jpg";
    public static final String DEFAULT_IMAGE_TYPE = "THUMBNAIL";
    public static final int DEFAULT_SORT_ORDER = 0;

    // ===== Entity Fixtures =====

    /** 업로드 완료된 THUMBNAIL 이미지 Entity 생성. */
    public static ProductGroupImageJpaEntity thumbnailEntity() {
        long seq = SEQUENCE.getAndIncrement();
        return ProductGroupImageJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_ORIGIN_URL + "?seq=" + seq,
                DEFAULT_UPLOADED_URL + "?seq=" + seq,
                "THUMBNAIL",
                0,
                false,
                null);
    }

    /** 특정 ProductGroupId를 가진 THUMBNAIL 이미지 Entity 생성. */
    public static ProductGroupImageJpaEntity thumbnailEntity(Long productGroupId) {
        long seq = SEQUENCE.getAndIncrement();
        return ProductGroupImageJpaEntity.create(
                null,
                productGroupId,
                DEFAULT_ORIGIN_URL + "?seq=" + seq,
                DEFAULT_UPLOADED_URL + "?seq=" + seq,
                "THUMBNAIL",
                0,
                false,
                null);
    }

    /** ID를 지정한 THUMBNAIL 이미지 Entity 생성. */
    public static ProductGroupImageJpaEntity thumbnailEntity(Long id, Long productGroupId) {
        return ProductGroupImageJpaEntity.create(
                id,
                productGroupId,
                DEFAULT_ORIGIN_URL,
                DEFAULT_UPLOADED_URL,
                "THUMBNAIL",
                0,
                false,
                null);
    }

    /** DETAIL 이미지 Entity 생성. */
    public static ProductGroupImageJpaEntity detailEntity(Long productGroupId, int sortOrder) {
        long seq = SEQUENCE.getAndIncrement();
        return ProductGroupImageJpaEntity.create(
                null,
                productGroupId,
                "https://example.com/detail" + seq + ".jpg",
                "https://s3.example.com/detail" + seq + ".jpg",
                "DETAIL",
                sortOrder,
                false,
                null);
    }

    /** 업로드 URL이 없는 이미지 Entity 생성 (업로드 대기 상태). */
    public static ProductGroupImageJpaEntity pendingUploadEntity(Long productGroupId) {
        long seq = SEQUENCE.getAndIncrement();
        return ProductGroupImageJpaEntity.create(
                null,
                productGroupId,
                DEFAULT_ORIGIN_URL + "?seq=" + seq,
                null,
                "THUMBNAIL",
                0,
                false,
                null);
    }

    /** 삭제된 이미지 Entity 생성. */
    public static ProductGroupImageJpaEntity deletedEntity(Long productGroupId) {
        long seq = SEQUENCE.getAndIncrement();
        return ProductGroupImageJpaEntity.create(
                null,
                productGroupId,
                DEFAULT_ORIGIN_URL + "?seq=" + seq,
                DEFAULT_UPLOADED_URL + "?seq=" + seq,
                "THUMBNAIL",
                0,
                true,
                Instant.now());
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static ProductGroupImageJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        return ProductGroupImageJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_ORIGIN_URL + "?seq=" + seq,
                null,
                "THUMBNAIL",
                0,
                false,
                null);
    }
}
