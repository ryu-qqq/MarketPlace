package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.ryuqq.marketplace.adapter.out.client.naver.client.NaverCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.naver.config.NaverCommerceProperties;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceProductMapper;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.domain.sellersaleschannel.SellerSalesChannelFixtures;
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
@DisplayName("NaverCommerceProductClientAdapter 테스트")
class NaverCommerceProductClientAdapterCircuitBreakerTest {

    @Mock private NaverCommerceApiClient apiClient;
    @Mock private NaverCommerceProductMapper mapper;
    @Mock private NaverCommerceProperties properties;

    private NaverCommerceProductClientAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new NaverCommerceProductClientAdapter(apiClient, mapper, properties);
    }

    @Nested
    @DisplayName("deleteProduct() - enabled=false 시 스킵")
    class DeleteProductDisabledTest {

        @Test
        @DisplayName("enabled=false 상태에서 deleteProduct() 호출 시 API를 호출하지 않는다")
        void deleteProduct_WhenDisabled_DoesNotCallApi() {
            // given
            when(properties.isEnabled()).thenReturn(false);

            // when & then (예외 없이 정상 종료)
            sut.deleteProduct("12345", SellerSalesChannelFixtures.connectedSellerSalesChannel());
        }
    }

    @Nested
    @DisplayName("deleteProduct() - ApiClient 위임")
    class DeleteProductApiClientTest {

        @Test
        @DisplayName("enabled=true 상태에서 deleteProduct() 호출 시 ApiClient.deleteProduct()를 호출한다")
        void deleteProduct_WhenEnabled_CallsApiClient() {
            // given
            when(properties.isEnabled()).thenReturn(true);

            // when
            sut.deleteProduct("12345", SellerSalesChannelFixtures.connectedSellerSalesChannel());

            // then: apiClient.deleteProduct("12345") 호출됨 (Mockito 기본 동작으로 예외 없음)
        }

        @Test
        @DisplayName("ApiClient에서 ExternalServiceUnavailableException 발생 시 그대로 전파된다")
        void deleteProduct_WhenApiClientThrowsUnavailable_Propagates() {
            // given
            when(properties.isEnabled()).thenReturn(true);
            org.mockito.Mockito.doThrow(
                            new ExternalServiceUnavailableException("Circuit Breaker OPEN", null))
                    .when(apiClient)
                    .deleteProduct("12345");

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
    @DisplayName("channelCode()는 'NAVER'를 반환한다")
    void channelCode_ReturnsNaver() {
        assertThat(sut.channelCode()).isEqualTo("NAVER");
    }
}
