package com.ryuqq.marketplace.adapter.out.client.sellic.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.out.client.sellic.client.SellicCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.sellic.config.SellicCommerceProperties;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicShipmentResponse;
import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceServerException;
import com.ryuqq.marketplace.application.claimsync.port.out.query.ExternalOrderItemMappingQueryPort;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxType;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@DisplayName("SellicShipmentSyncStrategy 단위 테스트")
@ExtendWith(MockitoExtension.class)
class SellicShipmentSyncStrategyTest {

    @Mock SellicCommerceApiClient apiClient;
    @Mock SellicCommerceProperties properties;
    @Mock ExternalOrderItemMappingQueryPort mappingQueryPort;
    @Spy ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks SellicShipmentSyncStrategy sut;

    // ── 헬퍼 ──

    private ShipmentOutbox mockOutbox(ShipmentOutboxType type, String payload) {
        ShipmentOutbox outbox = mock(ShipmentOutbox.class);
        Mockito.lenient().when(outbox.outboxType()).thenReturn(type);
        Mockito.lenient().when(outbox.orderItemIdValue()).thenReturn("OI-001");
        if (payload != null) {
            Mockito.lenient().when(outbox.payload()).thenReturn(payload);
        }
        return outbox;
    }

    private ExternalOrderItemMapping mockMapping(String externalProductOrderId) {
        ExternalOrderItemMapping mapping = mock(ExternalOrderItemMapping.class);
        Mockito.lenient().when(mapping.externalProductOrderId()).thenReturn(externalProductOrderId);
        return mapping;
    }

    // ── 테스트 ──

    @Nested
    @DisplayName("execute()")
    class ExecuteTest {

        @Test
        @DisplayName("CONFIRM 타입은 API 호출 없이 성공 반환")
        void confirmReturnsSuccess() {
            var outbox = mockOutbox(ShipmentOutboxType.CONFIRM, null);

            OutboxSyncResult result = sut.execute(outbox);

            assertThat(result.isSuccess()).isTrue();
            verifyNoInteractions(apiClient);
        }

        @Test
        @DisplayName("DELIVER 타입은 API 호출 없이 성공 반환")
        void deliverReturnsSuccess() {
            var outbox = mockOutbox(ShipmentOutboxType.DELIVER, null);

            OutboxSyncResult result = sut.execute(outbox);

            assertThat(result.isSuccess()).isTrue();
            verifyNoInteractions(apiClient);
        }

        @Test
        @DisplayName("CANCEL 타입은 API 호출 없이 성공 반환")
        void cancelReturnsSuccess() {
            var outbox = mockOutbox(ShipmentOutboxType.CANCEL, null);

            OutboxSyncResult result = sut.execute(outbox);

            assertThat(result.isSuccess()).isTrue();
            verifyNoInteractions(apiClient);
        }

        @Test
        @DisplayName("SHIP 타입은 송장을 등록하고 성공 반환")
        void shipRegistersShipment() {
            String payload = "{\"trackingNumber\":\"12345\",\"courierCode\":\"CJ\"}";
            var outbox = mockOutbox(ShipmentOutboxType.SHIP, payload);
            var mapping = mockMapping("999");

            given(mappingQueryPort.findByOrderItemId("OI-001")).willReturn(Optional.of(mapping));
            given(properties.getCustomerId()).willReturn("test-customer");
            given(properties.getApiKey()).willReturn("test-key");
            given(apiClient.registerShipment(any()))
                    .willReturn(new SellicShipmentResponse("success", "성공", null));

            OutboxSyncResult result = sut.execute(outbox);

            assertThat(result.isSuccess()).isTrue();
            verify(apiClient).registerShipment(any());
        }

        @Test
        @DisplayName("BadRequest 예외 시 재시도 불가 실패 반환")
        void badRequestReturnsNonRetryableFailure() {
            String payload = "{\"trackingNumber\":\"12345\",\"courierCode\":\"CJ\"}";
            var outbox = mockOutbox(ShipmentOutboxType.SHIP, payload);
            var mapping = mockMapping("999");

            given(mappingQueryPort.findByOrderItemId("OI-001")).willReturn(Optional.of(mapping));
            given(properties.getCustomerId()).willReturn("test-customer");
            given(properties.getApiKey()).willReturn("test-key");
            given(apiClient.registerShipment(any()))
                    .willThrow(new SellicCommerceBadRequestException("잘못된 요청"));

            OutboxSyncResult result = sut.execute(outbox);

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.retryable()).isFalse();
        }

        @Test
        @DisplayName("ServerException 예외 시 재시도 가능 실패 반환")
        void serverExceptionReturnsRetryableFailure() {
            String payload = "{\"trackingNumber\":\"12345\",\"courierCode\":\"CJ\"}";
            var outbox = mockOutbox(ShipmentOutboxType.SHIP, payload);
            var mapping = mockMapping("999");

            given(mappingQueryPort.findByOrderItemId("OI-001")).willReturn(Optional.of(mapping));
            given(properties.getCustomerId()).willReturn("test-customer");
            given(properties.getApiKey()).willReturn("test-key");
            given(apiClient.registerShipment(any()))
                    .willThrow(new SellicCommerceServerException(500, "서버 에러"));

            OutboxSyncResult result = sut.execute(outbox);

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.retryable()).isTrue();
        }

        @Test
        @DisplayName("매핑을 찾을 수 없으면 재시도 가능 실패 반환")
        void noMappingReturnsRetryableFailure() {
            String payload = "{\"trackingNumber\":\"12345\",\"courierCode\":\"CJ\"}";
            var outbox = mockOutbox(ShipmentOutboxType.SHIP, payload);

            given(mappingQueryPort.findByOrderItemId("OI-001")).willReturn(Optional.empty());

            OutboxSyncResult result = sut.execute(outbox);

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.retryable()).isTrue();
        }
    }
}
