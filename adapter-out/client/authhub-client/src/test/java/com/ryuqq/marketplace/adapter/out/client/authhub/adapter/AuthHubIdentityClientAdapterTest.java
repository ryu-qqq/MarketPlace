package com.ryuqq.marketplace.adapter.out.client.authhub.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.ryuqq.authhub.sdk.api.OnboardingApi;
import com.ryuqq.authhub.sdk.exception.AuthHubBadRequestException;
import com.ryuqq.authhub.sdk.exception.AuthHubException;
import com.ryuqq.authhub.sdk.exception.AuthHubServerException;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingRequest;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingResponse;
import com.ryuqq.marketplace.adapter.out.client.authhub.mapper.AuthHubIdentityMapper;
import com.ryuqq.marketplace.application.seller.dto.response.SellerIdentityProvisioningResult;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * AuthHubIdentityClientAdapter 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthHubIdentityClientAdapter 단위 테스트")
class AuthHubIdentityClientAdapterTest {

    @InjectMocks private AuthHubIdentityClientAdapter sut;

    @Mock private OnboardingApi onboardingApi;
    @Mock private AuthHubIdentityMapper mapper;

    private static final String DEFAULT_TENANT_ID = "tenant-123";
    private static final String DEFAULT_ORGANIZATION_ID = "org-123";

    @Nested
    @DisplayName("provisionSellerIdentity() - 셀러 ID 프로비저닝")
    class ProvisionSellerIdentityTest {

        @Test
        @DisplayName("프로비저닝 성공 시 성공 결과를 반환한다")
        @SuppressWarnings("unchecked")
        void provisionSellerIdentity_Success() {
            // given
            String payload = createValidPayload();
            SellerAuthOutbox outbox = createOutbox(payload);
            TenantOnboardingRequest request = createOnboardingRequest();
            TenantOnboardingResponse response =
                    new TenantOnboardingResponse(DEFAULT_TENANT_ID, DEFAULT_ORGANIZATION_ID);
            ApiResponse<TenantOnboardingResponse> apiResponse = mock(ApiResponse.class);
            given(apiResponse.data()).willReturn(response);

            SellerIdentityProvisioningResult expectedResult =
                    SellerIdentityProvisioningResult.success(
                            DEFAULT_TENANT_ID, DEFAULT_ORGANIZATION_ID);

            given(mapper.toTenantOnboardingRequest(payload)).willReturn(request);
            given(onboardingApi.onboard(request, outbox.idempotencyKeyValue()))
                    .willReturn(apiResponse);
            given(mapper.toSuccessResult(response)).willReturn(expectedResult);

            // when
            SellerIdentityProvisioningResult result = sut.provisionSellerIdentity(outbox);

            // then
            assertThat(result.success()).isTrue();
            assertThat(result.tenantId()).isEqualTo(DEFAULT_TENANT_ID);
            assertThat(result.organizationId()).isEqualTo(DEFAULT_ORGANIZATION_ID);
            then(onboardingApi).should().onboard(request, outbox.idempotencyKeyValue());
        }

        @Test
        @DisplayName("BadRequest 예외 시 영구 실패 결과를 반환한다")
        void provisionSellerIdentity_BadRequest_ReturnsPermanentFailure() {
            // given
            String payload = createValidPayload();
            SellerAuthOutbox outbox = createOutbox(payload);
            TenantOnboardingRequest request = createOnboardingRequest();
            SellerIdentityProvisioningResult expectedResult =
                    SellerIdentityProvisioningResult.permanentFailure(
                            "BAD_REQUEST", "Invalid request");

            given(mapper.toTenantOnboardingRequest(payload)).willReturn(request);
            given(onboardingApi.onboard(request, outbox.idempotencyKeyValue()))
                    .willThrow(new AuthHubBadRequestException("BAD_REQUEST", "Invalid request"));
            given(mapper.toPermanentFailure(eq("BAD_REQUEST"), anyString()))
                    .willReturn(expectedResult);

            // when
            SellerIdentityProvisioningResult result = sut.provisionSellerIdentity(outbox);

            // then
            assertThat(result.success()).isFalse();
            assertThat(result.retryable()).isFalse();
            assertThat(result.errorCode()).isEqualTo("BAD_REQUEST");
        }

        @Test
        @DisplayName("ServerError 예외 시 재시도 가능 실패 결과를 반환한다")
        void provisionSellerIdentity_ServerError_ReturnsRetryableFailure() {
            // given
            String payload = createValidPayload();
            SellerAuthOutbox outbox = createOutbox(payload);
            TenantOnboardingRequest request = createOnboardingRequest();
            SellerIdentityProvisioningResult expectedResult =
                    SellerIdentityProvisioningResult.retryableFailure(
                            "SERVER_ERROR", "Server error");

            given(mapper.toTenantOnboardingRequest(payload)).willReturn(request);
            given(onboardingApi.onboard(request, outbox.idempotencyKeyValue()))
                    .willThrow(new AuthHubServerException(500, "SERVER_ERROR", "Server error"));
            given(mapper.toRetryableFailure(eq("SERVER_ERROR"), anyString()))
                    .willReturn(expectedResult);

            // when
            SellerIdentityProvisioningResult result = sut.provisionSellerIdentity(outbox);

            // then
            assertThat(result.success()).isFalse();
            assertThat(result.retryable()).isTrue();
            assertThat(result.errorCode()).isEqualTo("SERVER_ERROR");
        }

        @Test
        @DisplayName("일반 AuthHub 예외 시 영구 실패 결과를 반환한다")
        void provisionSellerIdentity_AuthHubError_ReturnsPermanentFailure() {
            // given
            String payload = createValidPayload();
            SellerAuthOutbox outbox = createOutbox(payload);
            TenantOnboardingRequest request = createOnboardingRequest();
            SellerIdentityProvisioningResult expectedResult =
                    SellerIdentityProvisioningResult.permanentFailure(
                            "AUTHHUB_ERROR", "Unknown error");

            given(mapper.toTenantOnboardingRequest(payload)).willReturn(request);
            given(onboardingApi.onboard(request, outbox.idempotencyKeyValue()))
                    .willThrow(new AuthHubException(500, "AUTHHUB_ERROR", "Unknown error"));
            given(mapper.toPermanentFailure(eq("AUTHHUB_ERROR"), anyString()))
                    .willReturn(expectedResult);

            // when
            SellerIdentityProvisioningResult result = sut.provisionSellerIdentity(outbox);

            // then
            assertThat(result.success()).isFalse();
            assertThat(result.errorCode()).isEqualTo("AUTHHUB_ERROR");
        }

        private String createValidPayload() {
            return """
                   {
                       "tenantName": "Test Tenant",
                       "organizationName": "Test Organization"
                   }
                   """;
        }

        private SellerAuthOutbox createOutbox(String payload) {
            return SellerAuthOutbox.forNew(payload, Instant.now());
        }

        private TenantOnboardingRequest createOnboardingRequest() {
            return new TenantOnboardingRequest("Test Tenant", "Test Organization");
        }
    }
}
