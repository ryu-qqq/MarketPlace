package com.ryuqq.marketplace.domain.seller.vo;

import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import java.util.Objects;

/**
 * 셀러 인증 Outbox 멱등키 VO.
 *
 * <p>Auth Hub API 호출 시 중복 요청 방지를 위한 멱등키입니다.
 *
 * <p><strong>형식</strong>: {@code SAO:{sellerId}:{epochMilli}}
 *
 * <p><strong>사용 예시</strong>:
 *
 * <ul>
 *   <li>SAO:123:1706612400000
 *   <li>SAO:0:1706612400000 (sellerId가 null인 경우)
 * </ul>
 *
 * <p><strong>Auth Hub 연동</strong>: X-Idempotency-Key 헤더로 전송
 */
public record SellerAuthOutboxIdempotencyKey(String value) {

    private static final String PREFIX = "SAO";
    private static final String DELIMITER = ":";

    /**
     * 새 멱등키 생성.
     *
     * @param sellerId 셀러 ID (null 가능)
     * @param createdAt 생성 시각
     * @return 새 멱등키
     */
    public static SellerAuthOutboxIdempotencyKey generate(SellerId sellerId, Instant createdAt) {
        Objects.requireNonNull(createdAt, "createdAt은 필수입니다");

        long sellerIdValue = sellerId != null ? sellerId.value() : 0L;
        String value = PREFIX + DELIMITER + sellerIdValue + DELIMITER + createdAt.toEpochMilli();
        return new SellerAuthOutboxIdempotencyKey(value);
    }

    /**
     * 기존 값으로 재구성 (DB에서 로드 시).
     *
     * @param value 저장된 멱등키 값
     * @return 멱등키
     */
    public static SellerAuthOutboxIdempotencyKey of(String value) {
        Objects.requireNonNull(value, "멱등키 값은 필수입니다");
        if (!value.startsWith(PREFIX + DELIMITER)) {
            throw new IllegalArgumentException("잘못된 멱등키 형식입니다: " + value);
        }
        return new SellerAuthOutboxIdempotencyKey(value);
    }

    /**
     * HTTP 헤더 이름.
     *
     * @return X-Idempotency-Key
     */
    public static String headerName() {
        return "X-Idempotency-Key";
    }

    @Override
    public String toString() {
        return value;
    }
}
