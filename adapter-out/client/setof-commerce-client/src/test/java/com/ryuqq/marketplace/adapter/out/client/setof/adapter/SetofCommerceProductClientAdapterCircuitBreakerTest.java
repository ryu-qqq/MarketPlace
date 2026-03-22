package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceProductMapper;
import com.ryuqq.marketplace.adapter.out.client.setof.strategy.SetofProductUpdateExecutorProvider;
import com.ryuqq.marketplace.adapter.out.client.setof.support.SetofCommerceApiExecutor;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.domain.sellersaleschannel.SellerSalesChannelFixtures;
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

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SetofCommerceProductClientAdapter Circuit Breaker 테스트")
class SetofCommerceProductClientAdapterCircuitBreakerTest {

    @Mock private SetofCommerceApiClient apiClient;
    @Mock private SetofCommerceProductMapper mapper;
    @Mock private SetofProductUpdateExecutorProvider updateExecutorProvider;

    private CircuitBreaker circuitBreaker;
    private SetofCommerceApiExecutor executor;
    private SetofCommerceProductClientAdapter sut;

    @BeforeEach
    void setUp() {
        CircuitBreakerConfig config =
                CircuitBreakerConfig.custom()
                        .failureRateThreshold(50)
                        .slidingWindowSize(2)
                        .minimumNumberOfCalls(2)
                        .permittedNumberOfCallsInHalfOpenState(1)
                        .build();

        circuitBreaker =
                CircuitBreakerRegistry.of(config).circuitBreaker("test-setof-product-client");
        executor = new SetofCommerceApiExecutor(circuitBreaker);

        // SetofCommerceApiClient를 Executor 기반 실행으로 래핑
        sut = new SetofCommerceProductClientAdapter(apiClient, mapper, updateExecutorProvider);
    }

    @Nested
    @DisplayName("deleteProduct() - Circuit Breaker OPEN 시 예외 변환")
    class DeleteProductCircuitBreakerTest {

        @Test
        @DisplayName("CB OPEN 상태에서 deleteProduct() 호출 시 ExternalServiceUnavailableException이 발생한다")
        void deleteProduct_WhenCircuitBreakerOpen_ThrowsExternalServiceUnavailableException() {
            // given - ApiClient가 ExternalServiceUnavailableException을 던지도록 설정
            given(mapper.toDeleteRequest()).willReturn(null);
            willThrow(
                            new ExternalServiceUnavailableException(
                                    "세토프 커머스 서비스 일시 중단 (Circuit Breaker OPEN)",
                                    new RuntimeException()))
                    .given(apiClient)
                    .updateProduct(any(), any());

            // when & then
            assertThatThrownBy(
                            () ->
                                    sut.deleteProduct(
                                            "12345",
                                            SellerSalesChannelFixtures
                                                    .connectedSellerSalesChannel()))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN");
        }
    }

    @Test
    @DisplayName("channelCode()는 'SETOF'를 반환한다")
    void channelCode_ReturnsSetof() {
        assertThat(sut.channelCode()).isEqualTo("SETOF");
    }
}
