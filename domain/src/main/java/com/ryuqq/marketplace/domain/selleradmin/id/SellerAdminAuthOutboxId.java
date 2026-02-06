package com.ryuqq.marketplace.domain.selleradmin.id;

/**
 * 셀러 관리자 인증 Outbox ID Value Object.
 *
 * <p>인증 서버 연동용 Outbox를 식별하는 ID입니다.
 */
public record SellerAdminAuthOutboxId(Long value) {

    public static SellerAdminAuthOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SellerAdminAuthOutboxId 값은 null일 수 없습니다");
        }
        return new SellerAdminAuthOutboxId(value);
    }

    public static SellerAdminAuthOutboxId forNew() {
        return new SellerAdminAuthOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
