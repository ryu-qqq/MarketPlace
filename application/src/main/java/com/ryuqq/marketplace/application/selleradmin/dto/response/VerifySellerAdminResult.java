package com.ryuqq.marketplace.application.selleradmin.dto.response;

/**
 * 셀러 관리자 본인 확인 Result.
 *
 * @param exists 존재 여부
 * @param status 셀러 관리자 상태
 * @param sellerAdminId 셀러 관리자 ID
 * @param phoneNumber 핸드폰 번호
 * @author ryu-qqq
 * @since 1.1.0
 */
public record VerifySellerAdminResult(
        boolean exists, String status, String sellerAdminId, String phoneNumber) {

    public static VerifySellerAdminResult of(
            String status, String sellerAdminId, String phoneNumber) {
        return new VerifySellerAdminResult(true, status, sellerAdminId, phoneNumber);
    }
}
