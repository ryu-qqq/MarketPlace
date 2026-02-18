package com.ryuqq.marketplace.domain.imagetransform.vo;

import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.time.Instant;
import java.util.Objects;

/**
 * 이미지 변환 Outbox 멱등키 VO.
 *
 * <p>변환 중복 요청 방지를 위한 멱등키입니다.
 *
 * <p><strong>형식</strong>: {@code ITO:{sourceImageId}:{variantType}:{epochMilli}}
 *
 * <p><strong>사용 예시</strong>:
 *
 * <ul>
 *   <li>ITO:123:SMALL_WEBP:1706612400000
 *   <li>ITO:456:ORIGINAL_WEBP:1706612400000
 * </ul>
 */
public record ImageTransformOutboxIdempotencyKey(String value) {

    private static final String PREFIX = "ITO";
    private static final String DELIMITER = ":";

    /**
     * 새 멱등키 생성.
     *
     * @param sourceImageId 소스 이미지 ID
     * @param variantType Variant 타입
     * @param createdAt 생성 시각
     * @return 새 멱등키
     */
    public static ImageTransformOutboxIdempotencyKey generate(
            Long sourceImageId, ImageVariantType variantType, Instant createdAt) {
        Objects.requireNonNull(sourceImageId, "sourceImageId는 필수입니다");
        Objects.requireNonNull(variantType, "variantType은 필수입니다");
        Objects.requireNonNull(createdAt, "createdAt은 필수입니다");

        String value =
                PREFIX
                        + DELIMITER
                        + sourceImageId
                        + DELIMITER
                        + variantType.name()
                        + DELIMITER
                        + createdAt.toEpochMilli();
        return new ImageTransformOutboxIdempotencyKey(value);
    }

    /**
     * 기존 값으로 재구성 (DB에서 로드 시).
     *
     * @param value 저장된 멱등키 값
     * @return 멱등키
     */
    public static ImageTransformOutboxIdempotencyKey of(String value) {
        Objects.requireNonNull(value, "멱등키 값은 필수입니다");
        if (!value.startsWith(PREFIX + DELIMITER)) {
            throw new IllegalArgumentException("잘못된 멱등키 형식입니다: " + value);
        }
        return new ImageTransformOutboxIdempotencyKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
