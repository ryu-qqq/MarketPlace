package com.ryuqq.marketplace.adapter.out.client.authhub.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingRequest;
import com.ryuqq.marketplace.application.seller.dto.response.SellerIdentityProvisioningResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * AuthHubIdentityMapper 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("AuthHubIdentityMapper 단위 테스트")
class AuthHubIdentityMapperTest {

    private AuthHubIdentityMapper mapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mapper = new AuthHubIdentityMapper(objectMapper);
    }

    @Nested
    @DisplayName("toTenantOnboardingRequest()")
    class ToTenantOnboardingRequestTest {

        @Test
        @DisplayName("유효한 JSON을 TenantOnboardingRequest로 변환한다")
        void toTenantOnboardingRequest_ValidJson_Success() {
            // given
            String payload =
                    """
                    {
                        "tenantName": "Test Tenant",
                        "organizationName": "Test Organization"
                    }
                    """;

            // when
            TenantOnboardingRequest request = mapper.toTenantOnboardingRequest(payload);

            // then
            assertThat(request).isNotNull();
            assertThat(request.tenantName()).isEqualTo("Test Tenant");
            assertThat(request.organizationName()).isEqualTo("Test Organization");
        }

        @Test
        @DisplayName("유효하지 않은 JSON은 예외를 발생시킨다")
        void toTenantOnboardingRequest_InvalidJson_ThrowsException() {
            // given
            String payload = "invalid json";

            // when & then
            assertThatThrownBy(() -> mapper.toTenantOnboardingRequest(payload))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Failed to parse outbox payload");
        }
    }

    @Nested
    @DisplayName("결과 변환")
    class ResultConversionTest {

        @Test
        @DisplayName("영구 실패 결과를 생성한다")
        void toPermanentFailure_Success() {
            // given
            String errorCode = "BAD_REQUEST";
            String errorMessage = "Invalid request";

            // when
            SellerIdentityProvisioningResult result =
                    mapper.toPermanentFailure(errorCode, errorMessage);

            // then
            assertThat(result.success()).isFalse();
            assertThat(result.retryable()).isFalse();
            assertThat(result.errorCode()).isEqualTo(errorCode);
            assertThat(result.errorMessage()).isEqualTo(errorMessage);
        }

        @Test
        @DisplayName("재시도 가능한 실패 결과를 생성한다")
        void toRetryableFailure_Success() {
            // given
            String errorCode = "SERVER_ERROR";
            String errorMessage = "Server error";

            // when
            SellerIdentityProvisioningResult result =
                    mapper.toRetryableFailure(errorCode, errorMessage);

            // then
            assertThat(result.success()).isFalse();
            assertThat(result.retryable()).isTrue();
            assertThat(result.errorCode()).isEqualTo(errorCode);
            assertThat(result.errorMessage()).isEqualTo(errorMessage);
        }
    }
}
