package com.ryuqq.marketplace.domain.selleradmin.vo;

/**
 * 셀러 관리자 이메일 유형.
 *
 * <p>Outbox payload의 emailType 필드로 이메일 종류를 구분합니다.
 *
 * <ul>
 *   <li>{@link #SELLER_APPROVAL_INVITE}: 입점 승인 → 관리자 가입 안내 (Seller 레벨)
 *   <li>{@link #SELLER_ADMIN_WELCOME}: 회원가입 완료 → 이용 안내 (SellerAdmin 레벨)
 * </ul>
 */
public enum SellerAdminEmailType {
    SELLER_APPROVAL_INVITE("seller-approval-invite"),
    SELLER_ADMIN_WELCOME("seller-admin-welcome");

    private final String templateName;

    SellerAdminEmailType(String templateName) {
        this.templateName = templateName;
    }

    /**
     * SES 템플릿 이름을 반환합니다.
     *
     * @return SES 템플릿 이름
     */
    public String templateName() {
        return templateName;
    }
}
