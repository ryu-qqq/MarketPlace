package com.ryuqq.marketplace.domain.productgroupinspection.vo;

import java.time.Instant;
import java.util.Objects;

/**
 * 검수 Outbox 멱등키 VO.
 *
 * <p>중복 검수 요청 방지를 위한 멱등키입니다.
 *
 * <p><strong>형식</strong>: {@code PGI:{productGroupId}:{epochMilli}}
 */
public record InspectionOutboxIdempotencyKey(String value) {

    private static final String PREFIX = "PGI";
    private static final String DELIMITER = ":";

    /**
     * 새 멱등키 생성.
     *
     * @param productGroupId 상품 그룹 ID
     * @param createdAt 생성 시각
     * @return 새 멱등키
     */
    public static InspectionOutboxIdempotencyKey generate(Long productGroupId, Instant createdAt) {
        Objects.requireNonNull(productGroupId, "productGroupId는 필수입니다");
        Objects.requireNonNull(createdAt, "createdAt은 필수입니다");

        String value = PREFIX + DELIMITER + productGroupId + DELIMITER + createdAt.toEpochMilli();
        return new InspectionOutboxIdempotencyKey(value);
    }

    /**
     * 기존 값으로 재구성 (DB에서 로드 시).
     *
     * @param value 저장된 멱등키 값
     * @return 멱등키
     */
    public static InspectionOutboxIdempotencyKey of(String value) {
        Objects.requireNonNull(value, "멱등키 값은 필수입니다");
        if (!value.startsWith(PREFIX + DELIMITER)) {
            throw new IllegalArgumentException("잘못된 멱등키 형식입니다: " + value);
        }
        return new InspectionOutboxIdempotencyKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
