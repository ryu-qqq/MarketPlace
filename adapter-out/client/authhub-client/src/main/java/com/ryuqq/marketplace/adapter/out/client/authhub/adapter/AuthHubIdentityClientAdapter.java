package com.ryuqq.marketplace.adapter.out.client.authhub.adapter;

import com.ryuqq.authhub.sdk.api.OnboardingApi;
import com.ryuqq.authhub.sdk.exception.AuthHubBadRequestException;
import com.ryuqq.authhub.sdk.exception.AuthHubException;
import com.ryuqq.authhub.sdk.exception.AuthHubServerException;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingRequest;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingResponse;
import com.ryuqq.marketplace.adapter.out.client.authhub.mapper.AuthHubIdentityMapper;
import com.ryuqq.marketplace.application.seller.dto.response.SellerIdentityProvisioningResult;
import com.ryuqq.marketplace.application.seller.port.out.client.IdentityClient;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * AuthHub Identity 서비스 클라이언트 어댑터.
 *
 * <p>AuthHub SDK를 사용하여 Tenant/Organization을 생성합니다.
 *
 * <p>Outbox 패턴과 함께 사용되며, 멱등키를 통해 중복 요청을 방지합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnBean(OnboardingApi.class)
public class AuthHubIdentityClientAdapter implements IdentityClient {

    private final OnboardingApi onboardingApi;
    private final AuthHubIdentityMapper mapper;

    public AuthHubIdentityClientAdapter(OnboardingApi onboardingApi, AuthHubIdentityMapper mapper) {
        this.onboardingApi = onboardingApi;
        this.mapper = mapper;
    }

    @Override
    public SellerIdentityProvisioningResult provisionSellerIdentity(SellerAuthOutbox outbox) {
        TenantOnboardingRequest request = mapper.toTenantOnboardingRequest(outbox.payload());
        String idempotencyKey = outbox.idempotencyKeyValue();

        try {
            ApiResponse<TenantOnboardingResponse> response =
                    onboardingApi.onboard(request, idempotencyKey);

            return mapper.toSuccessResult(response.data());

        } catch (AuthHubBadRequestException e) {
            return mapper.toPermanentFailure("BAD_REQUEST", e.getMessage());

        } catch (AuthHubServerException e) {
            return mapper.toRetryableFailure("SERVER_ERROR", e.getMessage());

        } catch (AuthHubException e) {
            boolean retryable = isRetryableError(e);
            if (retryable) {
                return mapper.toRetryableFailure("AUTHHUB_ERROR", e.getMessage());
            }
            return mapper.toPermanentFailure("AUTHHUB_ERROR", e.getMessage());
        }
    }

    private boolean isRetryableError(AuthHubException e) {
        return e instanceof AuthHubServerException;
    }
}
