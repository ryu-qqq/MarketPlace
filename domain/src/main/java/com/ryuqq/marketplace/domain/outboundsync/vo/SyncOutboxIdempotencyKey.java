package com.ryuqq.marketplace.domain.outboundsync.vo;

import java.time.Instant;
import java.util.Objects;

/**
 * 외부 상품 연동 Outbox 멱등키 VO.
 *
 * <p>외부 채널 API 호출 시 중복 요청 방지를 위한 멱등키입니다.
 *
 * <p><strong>형식</strong>: {@code EPSO:{productGroupId}:{salesChannelId}:{syncType}:{epochMilli}}
 *
 * <p><strong>사용 예시</strong>:
 *
 * <ul>
 *   <li>EPSO:100:1:CREATE:1706612400000
 *   <li>EPSO:200:2:UPDATE:1706612400000
 * </ul>
 */
public record SyncOutboxIdempotencyKey(String value) {

    private static final String PREFIX = "EPSO";
    private static final String DELIMITER = ":";

    /**
     * 새 멱등키 생성.
     *
     * @param productGroupId 상품그룹 ID
     * @param salesChannelId 판매채널 ID
     * @param syncType 연동 타입
     * @param createdAt 생성 시각
     * @return 새 멱등키
     */
    public static SyncOutboxIdempotencyKey generate(
            Long productGroupId, Long salesChannelId, SyncType syncType, Instant createdAt) {
        Objects.requireNonNull(productGroupId, "productGroupId는 필수입니다");
        Objects.requireNonNull(salesChannelId, "salesChannelId는 필수입니다");
        Objects.requireNonNull(syncType, "syncType은 필수입니다");
        Objects.requireNonNull(createdAt, "createdAt은 필수입니다");

        String value =
                PREFIX
                        + DELIMITER
                        + productGroupId
                        + DELIMITER
                        + salesChannelId
                        + DELIMITER
                        + syncType.name()
                        + DELIMITER
                        + createdAt.toEpochMilli();
        return new SyncOutboxIdempotencyKey(value);
    }

    /**
     * 기존 값으로 재구성 (DB에서 로드 시).
     *
     * @param value 저장된 멱등키 값
     * @return 멱등키
     */
    public static SyncOutboxIdempotencyKey of(String value) {
        Objects.requireNonNull(value, "멱등키 값은 필수입니다");
        if (!value.startsWith(PREFIX + DELIMITER)) {
            throw new IllegalArgumentException("잘못된 멱등키 형식입니다: " + value);
        }
        return new SyncOutboxIdempotencyKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
