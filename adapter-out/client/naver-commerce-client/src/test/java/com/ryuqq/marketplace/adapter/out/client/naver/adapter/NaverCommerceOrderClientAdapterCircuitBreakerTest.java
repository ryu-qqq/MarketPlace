package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.client.naver.auth.NaverCommerceTokenManager;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceOrderMapper;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Instant;
import java.util.List;
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
@DisplayName("NaverCommerceOrderClientAdapter Circuit Breaker 테스트")
class NaverCommerceOrderClientAdapterCircuitBreakerTest {

    @Mock private RestClient restClient;
    @Mock private NaverCommerceTokenManager tokenManager;
    @Mock private NaverCommerceOrderMapper mapper;

    private CircuitBreaker circuitBreaker;
    private NaverCommerceOrderClientAdapter sut;

    @BeforeEach
    void setUp() {
        CircuitBreakerConfig config =
                CircuitBreakerConfig.custom()
                        .failureRateThreshold(50)
                        .slidingWindowSize(2)
                        .minimumNumberOfCalls(2)
                        .permittedNumberOfCallsInHalfOpenState(1)
                        .build();

        circuitBreaker = CircuitBreakerRegistry.of(config).circuitBreaker("test-naver-order");

        sut = new NaverCommerceOrderClientAdapter(restClient, tokenManager, mapper, circuitBreaker);
    }

    @Nested
    @DisplayName("getLastChangedStatuses() - CB OPEN 시 예외 변환")
    class GetLastChangedStatusesTest {

        @Test
        @DisplayName(
                "CB OPEN 상태에서 getLastChangedStatuses() 호출 시 ExternalServiceUnavailableException이"
                        + " 발생한다")
        void getLastChangedStatuses_WhenCBOpen_ThrowsExternalServiceUnavailableException() {
            // given
            circuitBreaker.transitionToOpenState();
            Instant from = Instant.now().minusSeconds(3600);
            Instant to = Instant.now();

            // when & then
            assertThatThrownBy(() -> sut.getLastChangedStatuses(from, to, null))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN")
                    .hasCauseInstanceOf(CallNotPermittedException.class);
        }
    }

    @Nested
    @DisplayName("queryProductOrders() - CB OPEN 시 예외 변환")
    class QueryProductOrdersTest {

        @Test
        @DisplayName(
                "CB OPEN 상태에서 queryProductOrders() 호출 시 ExternalServiceUnavailableException이 발생한다")
        void queryProductOrders_WhenCBOpen_ThrowsExternalServiceUnavailableException() {
            // given
            circuitBreaker.transitionToOpenState();

            // when & then
            assertThatThrownBy(() -> sut.queryProductOrders(List.of("order-1", "order-2")))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN")
                    .hasCauseInstanceOf(CallNotPermittedException.class);
        }
    }
}
