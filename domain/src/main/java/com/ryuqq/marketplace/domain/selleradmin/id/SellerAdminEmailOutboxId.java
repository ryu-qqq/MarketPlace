package com.ryuqq.marketplace.domain.selleradmin.id;

/**
 * 셀러 관리자 이메일 Outbox ID Value Object.
 *
 * <p>이메일 발송용 Outbox를 식별하는 ID입니다.
 */
public record SellerAdminEmailOutboxId(Long value) {

    public static SellerAdminEmailOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SellerAdminEmailOutboxId 값은 null일 수 없습니다");
        }
        return new SellerAdminEmailOutboxId(value);
    }

    public static SellerAdminEmailOutboxId forNew() {
        return new SellerAdminEmailOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
