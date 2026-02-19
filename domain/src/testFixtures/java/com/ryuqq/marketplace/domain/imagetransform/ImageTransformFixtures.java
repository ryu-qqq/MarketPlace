package com.ryuqq.marketplace.domain.imagetransform;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imagetransform.id.ImageTransformOutboxId;
import com.ryuqq.marketplace.domain.imagetransform.vo.ImageTransformOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.imagetransform.vo.ImageTransformOutboxStatus;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import java.time.Instant;

/**
 * ImageTransform 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 ImageTransform 관련 객체들을 생성합니다.
 */
public final class ImageTransformFixtures {

    private ImageTransformFixtures() {}

    // ===== 기본 값 상수 =====
    public static final Long DEFAULT_SOURCE_IMAGE_ID = 100L;
    public static final ImageSourceType DEFAULT_SOURCE_TYPE = ImageSourceType.PRODUCT_GROUP_IMAGE;
    public static final String DEFAULT_UPLOADED_URL = "https://cdn.example.com/uploaded/image.jpg";
    public static final ImageVariantType DEFAULT_VARIANT_TYPE = ImageVariantType.SMALL_WEBP;
    public static final String DEFAULT_TRANSFORM_REQUEST_ID = "tr-req-abc-123";

    // ===== ImageTransformOutbox Aggregate Fixtures =====

    /** PENDING 상태의 신규 Outbox 생성. */
    public static ImageTransformOutbox newPendingOutbox() {
        return ImageTransformOutbox.forNew(
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                ImageUrl.of(DEFAULT_UPLOADED_URL),
                DEFAULT_VARIANT_TYPE,
                CommonVoFixtures.now());
    }

    /** PENDING 상태의 신규 Outbox 생성 (variantType 지정). */
    public static ImageTransformOutbox newPendingOutbox(ImageVariantType variantType) {
        return ImageTransformOutbox.forNew(
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                ImageUrl.of(DEFAULT_UPLOADED_URL),
                variantType,
                CommonVoFixtures.now());
    }

    /** PENDING 상태의 reconstituted Outbox. */
    public static ImageTransformOutbox pendingOutbox() {
        return pendingOutbox(1L);
    }

    /** ID 지정 PENDING 상태 Outbox. */
    public static ImageTransformOutbox pendingOutbox(Long id) {
        Instant now = CommonVoFixtures.now();
        return ImageTransformOutbox.reconstitute(
                ImageTransformOutboxId.of(id),
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                ImageUrl.of(DEFAULT_UPLOADED_URL),
                DEFAULT_VARIANT_TYPE,
                null,
                ImageTransformOutboxStatus.PENDING,
                0,
                3,
                now,
                now,
                null,
                null,
                0L,
                "ITO:" + DEFAULT_SOURCE_IMAGE_ID + ":SMALL_WEBP:" + now.toEpochMilli());
    }

    /** PROCESSING 상태 Outbox. */
    public static ImageTransformOutbox processingOutbox() {
        Instant now = CommonVoFixtures.now();
        return ImageTransformOutbox.reconstitute(
                ImageTransformOutboxId.of(1L),
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                ImageUrl.of(DEFAULT_UPLOADED_URL),
                DEFAULT_VARIANT_TYPE,
                DEFAULT_TRANSFORM_REQUEST_ID,
                ImageTransformOutboxStatus.PROCESSING,
                0,
                3,
                now,
                now,
                null,
                null,
                1L,
                "ITO:" + DEFAULT_SOURCE_IMAGE_ID + ":SMALL_WEBP:" + now.toEpochMilli());
    }

    /** COMPLETED 상태 Outbox. */
    public static ImageTransformOutbox completedOutbox() {
        Instant now = CommonVoFixtures.now();
        return ImageTransformOutbox.reconstitute(
                ImageTransformOutboxId.of(1L),
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                ImageUrl.of(DEFAULT_UPLOADED_URL),
                DEFAULT_VARIANT_TYPE,
                DEFAULT_TRANSFORM_REQUEST_ID,
                ImageTransformOutboxStatus.COMPLETED,
                0,
                3,
                now,
                now,
                now,
                null,
                2L,
                "ITO:" + DEFAULT_SOURCE_IMAGE_ID + ":SMALL_WEBP:" + now.toEpochMilli());
    }

    /** FAILED 상태 Outbox. */
    public static ImageTransformOutbox failedOutbox() {
        Instant now = CommonVoFixtures.now();
        return ImageTransformOutbox.reconstitute(
                ImageTransformOutboxId.of(1L),
                DEFAULT_SOURCE_IMAGE_ID,
                DEFAULT_SOURCE_TYPE,
                ImageUrl.of(DEFAULT_UPLOADED_URL),
                DEFAULT_VARIANT_TYPE,
                DEFAULT_TRANSFORM_REQUEST_ID,
                ImageTransformOutboxStatus.FAILED,
                3,
                3,
                now,
                now,
                now,
                "최대 재시도 횟수 초과",
                3L,
                "ITO:" + DEFAULT_SOURCE_IMAGE_ID + ":SMALL_WEBP:" + now.toEpochMilli());
    }

    // ===== VO Fixtures =====

    public static ImageTransformOutboxId outboxId(Long value) {
        return ImageTransformOutboxId.of(value);
    }

    public static ImageTransformOutboxId newOutboxId() {
        return ImageTransformOutboxId.forNew();
    }

    public static ImageTransformOutboxIdempotencyKey idempotencyKey() {
        return ImageTransformOutboxIdempotencyKey.generate(
                DEFAULT_SOURCE_IMAGE_ID, DEFAULT_VARIANT_TYPE, CommonVoFixtures.now());
    }
}
