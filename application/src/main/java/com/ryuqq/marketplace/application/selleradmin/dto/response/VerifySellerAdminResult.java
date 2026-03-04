package com.ryuqq.marketplace.application.selleradmin.dto.response;

/**
 * 셀러 관리자 본인 확인 Result.
 *
 * @param exists 존재 여부
 * @param status 상태 (존재하지 않으면 null)
 * @author ryu-qqq
 * @since 1.1.0
 */
public record VerifySellerAdminResult(boolean exists, String status) {

    public static VerifySellerAdminResult found(String status) {
        return new VerifySellerAdminResult(true, status);
    }

    public static VerifySellerAdminResult notFound() {
        return new VerifySellerAdminResult(false, null);
    }
}
