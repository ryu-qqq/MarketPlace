package com.ryuqq.marketplace.domain.selleradmin.vo;

import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import java.util.Objects;

/**
 * 셀러 관리자 이메일 Outbox 멱등키 VO.
 *
 * <p>이메일 발송 시 중복 요청 방지를 위한 멱등키입니다.
 *
 * <p><strong>형식</strong>: {@code SAEO:{sellerId}:{epochMilli}}
 *
 * <p><strong>사용 예시</strong>:
 *
 * <ul>
 *   <li>SAEO:123:1706612400000
 * </ul>
 */
public record SellerAdminEmailOutboxIdempotencyKey(String value) {

    private static final String PREFIX = "SAEO";
    private static final String DELIMITER = ":";

    /**
     * 새 멱등키 생성.
     *
     * @param sellerId 셀러 ID
     * @param createdAt 생성 시각
     * @return 새 멱등키
     */
    public static SellerAdminEmailOutboxIdempotencyKey generate(
            SellerId sellerId, Instant createdAt) {
        Objects.requireNonNull(sellerId, "sellerId는 필수입니다");
        Objects.requireNonNull(createdAt, "createdAt은 필수입니다");

        String value = PREFIX + DELIMITER + sellerId.value() + DELIMITER + createdAt.toEpochMilli();
        return new SellerAdminEmailOutboxIdempotencyKey(value);
    }

    /**
     * 기존 값으로 재구성 (DB에서 로드 시).
     *
     * @param value 저장된 멱등키 값
     * @return 멱등키
     */
    public static SellerAdminEmailOutboxIdempotencyKey of(String value) {
        Objects.requireNonNull(value, "멱등키 값은 필수입니다");
        if (!value.startsWith(PREFIX + DELIMITER)) {
            throw new IllegalArgumentException("잘못된 멱등키 형식입니다: " + value);
        }
        return new SellerAdminEmailOutboxIdempotencyKey(value);
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
