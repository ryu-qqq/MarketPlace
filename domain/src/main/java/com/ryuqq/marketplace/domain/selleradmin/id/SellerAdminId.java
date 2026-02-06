package com.ryuqq.marketplace.domain.selleradmin.id;

/**
 * 셀러 관리자 ID Value Object.
 *
 * <p>셀러 관리자(SellerAdmin)를 식별하는 ID입니다. 외부에서 UUIDv7을 주입받습니다.
 */
public record SellerAdminId(String value) {

    public static SellerAdminId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SellerAdminId 값은 null 또는 빈 문자열일 수 없습니다");
        }
        return new SellerAdminId(value);
    }

    public static SellerAdminId forNew(String value) {
        return of(value);
    }
}
