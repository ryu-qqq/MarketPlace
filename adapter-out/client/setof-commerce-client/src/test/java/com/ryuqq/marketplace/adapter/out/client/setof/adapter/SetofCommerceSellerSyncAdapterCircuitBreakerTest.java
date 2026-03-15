package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceSellerSyncMapper;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
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
@DisplayName("SetofCommerceSellerSyncAdapter Circuit Breaker 테스트")
class SetofCommerceSellerSyncAdapterCircuitBreakerTest {

    @Mock private RestClient restClient;
    @Mock private SellerReadManager sellerReadManager;
    @Mock private SetofCommerceSellerSyncMapper mapper;

    private CircuitBreaker circuitBreaker;
    private SetofCommerceSellerSyncAdapter sut;

    @BeforeEach
    void setUp() {
        CircuitBreakerConfig config =
                CircuitBreakerConfig.custom()
                        .failureRateThreshold(50)
                        .slidingWindowSize(2)
                        .minimumNumberOfCalls(2)
                        .permittedNumberOfCallsInHalfOpenState(1)
                        .build();

        circuitBreaker = CircuitBreakerRegistry.of(config).circuitBreaker("test-setof-seller-sync");

        sut =
                new SetofCommerceSellerSyncAdapter(
                        restClient, sellerReadManager, mapper, circuitBreaker);
    }

    @Nested
    @DisplayName("createSeller() - Circuit Breaker OPEN 시 예외 변환")
    class CreateSellerCircuitBreakerTest {

        @Test
        @DisplayName("CB OPEN 상태에서 createSeller() 호출 시 ExternalServiceUnavailableException이 발생한다")
        void createSeller_WhenCircuitBreakerOpen_ThrowsExternalServiceUnavailableException() {
            // given
            circuitBreaker.transitionToOpenState();

            // when & then
            assertThatThrownBy(() -> sut.createSeller(1L))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN");
        }

        @Test
        @DisplayName(
                "CB OPEN 상태에서 발생하는 ExternalServiceUnavailableException의 원인은"
                        + " CallNotPermittedException이다")
        void createSeller_WhenCircuitBreakerOpen_CauseIsCallNotPermittedException() {
            // given
            circuitBreaker.transitionToOpenState();

            // when & then
            assertThatThrownBy(() -> sut.createSeller(1L))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasCauseInstanceOf(CallNotPermittedException.class);
        }
    }

    @Nested
    @DisplayName("updateSeller() - Circuit Breaker OPEN 시 예외 변환")
    class UpdateSellerCircuitBreakerTest {

        @Test
        @DisplayName("CB OPEN 상태에서 updateSeller() 호출 시 ExternalServiceUnavailableException이 발생한다")
        void updateSeller_WhenCircuitBreakerOpen_ThrowsExternalServiceUnavailableException() {
            // given
            circuitBreaker.transitionToOpenState();

            // when & then
            assertThatThrownBy(() -> sut.updateSeller(1L))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN");
        }
    }
}
