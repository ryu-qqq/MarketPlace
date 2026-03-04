package com.ryuqq.marketplace.application.selleradmin.dto.query;

/**
 * 셀러 관리자 본인 확인 Query.
 *
 * <p>이름과 로그인 ID로 셀러 관리자 존재 여부를 확인합니다.
 *
 * @param name 관리자 이름
 * @param loginId 로그인 ID
 * @author ryu-qqq
 * @since 1.1.0
 */
public record VerifySellerAdminQuery(String name, String loginId) {

    public static VerifySellerAdminQuery of(String name, String loginId) {
        return new VerifySellerAdminQuery(name, loginId);
    }
}
