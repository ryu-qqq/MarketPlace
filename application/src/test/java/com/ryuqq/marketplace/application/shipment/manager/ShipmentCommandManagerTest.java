package com.ryuqq.marketplace.application.shipment.manager;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shipment.port.out.command.ShipmentCommandPort;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
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
@DisplayName("ShipmentCommandManager 단위 테스트")
class ShipmentCommandManagerTest {

    @InjectMocks private ShipmentCommandManager sut;

    @Mock private ShipmentCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단건 Shipment 저장")
    class PersistTest {

        @Test
        @DisplayName("Shipment를 CommandPort를 통해 저장한다")
        void persist_Shipment_DelegatesToCommandPort() {
            // given
            Shipment shipment = ShipmentFixtures.preparingShipment();

            // when
            sut.persist(shipment);

            // then
            then(commandPort).should().persist(shipment);
        }
    }

    @Nested
    @DisplayName("persistAll() - 다건 Shipment 일괄 저장")
    class PersistAllTest {

        @Test
        @DisplayName("Shipment 목록을 CommandPort를 통해 일괄 저장한다")
        void persistAll_ShipmentList_DelegatesToCommandPort() {
            // given
            List<Shipment> shipments =
                    List.of(
                            ShipmentFixtures.preparingShipment(),
                            ShipmentFixtures.shippedShipment());

            // when
            sut.persistAll(shipments);

            // then
            then(commandPort).should().persistAll(shipments);
        }

        @Test
        @DisplayName("빈 목록을 전달해도 CommandPort를 호출한다")
        void persistAll_EmptyList_StillDelegatesToCommandPort() {
            // given
            List<Shipment> emptyList = List.of();

            // when
            sut.persistAll(emptyList);

            // then
            then(commandPort).should().persistAll(emptyList);
        }
    }
}
