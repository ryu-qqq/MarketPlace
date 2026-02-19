package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.DescriptionImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.ProductGroupDescriptionJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ProductGroupDescriptionJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ProductGroupDescriptionJpaEntity 관련 객체들을 생성합니다.
 */
public final class ProductGroupDescriptionJpaEntityFixtures {

    private ProductGroupDescriptionJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 1L;
    public static final String DEFAULT_CONTENT = "<p>상품 상세설명</p>";
    public static final String DEFAULT_CDN_PATH = "https://cdn.example.com/products/";
    public static final String DEFAULT_PUBLISH_STATUS = "PENDING";
    public static final String DEFAULT_ORIGIN_URL = "https://example.com/desc-image.jpg";
    public static final String DEFAULT_UPLOADED_URL = "https://s3.example.com/desc-uploaded.jpg";

    // ===== ProductGroupDescriptionJpaEntity Fixtures =====

    /** PENDING 상태의 ProductGroupDescription Entity 생성 (ID null). */
    public static ProductGroupDescriptionJpaEntity pendingEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ProductGroupDescriptionJpaEntity.create(
                null, DEFAULT_PRODUCT_GROUP_ID + seq, DEFAULT_CONTENT, null, "PENDING", now, now);
    }

    /** 특정 ProductGroupId를 가진 PENDING Entity 생성. */
    public static ProductGroupDescriptionJpaEntity pendingEntity(Long productGroupId) {
        Instant now = Instant.now();
        return ProductGroupDescriptionJpaEntity.create(
                null, productGroupId, DEFAULT_CONTENT, null, "PENDING", now, now);
    }

    /** ID를 지정한 PENDING Entity 생성. */
    public static ProductGroupDescriptionJpaEntity pendingEntity(Long id, Long productGroupId) {
        Instant now = Instant.now();
        return ProductGroupDescriptionJpaEntity.create(
                id, productGroupId, DEFAULT_CONTENT, null, "PENDING", now, now);
    }

    /** PUBLISHED 상태의 Entity 생성. */
    public static ProductGroupDescriptionJpaEntity publishedEntity(Long productGroupId) {
        Instant now = Instant.now();
        return ProductGroupDescriptionJpaEntity.create(
                null, productGroupId, DEFAULT_CONTENT, DEFAULT_CDN_PATH, "PUBLISHED", now, now);
    }

    /** PUBLISHED 상태의 Entity 생성 (ID 포함). */
    public static ProductGroupDescriptionJpaEntity publishedEntity(Long id, Long productGroupId) {
        Instant now = Instant.now();
        return ProductGroupDescriptionJpaEntity.create(
                id, productGroupId, DEFAULT_CONTENT, DEFAULT_CDN_PATH, "PUBLISHED", now, now);
    }

    /** CDN 경로가 있는 Entity 생성. */
    public static ProductGroupDescriptionJpaEntity entityWithCdnPath(Long productGroupId) {
        Instant now = Instant.now();
        return ProductGroupDescriptionJpaEntity.create(
                null, productGroupId, DEFAULT_CONTENT, DEFAULT_CDN_PATH, "PUBLISHED", now, now);
    }

    /** CDN 경로가 없는 Entity 생성. */
    public static ProductGroupDescriptionJpaEntity entityWithoutCdnPath(Long productGroupId) {
        Instant now = Instant.now();
        return ProductGroupDescriptionJpaEntity.create(
                null, productGroupId, DEFAULT_CONTENT, null, "PENDING", now, now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static ProductGroupDescriptionJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ProductGroupDescriptionJpaEntity.create(
                null, DEFAULT_PRODUCT_GROUP_ID, DEFAULT_CONTENT + seq, null, "PENDING", now, now);
    }

    // ===== DescriptionImageJpaEntity Fixtures =====

    /** 업로드 완료된 DescriptionImage Entity 생성. */
    public static DescriptionImageJpaEntity uploadedImageEntity(Long descriptionId) {
        long seq = SEQUENCE.getAndIncrement();
        return DescriptionImageJpaEntity.create(
                null,
                descriptionId,
                DEFAULT_ORIGIN_URL + "?seq=" + seq,
                DEFAULT_UPLOADED_URL + "?seq=" + seq,
                (int) seq,
                false,
                null);
    }

    /** 업로드 대기 중인 DescriptionImage Entity 생성. */
    public static DescriptionImageJpaEntity pendingImageEntity(Long descriptionId) {
        long seq = SEQUENCE.getAndIncrement();
        return DescriptionImageJpaEntity.create(
                null,
                descriptionId,
                DEFAULT_ORIGIN_URL + "?seq=" + seq,
                null,
                (int) seq,
                false,
                null);
    }

    /** 삭제된 DescriptionImage Entity 생성. */
    public static DescriptionImageJpaEntity deletedImageEntity(Long descriptionId) {
        long seq = SEQUENCE.getAndIncrement();
        return DescriptionImageJpaEntity.create(
                null,
                descriptionId,
                DEFAULT_ORIGIN_URL + "?seq=" + seq,
                DEFAULT_UPLOADED_URL + "?seq=" + seq,
                (int) seq,
                true,
                Instant.now());
    }

    /** ID를 지정한 DescriptionImage Entity 생성. */
    public static DescriptionImageJpaEntity imageEntity(Long id, Long descriptionId) {
        return DescriptionImageJpaEntity.create(
                id, descriptionId, DEFAULT_ORIGIN_URL, DEFAULT_UPLOADED_URL, 0, false, null);
    }

    /** 기본 이미지 목록. */
    public static List<DescriptionImageJpaEntity> defaultImageEntities(Long descriptionId) {
        return List.of(uploadedImageEntity(descriptionId), uploadedImageEntity(descriptionId));
    }

    /** 빈 이미지 목록. */
    public static List<DescriptionImageJpaEntity> emptyImageEntities() {
        return List.of();
    }
}
