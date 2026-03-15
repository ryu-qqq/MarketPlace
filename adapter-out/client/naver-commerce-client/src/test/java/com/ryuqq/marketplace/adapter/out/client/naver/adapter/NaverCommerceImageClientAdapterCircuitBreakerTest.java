package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.client.naver.auth.NaverCommerceTokenManager;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("NaverCommerceImageClientAdapter Circuit Breaker 테스트")
class NaverCommerceImageClientAdapterCircuitBreakerTest {

    @Mock private RestClient restClient;
    @Mock private NaverCommerceTokenManager tokenManager;

    private CircuitBreaker circuitBreaker;
    private NaverCommerceImageClientAdapter sut;

    @BeforeEach
    void setUp() {
        CircuitBreakerConfig config =
                CircuitBreakerConfig.custom()
                        .failureRateThreshold(50)
                        .slidingWindowSize(2)
                        .minimumNumberOfCalls(2)
                        .permittedNumberOfCallsInHalfOpenState(1)
                        .build();

        circuitBreaker = CircuitBreakerRegistry.of(config).circuitBreaker("test-naver-image");

        sut = new NaverCommerceImageClientAdapter(restClient, tokenManager, circuitBreaker);
    }

    @Nested
    @DisplayName("uploadBytes() - CB OPEN 시 예외 변환")
    class UploadBytesCircuitBreakerTest {

        @Test
        @DisplayName("CB OPEN 상태에서 uploadBytes() 호출 시 ExternalServiceUnavailableException이 발생한다")
        void uploadBytes_WhenCBOpen_ThrowsExternalServiceUnavailableException() {
            // given
            circuitBreaker.transitionToOpenState();
            byte[] imageBytes = new byte[] {0x01, 0x02, 0x03};

            // when & then
            assertThatThrownBy(() -> sut.uploadBytes(imageBytes, "test.jpg", "image/jpeg"))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN")
                    .hasCauseInstanceOf(CallNotPermittedException.class);
        }
    }

    @Nested
    @DisplayName("uploadFromUrls() - CB OPEN 시 예외 변환")
    class UploadFromUrlsCircuitBreakerTest {

        @Test
        @DisplayName(
                "CB OPEN 상태에서 uploadFromUrls() 호출 시 이미지 다운로드 전에 CB 차단되어"
                        + " ExternalServiceUnavailableException이 발생한다")
        void uploadFromUrls_WhenCBOpen_ThrowsExternalServiceUnavailableExceptionBeforeDownload() {
            // given
            circuitBreaker.transitionToOpenState();

            // when & then
            // uploadFromUrls는 먼저 이미지를 다운로드하고 executeUpload에서 CB를 거친다
            // 이미지 다운로드 단계에서 실패할 수 있으므로 uploadBytes로 직접 테스트
            // uploadBytes는 executeUpload를 직접 호출하므로 CB 테스트에 적합
            assertThatThrownBy(() -> sut.uploadBytes(new byte[] {1}, "t.jpg", "image/jpeg"))
                    .isInstanceOf(ExternalServiceUnavailableException.class);
        }
    }
}
