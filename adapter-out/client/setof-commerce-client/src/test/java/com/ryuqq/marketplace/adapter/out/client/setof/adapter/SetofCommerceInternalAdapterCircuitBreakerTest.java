package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupBasicInfoUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductPriceUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductStockUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductsUpdateRequest;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
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
@DisplayName("세토프 내부 어댑터 Circuit Breaker 테스트")
class SetofCommerceInternalAdapterCircuitBreakerTest {

    @Mock private RestClient restClient;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        CircuitBreakerConfig config =
                CircuitBreakerConfig.custom()
                        .failureRateThreshold(50)
                        .slidingWindowSize(2)
                        .minimumNumberOfCalls(2)
                        .permittedNumberOfCallsInHalfOpenState(1)
                        .build();

        circuitBreaker = CircuitBreakerRegistry.of(config).circuitBreaker("test-setof-internal");
        circuitBreaker.transitionToOpenState();
    }

    @Nested
    @DisplayName("SetofCommerceBasicInfoAdapter - CB OPEN 시 예외 변환")
    class BasicInfoAdapterTest {

        @Test
        @DisplayName(
                "CB OPEN 상태에서 updateBasicInfo() 호출 시 ExternalServiceUnavailableException이 발생한다")
        void updateBasicInfo_WhenCircuitBreakerOpen_ThrowsExternalServiceUnavailableException() {
            SetofCommerceBasicInfoAdapter adapter =
                    new SetofCommerceBasicInfoAdapter(restClient, circuitBreaker);

            assertThatThrownBy(
                            () ->
                                    adapter.updateBasicInfo(
                                            "100",
                                            new SetofProductGroupBasicInfoUpdateRequest(
                                                    "테스트 상품", 1L, 1L, 1L, 1L)))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN")
                    .hasCauseInstanceOf(CallNotPermittedException.class);
        }
    }

    @Nested
    @DisplayName("SetofCommerceProductAdapter - CB OPEN 시 예외 변환")
    class ProductAdapterTest {

        private SetofCommerceProductAdapter adapter;

        @BeforeEach
        void setUp() {
            adapter = new SetofCommerceProductAdapter(restClient, circuitBreaker);
        }

        @Test
        @DisplayName("CB OPEN 상태에서 updatePrice() 호출 시 ExternalServiceUnavailableException이 발생한다")
        void updatePrice_WhenCircuitBreakerOpen_ThrowsExternalServiceUnavailableException() {
            assertThatThrownBy(
                            () ->
                                    adapter.updatePrice(
                                            1L, new SetofProductPriceUpdateRequest(10000, 9000)))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN")
                    .hasCauseInstanceOf(CallNotPermittedException.class);
        }

        @Test
        @DisplayName("CB OPEN 상태에서 updateStock() 호출 시 ExternalServiceUnavailableException이 발생한다")
        void updateStock_WhenCircuitBreakerOpen_ThrowsExternalServiceUnavailableException() {
            assertThatThrownBy(
                            () -> adapter.updateStock(1L, new SetofProductStockUpdateRequest(100)))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN")
                    .hasCauseInstanceOf(CallNotPermittedException.class);
        }

        @Test
        @DisplayName("CB OPEN 상태에서 updateProducts() 호출 시 ExternalServiceUnavailableException이 발생한다")
        void updateProducts_WhenCircuitBreakerOpen_ThrowsExternalServiceUnavailableException() {
            assertThatThrownBy(
                            () ->
                                    adapter.updateProducts(
                                            1L,
                                            new SetofProductsUpdateRequest(List.of(), List.of())))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN")
                    .hasCauseInstanceOf(CallNotPermittedException.class);
        }
    }
}
