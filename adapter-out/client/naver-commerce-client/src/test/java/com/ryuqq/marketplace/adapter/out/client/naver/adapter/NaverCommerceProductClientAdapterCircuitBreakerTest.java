package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.client.naver.auth.NaverCommerceTokenManager;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceProductMapper;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.domain.sellersaleschannel.SellerSalesChannelFixtures;
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
@DisplayName("NaverCommerceProductClientAdapter Circuit Breaker 테스트")
class NaverCommerceProductClientAdapterCircuitBreakerTest {

    @Mock private RestClient restClient;
    @Mock private NaverCommerceTokenManager tokenManager;
    @Mock private NaverCommerceProductMapper mapper;

    private CircuitBreaker circuitBreaker;
    private NaverCommerceProductClientAdapter sut;

    @BeforeEach
    void setUp() {
        CircuitBreakerConfig config =
                CircuitBreakerConfig.custom()
                        .failureRateThreshold(50)
                        .slidingWindowSize(2)
                        .minimumNumberOfCalls(2)
                        .permittedNumberOfCallsInHalfOpenState(1)
                        .build();

        circuitBreaker = CircuitBreakerRegistry.of(config).circuitBreaker("test-naver-product");

        sut =
                new NaverCommerceProductClientAdapter(
                        restClient, tokenManager, mapper, circuitBreaker);
    }

    @Nested
    @DisplayName("deleteProduct() - Circuit Breaker OPEN 시 예외 변환")
    class DeleteProductCircuitBreakerTest {

        @Test
        @DisplayName("CB OPEN 상태에서 deleteProduct() 호출 시 ExternalServiceUnavailableException이 발생한다")
        void deleteProduct_WhenCircuitBreakerOpen_ThrowsExternalServiceUnavailableException() {
            // given
            circuitBreaker.transitionToOpenState();

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

        @Test
        @DisplayName(
                "CB OPEN 상태에서 발생하는 ExternalServiceUnavailableException의 원인은"
                        + " CallNotPermittedException이다")
        void deleteProduct_WhenCircuitBreakerOpen_CauseIsCallNotPermittedException() {
            // given
            circuitBreaker.transitionToOpenState();

            // when & then
            assertThatThrownBy(
                            () ->
                                    sut.deleteProduct(
                                            "12345",
                                            SellerSalesChannelFixtures
                                                    .connectedSellerSalesChannel()))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasCauseInstanceOf(CallNotPermittedException.class);
        }
    }

    @Test
    @DisplayName("channelCode()는 'NAVER'를 반환한다")
    void channelCode_ReturnsNaver() {
        assertThat(sut.channelCode()).isEqualTo("NAVER");
    }
}
