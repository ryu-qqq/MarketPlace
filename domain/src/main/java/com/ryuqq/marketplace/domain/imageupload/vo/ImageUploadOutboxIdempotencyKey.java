package com.ryuqq.marketplace.domain.imageupload.vo;

import java.time.Instant;
import java.util.Objects;

/**
 * 이미지 업로드 Outbox 멱등키 VO.
 *
 * <p>S3 업로드 중복 요청 방지를 위한 멱등키입니다.
 *
 * <p><strong>형식</strong>: {@code IUO:{sourceType}:{sourceId}:{epochMilli}}
 *
 * <p><strong>사용 예시</strong>:
 *
 * <ul>
 *   <li>IUO:PRODUCT_GROUP_IMAGE:123:1706612400000
 *   <li>IUO:DESCRIPTION_IMAGE:456:1706612400000
 * </ul>
 */
public record ImageUploadOutboxIdempotencyKey(String value) {

    private static final String PREFIX = "IUO";
    private static final String DELIMITER = ":";

    /**
     * 새 멱등키 생성.
     *
     * @param sourceType 이미지 소스 타입
     * @param sourceId 이미지 DB ID
     * @param createdAt 생성 시각
     * @return 새 멱등키
     */
    public static ImageUploadOutboxIdempotencyKey generate(
            ImageSourceType sourceType, Long sourceId, Instant createdAt) {
        Objects.requireNonNull(sourceType, "sourceType은 필수입니다");
        Objects.requireNonNull(sourceId, "sourceId는 필수입니다");
        Objects.requireNonNull(createdAt, "createdAt은 필수입니다");

        String value =
                PREFIX
                        + DELIMITER
                        + sourceType.name()
                        + DELIMITER
                        + sourceId
                        + DELIMITER
                        + createdAt.toEpochMilli();
        return new ImageUploadOutboxIdempotencyKey(value);
    }

    /**
     * 기존 값으로 재구성 (DB에서 로드 시).
     *
     * @param value 저장된 멱등키 값
     * @return 멱등키
     */
    public static ImageUploadOutboxIdempotencyKey of(String value) {
        Objects.requireNonNull(value, "멱등키 값은 필수입니다");
        if (!value.startsWith(PREFIX + DELIMITER)) {
            throw new IllegalArgumentException("잘못된 멱등키 형식입니다: " + value);
        }
        return new ImageUploadOutboxIdempotencyKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
