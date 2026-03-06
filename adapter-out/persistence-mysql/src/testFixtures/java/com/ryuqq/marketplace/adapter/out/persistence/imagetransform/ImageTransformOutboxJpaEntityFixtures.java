package com.ryuqq.marketplace.adapter.out.persistence.imagetransform;

import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.entity.ImageTransformOutboxJpaEntity;
import com.ryuqq.marketplace.domain.imagetransform.vo.ImageTransformOutboxStatus;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ImageTransformOutboxJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ImageTransformOutboxJpaEntity 관련 객체들을 생성합니다.
 */
public final class ImageTransformOutboxJpaEntityFixtures {

    private ImageTransformOutboxJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_SOURCE_IMAGE_ID = 100L;
    public static final ImageSourceType DEFAULT_SOURCE_TYPE = ImageSourceType.PRODUCT_GROUP_IMAGE;
    public static final String DEFAULT_UPLOADED_URL = "https://cdn.example.com/uploaded/image.jpg";
    public static final ImageVariantType DEFAULT_VARIANT_TYPE = ImageVariantType.SMALL_WEBP;
    public static final String DEFAULT_FILE_ASSET_ID = "asset-abc-123";
    public static final String DEFAULT_TRANSFORM_REQUEST_ID = "tr-req-abc-123";
    public static final int DEFAULT_MAX_RETRY = 3;

    // ===== Entity Fixtures =====

    /** PENDING 상태의 신규 Outbox Entity 생성 (ID null). */
    public static ImageTransformOutboxJpaEntity newPendingEntity() {
        Instant now = Instant.now();
        long seq = SEQUENCE.getAndIncrement();
        return ImageTransformOutboxJpaEntity.create(
                null,
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_UPLOADED_URL,
                DEFAULT_VARIANT_TYPE,
                DEFAULT_FILE_ASSET_ID,
                null,
                ImageTransformOutboxStatus.PENDING,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                "ITO:" + DEFAULT_SOURCE_IMAGE_ID + ":SMALL_WEBP:" + now.toEpochMilli() + ":" + seq);
    }

    /** PENDING 상태의 Outbox Entity 생성 (ID 지정). */
    public static ImageTransformOutboxJpaEntity pendingEntity(Long id) {
        Instant now = Instant.now();
        long seq = SEQUENCE.getAndIncrement();
        return ImageTransformOutboxJpaEntity.create(
                id,
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_UPLOADED_URL,
                DEFAULT_VARIANT_TYPE,
                DEFAULT_FILE_ASSET_ID,
                null,
                ImageTransformOutboxStatus.PENDING,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                "ITO:" + DEFAULT_SOURCE_IMAGE_ID + ":SMALL_WEBP:" + now.toEpochMilli() + ":" + seq);
    }

    /** PROCESSING 상태의 Outbox Entity 생성 (ID null). */
    public static ImageTransformOutboxJpaEntity newProcessingEntity() {
        Instant now = Instant.now();
        long seq = SEQUENCE.getAndIncrement();
        return ImageTransformOutboxJpaEntity.create(
                null,
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_UPLOADED_URL,
                DEFAULT_VARIANT_TYPE,
                DEFAULT_FILE_ASSET_ID,
                DEFAULT_TRANSFORM_REQUEST_ID,
                ImageTransformOutboxStatus.PROCESSING,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                1L,
                "ITO:" + DEFAULT_SOURCE_IMAGE_ID + ":SMALL_WEBP:" + now.toEpochMilli() + ":" + seq);
    }

    /** COMPLETED 상태의 Outbox Entity 생성 (ID null). */
    public static ImageTransformOutboxJpaEntity newCompletedEntity() {
        Instant now = Instant.now();
        long seq = SEQUENCE.getAndIncrement();
        return ImageTransformOutboxJpaEntity.create(
                null,
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_UPLOADED_URL,
                DEFAULT_VARIANT_TYPE,
                DEFAULT_FILE_ASSET_ID,
                DEFAULT_TRANSFORM_REQUEST_ID,
                ImageTransformOutboxStatus.COMPLETED,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                2L,
                "ITO:" + DEFAULT_SOURCE_IMAGE_ID + ":SMALL_WEBP:" + now.toEpochMilli() + ":" + seq);
    }

    /** FAILED 상태의 Outbox Entity 생성 (ID null). */
    public static ImageTransformOutboxJpaEntity newFailedEntity() {
        Instant now = Instant.now();
        long seq = SEQUENCE.getAndIncrement();
        return ImageTransformOutboxJpaEntity.create(
                null,
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_UPLOADED_URL,
                DEFAULT_VARIANT_TYPE,
                DEFAULT_FILE_ASSET_ID,
                DEFAULT_TRANSFORM_REQUEST_ID,
                ImageTransformOutboxStatus.FAILED,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                "최대 재시도 횟수 초과",
                3L,
                "ITO:" + DEFAULT_SOURCE_IMAGE_ID + ":SMALL_WEBP:" + now.toEpochMilli() + ":" + seq);
    }

    /** 재시도 횟수가 남아 있는 PENDING Entity 생성. */
    public static ImageTransformOutboxJpaEntity newPendingEntityWithRetry(int retryCount) {
        Instant now = Instant.now();
        long seq = SEQUENCE.getAndIncrement();
        return ImageTransformOutboxJpaEntity.create(
                null,
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_UPLOADED_URL,
                DEFAULT_VARIANT_TYPE,
                DEFAULT_FILE_ASSET_ID,
                null,
                ImageTransformOutboxStatus.PENDING,
                retryCount,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                "이전 실패",
                0L,
                "ITO:" + DEFAULT_SOURCE_IMAGE_ID + ":SMALL_WEBP:" + now.toEpochMilli() + ":" + seq);
    }

    /** 지정한 소스 이미지 ID와 variantType으로 PENDING Entity 생성. */
    public static ImageTransformOutboxJpaEntity newPendingEntity(
            Long sourceImageId, ImageVariantType variantType) {
        Instant now = Instant.now();
        long seq = SEQUENCE.getAndIncrement();
        return ImageTransformOutboxJpaEntity.create(
                null,
                sourceImageId,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_UPLOADED_URL,
                variantType,
                DEFAULT_FILE_ASSET_ID,
                null,
                ImageTransformOutboxStatus.PENDING,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                "ITO:"
                        + sourceImageId
                        + ":"
                        + variantType.name()
                        + ":"
                        + now.toEpochMilli()
                        + ":"
                        + seq);
    }

    /** 오래된 updatedAt을 가진 PROCESSING Entity 생성 (타임아웃 시나리오용). */
    public static ImageTransformOutboxJpaEntity newProcessingEntityWithOldUpdatedAt(
            Instant oldUpdatedAt) {
        Instant now = Instant.now();
        long seq = SEQUENCE.getAndIncrement();
        return ImageTransformOutboxJpaEntity.create(
                null,
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_UPLOADED_URL,
                DEFAULT_VARIANT_TYPE,
                DEFAULT_FILE_ASSET_ID,
                DEFAULT_TRANSFORM_REQUEST_ID,
                ImageTransformOutboxStatus.PROCESSING,
                0,
                DEFAULT_MAX_RETRY,
                oldUpdatedAt,
                oldUpdatedAt,
                null,
                null,
                1L,
                "ITO:" + DEFAULT_SOURCE_IMAGE_ID + ":SMALL_WEBP:" + now.toEpochMilli() + ":" + seq);
    }
}
