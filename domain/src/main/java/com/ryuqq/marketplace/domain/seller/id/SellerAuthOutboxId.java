package com.ryuqq.marketplace.domain.seller.id;

/**
 * 셀러 인증 Outbox ID Value Object.
 *
 * <p>인증 서버 연동용 Outbox를 식별하는 ID입니다.
 */
public record SellerAuthOutboxId(Long value) {

    public static SellerAuthOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SellerAuthOutboxId 값은 null일 수 없습니다");
        }
        return new SellerAuthOutboxId(value);
    }

    public static SellerAuthOutboxId forNew() {
        return new SellerAuthOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
