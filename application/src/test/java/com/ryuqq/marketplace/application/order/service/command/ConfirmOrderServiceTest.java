package com.ryuqq.marketplace.application.order.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.order.OrderCommandFixtures;
import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import java.time.Instant;
import java.util.List;
import org.assertj.core.api.Assertions;
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
@DisplayName("ConfirmOrderService 단위 테스트")
class ConfirmOrderServiceTest {

    @InjectMocks private ConfirmOrderService sut;

    @Mock private OrderCommandFactory factory;
    @Mock private OrderItemReadManager readManager;
    @Mock private OrderItemCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 주문상품 구매 확정 처리")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드로 주문상품을 CONFIRMED 상태로 전환하고 저장한다")
        void execute_ValidCommand_ConfirmsOrderItemsAndUpdates() {
            // given
            OrderItemStatusCommand command = OrderCommandFixtures.orderItemStatusCommand();
            OrderItem orderItem = OrderFixtures.reconstitutedOrderItem();
            Instant now = CommonVoFixtures.now();
            List<OrderItemId> orderItemIds =
                    command.orderItemIds().stream().map(OrderItemId::of).toList();
            StatusChangeContext<List<OrderItemId>> ctx =
                    new StatusChangeContext<>(orderItemIds, now);

            given(factory.createStatusChangeContext(command)).willReturn(ctx);
            given(readManager.findAllByIds(orderItemIds)).willReturn(List.of(orderItem));

            // when
            sut.execute(command);

            // then
            then(readManager).should().findAllByIds(orderItemIds);
            then(commandManager).should().persistAll(List.of(orderItem));
        }

        @Test
        @DisplayName("confirm 호출 후 OrderItem의 상태가 CONFIRMED로 변경된다")
        void execute_ValidCommand_OrderItemStatusBecomesConfirmed() {
            // given
            OrderItemStatusCommand command = OrderCommandFixtures.orderItemStatusCommand();
            OrderItem orderItem = OrderFixtures.reconstitutedOrderItem();
            Instant now = CommonVoFixtures.now();
            List<OrderItemId> orderItemIds =
                    command.orderItemIds().stream().map(OrderItemId::of).toList();
            StatusChangeContext<List<OrderItemId>> ctx =
                    new StatusChangeContext<>(orderItemIds, now);

            given(factory.createStatusChangeContext(command)).willReturn(ctx);
            given(readManager.findAllByIds(orderItemIds)).willReturn(List.of(orderItem));

            // when
            sut.execute(command);

            // then
            Assertions.assertThat(orderItem.status()).isEqualTo(OrderItemStatus.CONFIRMED);
        }

        @Test
        @DisplayName("대상 주문상품이 없으면 persistAll이 빈 목록으로 호출된다")
        void execute_EmptyOrderItems_CallsUpdateWithEmptyList() {
            // given
            OrderItemStatusCommand command = OrderCommandFixtures.orderItemStatusCommand();
            Instant now = CommonVoFixtures.now();
            List<OrderItemId> orderItemIds =
                    command.orderItemIds().stream().map(OrderItemId::of).toList();
            StatusChangeContext<List<OrderItemId>> ctx =
                    new StatusChangeContext<>(orderItemIds, now);

            given(factory.createStatusChangeContext(command)).willReturn(ctx);
            given(readManager.findAllByIds(orderItemIds)).willReturn(List.of());

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persistAll(List.of());
        }
    }
}
