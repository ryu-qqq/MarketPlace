package com.ryuqq.marketplace.application.legacyauth.dto.result;

/**
 * 레거시 셀러 인증 정보 조회 결과.
 *
 * <p>luxurydb의 administrators + admin_auth_group + auth_group + seller 4개 테이블 조인 결과.
 *
 * @param sellerId 셀러 ID (luxurydb seller.seller_id)
 * @param email 관리자 이메일 (administrators.EMAIL)
 * @param passwordHash BCrypt 해시 (administrators.PASSWORD_HASH)
 * @param roleType 역할 (auth_group.AUTH_GROUP_TYPE: MASTER, SELLER)
 * @param approvalStatus 승인 상태 (seller.APPROVAL_STATUS: APPROVED, PENDING 등)
 */
public record LegacySellerAuthResult(
        long sellerId, String email, String passwordHash, String roleType, String approvalStatus) {

    public boolean isApproved() {
        return "APPROVED".equals(approvalStatus);
    }
}
