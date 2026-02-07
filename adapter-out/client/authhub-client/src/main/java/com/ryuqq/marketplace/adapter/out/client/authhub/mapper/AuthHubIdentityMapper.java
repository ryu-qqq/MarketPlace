package com.ryuqq.marketplace.adapter.out.client.authhub.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingRequest;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingResponse;
import com.ryuqq.marketplace.application.seller.dto.response.SellerIdentityProvisioningResult;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Component;

/**
 * AuthHub Identity Mapper.
 *
 * <p>Application DTO와 AuthHub SDK 객체 간의 변환을 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class AuthHubIdentityMapper {

    private final ObjectMapper objectMapper;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "Spring-managed singleton bean, immutable after injection")
    public AuthHubIdentityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // ========== Request 변환 ==========

    /**
     * Outbox 페이로드를 SDK 요청으로 변환합니다.
     *
     * @param payload JSON 페이로드
     * @return SDK Tenant Onboarding 요청
     * @throws IllegalArgumentException 페이로드 파싱 실패 시
     */
    public TenantOnboardingRequest toTenantOnboardingRequest(String payload) {
        try {
            return objectMapper.readValue(payload, TenantOnboardingRequest.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse outbox payload: " + e.getMessage(), e);
        }
    }

    // ========== Response 변환 ==========

    /**
     * SDK 응답을 성공 결과로 변환합니다.
     *
     * @param response SDK Tenant Onboarding 응답
     * @return Seller Identity Provisioning 성공 결과
     */
    public SellerIdentityProvisioningResult toSuccessResult(TenantOnboardingResponse response) {
        return SellerIdentityProvisioningResult.success(
                response.tenantId(), response.organizationId());
    }

    /**
     * 영구 실패 결과를 생성합니다.
     *
     * @param errorCode 에러 코드
     * @param errorMessage 에러 메시지
     * @return Seller Identity Provisioning 영구 실패 결과
     */
    public SellerIdentityProvisioningResult toPermanentFailure(
            String errorCode, String errorMessage) {
        return SellerIdentityProvisioningResult.permanentFailure(errorCode, errorMessage);
    }

    /**
     * 재시도 가능한 실패 결과를 생성합니다.
     *
     * @param errorCode 에러 코드
     * @param errorMessage 에러 메시지
     * @return Seller Identity Provisioning 재시도 가능 실패 결과
     */
    public SellerIdentityProvisioningResult toRetryableFailure(
            String errorCode, String errorMessage) {
        return SellerIdentityProvisioningResult.retryableFailure(errorCode, errorMessage);
    }
}
