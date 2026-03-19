package com.ryuqq.marketplace.application.shipment.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.shipment.port.out.query.ShipmentOutboxQueryPort;
import com.ryuqq.marketplace.domain.shipment.outbox.ShipmentOutboxFixtures;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ShipmentOutboxReadManager 단위 테스트")
class ShipmentOutboxReadManagerTest {

    @InjectMocks private ShipmentOutboxReadManager sut;

    @Mock private ShipmentOutboxQueryPort queryPort;

    @Nested
    @DisplayName("findPendingOutboxes() - PENDING 상태 Outbox 목록 조회")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("beforeTime 이전에 생성된 PENDING Outbox 목록을 반환한다")
        void findPendingOutboxes_ValidParams_ReturnsPendingOutboxes() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(5);
            int batchSize = 10;
            List<ShipmentOutbox> expected =
                    List.of(
                            ShipmentOutboxFixtures.pendingShipmentOutbox(),
                            ShipmentOutboxFixtures.pendingShipmentOutbox());

            given(queryPort.findPendingOutboxes(beforeTime, batchSize)).willReturn(expected);

            // when
            List<ShipmentOutbox> result = sut.findPendingOutboxes(beforeTime, batchSize);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("PENDING 상태 Outbox가 없으면 빈 목록을 반환한다")
        void findPendingOutboxes_NoPending_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(5);
            int batchSize = 10;
            given(queryPort.findPendingOutboxes(beforeTime, batchSize)).willReturn(List.of());

            // when
            List<ShipmentOutbox> result = sut.findPendingOutboxes(beforeTime, batchSize);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes() - 처리 타임아웃 Outbox 목록 조회")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃 임계값 이전에 처리 시작된 PROCESSING Outbox 목록을 반환한다")
        void findProcessingTimeoutOutboxes_ValidParams_ReturnsTimeoutOutboxes() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(300);
            int batchSize = 50;
            List<ShipmentOutbox> expected =
                    List.of(ShipmentOutboxFixtures.processingShipmentOutbox());

            given(queryPort.findProcessingTimeoutOutboxes(timeoutBefore, batchSize))
                    .willReturn(expected);

            // when
            List<ShipmentOutbox> result =
                    sut.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("타임아웃된 Outbox가 없으면 빈 목록을 반환한다")
        void findProcessingTimeoutOutboxes_NoneTimeout_ReturnsEmptyList() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(300);
            int batchSize = 50;
            given(queryPort.findProcessingTimeoutOutboxes(timeoutBefore, batchSize))
                    .willReturn(List.of());

            // when
            List<ShipmentOutbox> result =
                    sut.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getById() - ID로 ShipmentOutbox 조회")
    class GetByIdTest {

        @Test
        @DisplayName("ID로 ShipmentOutbox를 반환한다")
        void getById_ExistingId_ReturnsShipmentOutbox() {
            // given
            Long outboxId = 1L;
            ShipmentOutbox expected = ShipmentOutboxFixtures.pendingShipmentOutbox();
            given(queryPort.getById(outboxId)).willReturn(expected);

            // when
            ShipmentOutbox result = sut.getById(outboxId);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }
}
