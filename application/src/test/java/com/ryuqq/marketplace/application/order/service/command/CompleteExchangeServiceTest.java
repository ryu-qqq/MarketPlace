package com.ryuqq.marketplace.application.order.service.command;

import static org.assertj.core.api.Assertions.assertThat;
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
@DisplayName("CompleteExchangeService 단위 테스트")
class CompleteExchangeServiceTest {

    @InjectMocks private CompleteExchangeService sut;

    @Mock private OrderCommandFactory factory;
    @Mock private OrderItemReadManager readManager;
    @Mock private OrderItemCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 교환 완료 처리")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드로 주문상품을 RETURNED 상태로 전환하고 저장한다")
        void execute_ValidCommand_CompletesReturnAndUpdates() {
            // given
            OrderItemStatusCommand command = OrderCommandFixtures.orderItemStatusCommand();
            OrderItem orderItem =
                    OrderFixtures.reconstitutedOrderItem(1L, OrderItemStatus.RETURN_REQUESTED);
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
        @DisplayName("completeReturn 호출 후 OrderItem 상태가 RETURNED로 변경된다")
        void execute_ValidCommand_OrderItemStatusBecomesReturned() {
            // given
            OrderItemStatusCommand command = OrderCommandFixtures.orderItemStatusCommand();
            OrderItem orderItem =
                    OrderFixtures.reconstitutedOrderItem(1L, OrderItemStatus.RETURN_REQUESTED);
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
            assertThat(orderItem.status()).isEqualTo(OrderItemStatus.RETURNED);
        }
    }
}
