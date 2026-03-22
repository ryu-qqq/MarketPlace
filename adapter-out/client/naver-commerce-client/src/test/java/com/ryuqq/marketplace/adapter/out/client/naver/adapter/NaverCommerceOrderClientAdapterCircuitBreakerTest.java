package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.ryuqq.marketplace.adapter.out.client.naver.client.NaverCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceOrderMapper;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
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

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("NaverCommerceOrderClientAdapter 테스트")
class NaverCommerceOrderClientAdapterCircuitBreakerTest {

    @Mock private NaverCommerceApiClient apiClient;
    @Mock private NaverCommerceOrderMapper mapper;

    private NaverCommerceOrderClientAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new NaverCommerceOrderClientAdapter(apiClient, mapper);
    }

    @Nested
    @DisplayName("getLastChangedStatuses() - ApiClient 위임")
    class GetLastChangedStatusesTest {

        @Test
        @DisplayName("ApiClient에서 ExternalServiceUnavailableException 발생 시 그대로 전파된다")
        void getLastChangedStatuses_WhenApiClientThrowsUnavailable_Propagates() {
            // given
            when(apiClient.getLastChangedStatuses(
                            anyString(), anyString(), anyString(), anyInt(), any()))
                    .thenThrow(
                            new ExternalServiceUnavailableException("Circuit Breaker OPEN", null));
            Instant from = Instant.now().minusSeconds(3600);
            Instant to = Instant.now();

            // when & then
            assertThatThrownBy(() -> sut.getLastChangedStatuses(from, to, null))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN");
        }
    }

    @Nested
    @DisplayName("queryProductOrders() - ApiClient 위임")
    class QueryProductOrdersTest {

        @Test
        @DisplayName("ApiClient에서 ExternalServiceUnavailableException 발생 시 그대로 전파된다")
        void queryProductOrders_WhenApiClientThrowsUnavailable_Propagates() {
            // given
            when(apiClient.queryProductOrders(any()))
                    .thenThrow(
                            new ExternalServiceUnavailableException("Circuit Breaker OPEN", null));

            // when & then
            assertThatThrownBy(() -> sut.queryProductOrders(List.of("order-1", "order-2")))
                    .isInstanceOf(ExternalServiceUnavailableException.class)
                    .hasMessageContaining("Circuit Breaker OPEN");
        }
    }
}
