package com.ryuqq.marketplace.application.selleradmin.dto.response;

/**
 * 셀러 관리자 Identity 프로비저닝 결과.
 *
 * <p>인증 서버에 사용자 등록 요청 결과를 담습니다.
 *
 * @param success 성공 여부
 * @param authUserId 생성된 인증 사용자 ID (성공 시)
 * @param errorCode 에러 코드 (실패 시)
 * @param errorMessage 에러 메시지 (실패 시)
 * @param retryable 재시도 가능 여부 (실패 시)
 */
public record SellerAdminIdentityProvisioningResult(
        boolean success,
        String authUserId,
        String errorCode,
        String errorMessage,
        boolean retryable) {

    public static SellerAdminIdentityProvisioningResult success(String authUserId) {
        return new SellerAdminIdentityProvisioningResult(true, authUserId, null, null, false);
    }

    public static SellerAdminIdentityProvisioningResult retryableFailure(
            String errorCode, String errorMessage) {
        return new SellerAdminIdentityProvisioningResult(
                false, null, errorCode, errorMessage, true);
    }

    public static SellerAdminIdentityProvisioningResult permanentFailure(
            String errorCode, String errorMessage) {
        return new SellerAdminIdentityProvisioningResult(
                false, null, errorCode, errorMessage, false);
    }
}
