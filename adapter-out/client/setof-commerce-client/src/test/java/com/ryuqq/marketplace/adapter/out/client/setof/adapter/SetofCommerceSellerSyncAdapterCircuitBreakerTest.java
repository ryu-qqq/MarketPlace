package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceSellerSyncMapper;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
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

/**
 * SetofCommerceSellerSyncAdapter CB 예외 전파 테스트.
 *
 * <p>CB 동작은 ApiExecutor에서 처리. Adapter는 ExternalServiceUnavailableException이 전파되는지만 검증.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SetofCommerceSellerSyncAdapter Circuit Breaker 테스트")
class SetofCommerceSellerSyncAdapterCircuitBreakerTest {

    @Mock private SetofCommerceApiClient apiClient;
    @Mock private SellerReadManager sellerReadManager;
    @Mock private SetofCommerceSellerSyncMapper mapper;
    @Mock private Seller seller;

    private SetofCommerceSellerSyncAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new SetofCommerceSellerSyncAdapter(apiClient, sellerReadManager, mapper);
    }

    @Nested
    @DisplayName("createSeller() - ApiClient가 ExternalServiceUnavailableException 던질 때")
    class CreateSellerCircuitBreakerTest {

        @Test
        @DisplayName("createSeller() 호출 시 ExternalServiceUnavailableException이 전파된다")
        void createSeller_WhenApiClientThrows_PropagatesException() {
            // given
            given(sellerReadManager.getById(any())).willReturn(seller);
            given(mapper.toSellerRequest(any())).willReturn(null);
            given(apiClient.createSeller(any()))
                    .willThrow(
                            new ExternalServiceUnavailableException(
                                    "세토프 커머스 서비스 일시 중단 (Circuit Breaker OPEN)",
                                    new RuntimeException()));

            // when & then
            assertThatThrownBy(() -> sut.createSeller(1L))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN");
        }
    }

    @Nested
    @DisplayName("updateSeller() - ApiClient가 ExternalServiceUnavailableException 던질 때")
    class UpdateSellerCircuitBreakerTest {

        @Test
        @DisplayName("updateSeller() 호출 시 ExternalServiceUnavailableException이 전파된다")
        void updateSeller_WhenApiClientThrows_PropagatesException() {
            // given
            given(sellerReadManager.getById(any())).willReturn(seller);
            given(mapper.toSellerRequest(any())).willReturn(null);
            willThrow(
                            new ExternalServiceUnavailableException(
                                    "세토프 커머스 서비스 일시 중단 (Circuit Breaker OPEN)",
                                    new RuntimeException()))
                    .given(apiClient)
                    .updateSeller(any(), any());

            // when & then
            assertThatThrownBy(() -> sut.updateSeller(1L))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN");
        }
    }
}
