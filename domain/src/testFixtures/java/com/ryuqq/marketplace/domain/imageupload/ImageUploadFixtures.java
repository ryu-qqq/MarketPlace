package com.ryuqq.marketplace.domain.imageupload;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.imageupload.id.ImageUploadOutboxId;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageUploadOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageUploadOutboxStatus;
import com.ryuqq.marketplace.domain.imageupload.vo.OriginUrl;
import java.time.Instant;

/**
 * ImageUpload 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 ImageUpload 관련 객체들을 생성합니다.
 */
public final class ImageUploadFixtures {

    private ImageUploadFixtures() {}

    // ===== 기본 값 상수 =====
    public static final Long DEFAULT_SOURCE_ID = 100L;
    public static final String DEFAULT_ORIGIN_URL = "https://example.com/images/product.jpg";
    public static final String DEFAULT_ORIGIN_URL_NO_EXT = "https://example.com/images/product";
    public static final String DEFAULT_ORIGIN_URL_WITH_QUERY =
            "https://example.com/images/product.png?width=500&quality=80";
    public static final ImageSourceType DEFAULT_SOURCE_TYPE = ImageSourceType.PRODUCT_GROUP_IMAGE;

    // ===== ImageUploadOutbox Aggregate Fixtures =====

    /** PENDING 상태의 신규 Outbox 생성. */
    public static ImageUploadOutbox newPendingOutbox() {
        return ImageUploadOutbox.forNew(
                DEFAULT_SOURCE_ID, DEFAULT_SOURCE_TYPE, DEFAULT_ORIGIN_URL, CommonVoFixtures.now());
    }

    /** PENDING 상태의 신규 Outbox 생성 (sourceType 지정). */
    public static ImageUploadOutbox newPendingOutbox(ImageSourceType sourceType) {
        return ImageUploadOutbox.forNew(
                DEFAULT_SOURCE_ID, sourceType, DEFAULT_ORIGIN_URL, CommonVoFixtures.now());
    }

    /** PENDING 상태의 reconstituted Outbox. */
    public static ImageUploadOutbox pendingOutbox() {
        return pendingOutbox(1L);
    }

    /** ID 지정 PENDING 상태 Outbox. */
    public static ImageUploadOutbox pendingOutbox(Long id) {
        Instant now = CommonVoFixtures.now();
        return ImageUploadOutbox.reconstitute(
                ImageUploadOutboxId.of(id),
                DEFAULT_SOURCE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.PENDING,
                0,
                3,
                now,
                now,
                null,
                null,
                0L,
                "IUO:PRODUCT_GROUP_IMAGE:" + DEFAULT_SOURCE_ID + ":" + now.toEpochMilli());
    }

    /** PROCESSING 상태 Outbox. */
    public static ImageUploadOutbox processingOutbox() {
        Instant now = CommonVoFixtures.now();
        return ImageUploadOutbox.reconstitute(
                ImageUploadOutboxId.of(1L),
                DEFAULT_SOURCE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.PROCESSING,
                0,
                3,
                now,
                now,
                null,
                null,
                1L,
                "IUO:PRODUCT_GROUP_IMAGE:" + DEFAULT_SOURCE_ID + ":" + now.toEpochMilli());
    }

    /** COMPLETED 상태 Outbox. */
    public static ImageUploadOutbox completedOutbox() {
        Instant now = CommonVoFixtures.now();
        return ImageUploadOutbox.reconstitute(
                ImageUploadOutboxId.of(1L),
                DEFAULT_SOURCE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.COMPLETED,
                0,
                3,
                now,
                now,
                now,
                null,
                2L,
                "IUO:PRODUCT_GROUP_IMAGE:" + DEFAULT_SOURCE_ID + ":" + now.toEpochMilli());
    }

    /** FAILED 상태 Outbox. */
    public static ImageUploadOutbox failedOutbox() {
        Instant now = CommonVoFixtures.now();
        return ImageUploadOutbox.reconstitute(
                ImageUploadOutboxId.of(1L),
                DEFAULT_SOURCE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.FAILED,
                3,
                3,
                now,
                now,
                now,
                "최대 재시도 횟수 초과",
                3L,
                "IUO:PRODUCT_GROUP_IMAGE:" + DEFAULT_SOURCE_ID + ":" + now.toEpochMilli());
    }

    // ===== VO Fixtures =====

    public static OriginUrl originUrl() {
        return OriginUrl.of(DEFAULT_ORIGIN_URL);
    }

    public static OriginUrl originUrl(String url) {
        return OriginUrl.of(url);
    }

    public static ImageUploadOutboxId outboxId(Long value) {
        return ImageUploadOutboxId.of(value);
    }

    public static ImageUploadOutboxId newOutboxId() {
        return ImageUploadOutboxId.forNew();
    }

    public static ImageUploadOutboxIdempotencyKey idempotencyKey() {
        return ImageUploadOutboxIdempotencyKey.generate(
                DEFAULT_SOURCE_TYPE, DEFAULT_SOURCE_ID, CommonVoFixtures.now());
    }
}
