package com.ryuqq.marketplace.application.order.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.order.OrderCommandFixtures;
import com.ryuqq.marketplace.application.order.dto.command.OrderItemCancelCommand;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentCancelHelper;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
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
@DisplayName("CancelOrderService 단위 테스트")
class CancelOrderServiceTest {

    @InjectMocks private CancelOrderService sut;

    @Mock private OrderCommandFactory factory;
    @Mock private OrderItemReadManager readManager;
    @Mock private OrderItemCommandManager commandManager;
    @Mock private ShipmentCancelHelper shipmentCancelHelper;
    @Mock private ShipmentCommandManager shipmentCommandManager;

    @Nested
    @DisplayName("execute() - 주문상품 취소 처리")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드로 주문상품을 CANCELLED 상태로 전환하고 저장한다")
        void execute_ValidCommand_CancelsOrderItemsAndUpdates() {
            // given
            OrderItemCancelCommand command = OrderCommandFixtures.orderItemCancelCommand();
            OrderItem orderItem = OrderFixtures.reconstitutedOrderItem();
            Instant now = CommonVoFixtures.now();
            List<OrderItemId> orderItemIds =
                    command.orderItemIds().stream().map(OrderItemId::of).toList();
            StatusChangeContext<List<OrderItemId>> ctx =
                    new StatusChangeContext<>(orderItemIds, now);

            given(factory.createCancelContext(command)).willReturn(ctx);
            given(readManager.findAllByIds(orderItemIds)).willReturn(List.of(orderItem));

            // when
            sut.execute(command);

            // then
            then(readManager).should().findAllByIds(orderItemIds);
            then(commandManager).should().persistAll(List.of(orderItem));
        }

        @Test
        @DisplayName("대상 주문상품이 없으면 persistAll이 빈 목록으로 호출된다")
        void execute_EmptyOrderItems_CallsUpdateWithEmptyList() {
            // given
            OrderItemCancelCommand command = OrderCommandFixtures.orderItemCancelCommand();
            Instant now = CommonVoFixtures.now();
            List<OrderItemId> orderItemIds =
                    command.orderItemIds().stream().map(OrderItemId::of).toList();
            StatusChangeContext<List<OrderItemId>> ctx =
                    new StatusChangeContext<>(orderItemIds, now);

            given(factory.createCancelContext(command)).willReturn(ctx);
            given(readManager.findAllByIds(orderItemIds)).willReturn(List.of());

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persistAll(List.of());
        }

        @Test
        @DisplayName("복수의 주문상품이 있으면 모두 취소 처리된다")
        void execute_MultipleOrderItems_AllItemsCancelled() {
            // given
            OrderItemCancelCommand command =
                    OrderCommandFixtures.orderItemCancelCommand("1001", "1002");
            OrderItem item1 =
                    OrderFixtures.reconstitutedOrderItem(
                            1L, com.ryuqq.marketplace.domain.order.vo.OrderItemStatus.READY);
            OrderItem item2 =
                    OrderFixtures.reconstitutedOrderItem(
                            2L, com.ryuqq.marketplace.domain.order.vo.OrderItemStatus.CONFIRMED);
            Instant now = CommonVoFixtures.now();
            List<OrderItemId> orderItemIds =
                    command.orderItemIds().stream().map(OrderItemId::of).toList();
            StatusChangeContext<List<OrderItemId>> ctx =
                    new StatusChangeContext<>(orderItemIds, now);

            given(factory.createCancelContext(command)).willReturn(ctx);
            given(readManager.findAllByIds(orderItemIds)).willReturn(List.of(item1, item2));

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persistAll(List.of(item1, item2));
        }
    }
}
