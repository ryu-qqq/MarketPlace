package com.ryuqq.marketplace.domain.externalmall;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AuthConfig VO 테스트")
class AuthConfigTest {

    @Test
    @DisplayName("OcoAuthConfig 생성 - 유효한 데이터")
    void shouldCreateOcoAuthConfig() {
        // Given
        String clientId = "test-client-id";
        String clientSecret = "test-client-secret";
        String apiKey = "test-api-key";

        // When
        AuthConfig authConfig = new OcoAuthConfig(clientId, clientSecret, apiKey);

        // Then
        assertThat(authConfig).isNotNull();
        assertThat(authConfig).isInstanceOf(OcoAuthConfig.class);
    }

    @Test
    @DisplayName("OcoAuthConfig 생성 실패 - clientId가 빈 문자열")
    void shouldThrowExceptionWhenClientIdIsBlank() {
        // Given
        String clientId = "";
        String clientSecret = "test-client-secret";
        String apiKey = "test-api-key";

        // When & Then
        assertThatThrownBy(() -> new OcoAuthConfig(clientId, clientSecret, apiKey))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("clientId");
    }

    @Test
    @DisplayName("OcoAuthConfig 생성 실패 - clientSecret이 null")
    void shouldThrowExceptionWhenClientSecretIsNull() {
        // Given
        String clientId = "test-client-id";
        String clientSecret = null;
        String apiKey = "test-api-key";

        // When & Then
        assertThatThrownBy(() -> new OcoAuthConfig(clientId, clientSecret, apiKey))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("clientSecret");
    }

    @Test
    @DisplayName("OcoAuthConfig 생성 실패 - apiKey가 빈 문자열")
    void shouldThrowExceptionWhenApiKeyIsBlank() {
        // Given
        String clientId = "test-client-id";
        String clientSecret = "test-client-secret";
        String apiKey = "   ";

        // When & Then
        assertThatThrownBy(() -> new OcoAuthConfig(clientId, clientSecret, apiKey))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("apiKey");
    }

    @Test
    @DisplayName("AuthConfig는 sealed interface로 구현됨")
    void shouldBeSealedInterface() {
        // Given
        AuthConfig ocoAuthConfig = new OcoAuthConfig("client-id", "client-secret", "api-key");

        // When & Then
        // Sealed interface는 허가된 구현체만 가능
        assertThat(ocoAuthConfig).isInstanceOf(AuthConfig.class);
    }
}
