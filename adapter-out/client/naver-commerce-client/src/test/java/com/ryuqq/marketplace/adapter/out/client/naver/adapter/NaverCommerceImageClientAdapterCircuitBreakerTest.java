package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ryuqq.marketplace.adapter.out.client.naver.client.NaverCommerceApiClient;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("NaverCommerceImageClientAdapter 테스트")
class NaverCommerceImageClientAdapterCircuitBreakerTest {

    @Mock private NaverCommerceApiClient apiClient;

    private NaverCommerceImageClientAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new NaverCommerceImageClientAdapter(apiClient);
    }

    @Nested
    @DisplayName("uploadBytes() - ApiClient 위임")
    class UploadBytesCircuitBreakerTest {

        @Test
        @DisplayName("ApiClient에서 ExternalServiceUnavailableException 발생 시 그대로 전파된다")
        void uploadBytes_WhenApiClientThrowsUnavailable_Propagates() {
            // given
            when(apiClient.uploadImages(any()))
                    .thenThrow(
                            new ExternalServiceUnavailableException("Circuit Breaker OPEN", null));
            byte[] imageBytes = new byte[] {0x01, 0x02, 0x03};

            // when & then
            assertThatThrownBy(() -> sut.uploadBytes(imageBytes, "test.jpg", "image/jpeg"))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN");
        }
    }

    @Nested
    @DisplayName("uploadFromUrls() - ApiClient 위임")
    class UploadFromUrlsCircuitBreakerTest {

        @Test
        @DisplayName("ApiClient에서 ExternalServiceUnavailableException 발생 시 uploadBytes에서도 전파된다")
        void uploadFromUrls_WhenApiClientThrowsUnavailable_PropagatesViaUploadBytes() {
            // given
            when(apiClient.uploadImages(any()))
                    .thenThrow(
                            new ExternalServiceUnavailableException("Circuit Breaker OPEN", null));

            // when & then
            assertThatThrownBy(() -> sut.uploadBytes(new byte[] {1}, "t.jpg", "image/jpeg"))
                    .isInstanceOf(ExternalServiceUnavailableException.class);
        }
    }
}
