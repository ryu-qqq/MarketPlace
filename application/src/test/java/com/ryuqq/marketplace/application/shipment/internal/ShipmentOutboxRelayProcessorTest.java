package com.ryuqq.marketplace.application.shipment.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxReadManager;
import com.ryuqq.marketplace.application.shipment.port.out.client.ShipmentOutboxPublishClient;
import com.ryuqq.marketplace.domain.shipment.outbox.ShipmentOutboxFixtures;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ShipmentOutboxRelayProcessor 단위 테스트")
class ShipmentOutboxRelayProcessorTest {

    @InjectMocks private ShipmentOutboxRelayProcessor sut;

    @Mock private ShipmentOutboxCommandManager outboxCommandManager;
    @Mock private ShipmentOutboxReadManager outboxReadManager;
    @Mock private ShipmentOutboxPublishClient publishClient;
    @Spy private ObjectMapper objectMapper;

    @Nested
    @DisplayName("relay() - 배송 Outbox SQS 발행")
    class RelayTest {

        @Test
        @DisplayName("SQS 발행 성공 시 PROCESSING 상태로 persist 후 true를 반환한다")
        void relay_PublishSuccess_PersistsAndReturnsTrue() {
            // given
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();
            given(publishClient.publish(anyString())).willReturn("msg-id-001");

            // when
            boolean result = sut.relay(outbox);

            // then
            assertThat(result).isTrue();
            then(outboxCommandManager).should().persist(outbox);
            then(publishClient).should().publish(anyString());
        }

        @Test
        @DisplayName("SQS 발행 실패 시 re-read 후 실패 상태로 persist하고 false를 반환한다")
        void relay_PublishFails_PersistsFailureAndReturnsFalse() {
            // given
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();
            ShipmentOutbox freshOutbox = ShipmentOutboxFixtures.processingShipmentOutbox();

            given(outboxReadManager.getById(outbox.idValue())).willReturn(freshOutbox);
            willThrow(new RuntimeException("SQS 연결 실패")).given(publishClient).publish(anyString());

            // when
            boolean result = sut.relay(outbox);

            // then
            assertThat(result).isFalse();
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("SQS 발행 실패 후 re-read도 실패하면 false를 반환하고 예외를 전파하지 않는다")
        void relay_PublishAndReReadBothFail_ReturnsFalseWithoutException() {
            // given
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();

            willThrow(new RuntimeException("SQS 연결 실패")).given(publishClient).publish(anyString());
            given(outboxReadManager.getById(outbox.idValue()))
                    .willThrow(new RuntimeException("DB 연결 실패"));

            // when
            boolean result = sut.relay(outbox);

            // then
            assertThat(result).isFalse();
        }
    }
}
