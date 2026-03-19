package com.ryuqq.marketplace.application.order.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.order.OrderCommandFixtures;
import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
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
@DisplayName("DeliverOrderService 단위 테스트")
class DeliverOrderServiceTest {

    @InjectMocks private DeliverOrderService sut;

    @Mock private OrderCommandFactory factory;
    @Mock private ShipmentReadManager shipmentReadManager;
    @Mock private ShipmentCommandManager shipmentCommandManager;

    @Nested
    @DisplayName("execute() - 주문상품 배송완료 처리")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드로 Shipment를 DELIVERED 상태로 전환하고 저장한다")
        void execute_ValidCommand_DeliversShipmentsAndPersists() {
            // given
            OrderItemStatusCommand command = OrderCommandFixtures.orderItemStatusCommand();
            Shipment shipment = ShipmentFixtures.inTransitShipment();
            Instant now = CommonVoFixtures.now();
            List<OrderItemId> orderItemIds =
                    command.orderItemIds().stream().map(OrderItemId::of).toList();
            StatusChangeContext<List<OrderItemId>> ctx =
                    new StatusChangeContext<>(orderItemIds, now);

            given(factory.createStatusChangeContext(command)).willReturn(ctx);
            given(shipmentReadManager.findByOrderItemIds(orderItemIds))
                    .willReturn(List.of(shipment));

            // when
            sut.execute(command);

            // then
            then(shipmentReadManager).should().findByOrderItemIds(orderItemIds);
            then(shipmentCommandManager).should().persistAll(List.of(shipment));
        }

        @Test
        @DisplayName("deliver 호출 후 Shipment 상태가 DELIVERED로 변경된다")
        void execute_ValidCommand_ShipmentStatusBecomesDelivered() {
            // given
            OrderItemStatusCommand command = OrderCommandFixtures.orderItemStatusCommand();
            Shipment shipment = ShipmentFixtures.inTransitShipment();
            Instant now = CommonVoFixtures.now();
            List<OrderItemId> orderItemIds =
                    command.orderItemIds().stream().map(OrderItemId::of).toList();
            StatusChangeContext<List<OrderItemId>> ctx =
                    new StatusChangeContext<>(orderItemIds, now);

            given(factory.createStatusChangeContext(command)).willReturn(ctx);
            given(shipmentReadManager.findByOrderItemIds(orderItemIds))
                    .willReturn(List.of(shipment));

            // when
            sut.execute(command);

            // then
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.DELIVERED);
        }
    }
}
