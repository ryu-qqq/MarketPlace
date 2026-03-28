package com.ryuqq.marketplace.application.shipment.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shipment.ShipmentCommandFixtures;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipSingleCommand;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory.ShipSingleContext;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentPersistFacade;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentPersistenceBundle;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.exception.ShipmentNotFoundException;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethod;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethodType;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentShipData;
import java.time.Instant;
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
@DisplayName("ShipSingleService 단위 테스트")
class ShipSingleServiceTest {

    @InjectMocks private ShipSingleService sut;

    @Mock private ShipmentReadManager readManager;
    @Mock private ShipmentPersistFacade persistFacade;
    @Mock private ShipmentCommandFactory commandFactory;

    @Nested
    @DisplayName("execute() - 단건 송장등록")
    class ExecuteTest {

        @Test
        @DisplayName("orderItemId로 배송을 조회하고 송장을 등록한다")
        void execute_ValidCommand_ShipsSuccessfully() {
            // given
            Instant now = Instant.parse("2026-02-18T10:00:00Z");
            ShipmentMethod method = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ShipmentShipData shipData = ShipmentShipData.of("1234567890", method);

            ShipSingleCommand command = ShipmentCommandFixtures.shipSingleCommand();
            OrderItemId orderItemId = OrderItemId.of(1001L);
            ShipSingleContext context = new ShipSingleContext(orderItemId, shipData, now);

            given(commandFactory.createShipSingleContext(command)).willReturn(context);

            Shipment shipment = ShipmentFixtures.preparingShipment();
            given(readManager.getByOrderItemId(orderItemId)).willReturn(shipment);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createShipSingleContext(command);
            then(readManager).should().getByOrderItemId(orderItemId);
            then(persistFacade).should().persistAll(any(ShipmentPersistenceBundle.class));
        }

        @Test
        @DisplayName("존재하지 않는 orderItemId이면 ShipmentNotFoundException이 발생한다")
        void execute_OrderNotFound_ThrowsException() {
            // given
            Instant now = Instant.parse("2026-02-18T10:00:00Z");
            ShipmentMethod method = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ShipmentShipData shipData = ShipmentShipData.of("1234567890", method);

            ShipSingleCommand command = ShipmentCommandFixtures.shipSingleCommand();
            OrderItemId orderItemId = OrderItemId.of(1001L);
            ShipSingleContext context = new ShipSingleContext(orderItemId, shipData, now);

            given(commandFactory.createShipSingleContext(command)).willReturn(context);
            given(readManager.getByOrderItemId(orderItemId))
                    .willThrow(new ShipmentNotFoundException("1001"));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ShipmentNotFoundException.class);
        }
    }
}
