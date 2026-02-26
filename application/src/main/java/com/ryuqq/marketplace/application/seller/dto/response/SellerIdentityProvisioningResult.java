package com.ryuqq.marketplace.application.seller.dto.response;

/**
 * 셀러 Identity 프로비저닝 결과.
 *
 * @param success 성공 여부
 * @param tenantId 생성된 Tenant ID (성공 시)
 * @param organizationId 생성된 Organization ID (성공 시)
 * @param errorCode 에러 코드 (실패 시)
 * @param errorMessage 에러 메시지 (실패 시)
 * @param retryable 재시도 가능 여부 (실패 시)
 */
public record SellerIdentityProvisioningResult(
        boolean success,
        String tenantId,
        String organizationId,
        String errorCode,
        String errorMessage,
        boolean retryable) {

    public static SellerIdentityProvisioningResult success(String tenantId, String organizationId) {
        return new SellerIdentityProvisioningResult(
                true, tenantId, organizationId, null, null, false);
    }

    public static SellerIdentityProvisioningResult retryableFailure(
            String errorCode, String errorMessage) {
        return new SellerIdentityProvisioningResult(
                false, null, null, errorCode, errorMessage, true);
    }

    public static SellerIdentityProvisioningResult permanentFailure(
            String errorCode, String errorMessage) {
        return new SellerIdentityProvisioningResult(
                false, null, null, errorCode, errorMessage, false);
    }
}
