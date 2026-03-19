package com.ryuqq.marketplace.application.shipment.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shipment.port.out.command.ShipmentOutboxCommandPort;
import com.ryuqq.marketplace.domain.shipment.outbox.ShipmentOutboxFixtures;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
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
@DisplayName("ShipmentOutboxCommandManager 단위 테스트")
class ShipmentOutboxCommandManagerTest {

    @InjectMocks private ShipmentOutboxCommandManager sut;

    @Mock private ShipmentOutboxCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단건 ShipmentOutbox 저장")
    class PersistTest {

        @Test
        @DisplayName("ShipmentOutbox를 저장하고 생성된 ID를 반환한다")
        void persist_ShipmentOutbox_ReturnsGeneratedId() {
            // given
            ShipmentOutbox outbox = ShipmentOutboxFixtures.newShipmentOutbox();
            given(commandPort.persist(outbox)).willReturn(1L);

            // when
            Long result = sut.persist(outbox);

            // then
            assertThat(result).isEqualTo(1L);
            then(commandPort).should().persist(outbox);
        }
    }

    @Nested
    @DisplayName("persistAll() - 다건 ShipmentOutbox 일괄 저장")
    class PersistAllTest {

        @Test
        @DisplayName("ShipmentOutbox 목록을 CommandPort를 통해 일괄 저장한다")
        void persistAll_OutboxList_DelegatesToCommandPort() {
            // given
            List<ShipmentOutbox> outboxes =
                    List.of(
                            ShipmentOutboxFixtures.newShipmentOutbox(),
                            ShipmentOutboxFixtures.newShipmentOutbox());

            // when
            sut.persistAll(outboxes);

            // then
            then(commandPort).should().persistAll(outboxes);
        }

        @Test
        @DisplayName("빈 목록을 전달해도 CommandPort를 호출한다")
        void persistAll_EmptyList_StillDelegatesToCommandPort() {
            // given
            List<ShipmentOutbox> emptyList = List.of();

            // when
            sut.persistAll(emptyList);

            // then
            then(commandPort).should().persistAll(emptyList);
        }
    }
}
