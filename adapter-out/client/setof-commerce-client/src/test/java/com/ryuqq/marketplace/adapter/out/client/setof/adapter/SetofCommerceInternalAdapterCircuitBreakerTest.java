package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willAnswer;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupBasicInfoUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductPriceUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductStockUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductsUpdateRequest;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 세토프 내부 어댑터가 ApiClient의 ExternalServiceUnavailableException을 올바르게 전파하는지 검증합니다.
 *
 * <p>CB 동작 자체는 SetofCommerceApiExecutor에서 처리하므로, Adapter 테스트는 예외 전파 여부만 확인합니다.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("세토프 내부 어댑터 ExternalServiceUnavailableException 전파 테스트")
class SetofCommerceInternalAdapterCircuitBreakerTest {

    @Mock private SetofCommerceApiClient apiClient;

    private ExternalServiceUnavailableException unavailableException;

    @BeforeEach
    void setUp() {
        unavailableException =
                new ExternalServiceUnavailableException(
                        "세토프 커머스 서비스 일시 중단 (Circuit Breaker OPEN)", new RuntimeException());
    }

    @Nested
    @DisplayName("SetofCommerceBasicInfoAdapter - ExternalServiceUnavailableException 전파")
    class BasicInfoAdapterTest {

        @Test
        @DisplayName("ApiClient가 ExternalServiceUnavailableException을 던지면 그대로 전파된다")
        void updateBasicInfo_WhenApiClientThrows_PropagatesException() {
            SetofCommerceBasicInfoAdapter adapter = new SetofCommerceBasicInfoAdapter(apiClient);
            willAnswer(invocation -> {
                throw unavailableException;
            }).given(apiClient).updateBasicInfo(any(), any());

            assertThatThrownBy(
                            () ->
                                    adapter.updateBasicInfo(
                                            "100",
                                            new SetofProductGroupBasicInfoUpdateRequest(
                                                    "테스트 상품", 1L, 1L, 1L, 1L)))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN");
        }
    }

    @Nested
    @DisplayName("SetofCommerceProductAdapter - ExternalServiceUnavailableException 전파")
    class ProductAdapterTest {

        private SetofCommerceProductAdapter adapter;

        @BeforeEach
        void setUp() {
            adapter = new SetofCommerceProductAdapter(apiClient);
        }

        @Test
        @DisplayName("updatePrice() 호출 시 ApiClient 예외가 전파된다")
        void updatePrice_WhenApiClientThrows_PropagatesException() {
            willAnswer(invocation -> {
                throw unavailableException;
            }).given(apiClient).updatePrice(any(), any());

            assertThatThrownBy(
                            () ->
                                    adapter.updatePrice(
                                            1L, new SetofProductPriceUpdateRequest(10000, 9000)))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN");
        }

        @Test
        @DisplayName("updateStock() 호출 시 ApiClient 예외가 전파된다")
        void updateStock_WhenApiClientThrows_PropagatesException() {
            willAnswer(invocation -> {
                throw unavailableException;
            }).given(apiClient).updateStock(any(), any());

            assertThatThrownBy(
                            () -> adapter.updateStock(1L, new SetofProductStockUpdateRequest(100)))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN");
        }

        @Test
        @DisplayName("updateProducts() 호출 시 ApiClient 예외가 전파된다")
        void updateProducts_WhenApiClientThrows_PropagatesException() {
            willAnswer(invocation -> {
                throw unavailableException;
            }).given(apiClient).updateProducts(any(), any());

            assertThatThrownBy(
                            () ->
                                    adapter.updateProducts(
                                            1L,
                                            new SetofProductsUpdateRequest(List.of(), List.of())))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN");
        }
    }
}
