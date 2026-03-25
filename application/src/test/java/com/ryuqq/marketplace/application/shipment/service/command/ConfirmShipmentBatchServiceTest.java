package com.ryuqq.marketplace.application.shipment.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.common.dto.command.BulkStatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBatchCommand;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentPersistFacade;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentPersistenceBundle;
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
@DisplayName("ConfirmShipmentBatchService 단위 테스트")
class ConfirmShipmentBatchServiceTest {

    @InjectMocks private ConfirmShipmentBatchService sut;

    @Mock private OrderItemReadManager orderItemReadManager;
    @Mock private ShipmentCommandFactory commandFactory;
    @Mock private ShipmentPersistFacade persistFacade;

    @Nested
    @DisplayName("execute() - 발주확인 일괄 처리")
    class ExecuteTest {

        private static final String ORDER_ITEM_ID_1 = "01940001-0000-7000-8000-000000000001";
        private static final String ORDER_ITEM_ID_2 = "01940001-0000-7000-8000-000000000002";

        @Test
        @DisplayName("모든 항목이 성공하면 전체 성공 결과를 반환한다")
        void execute_AllSuccess_ReturnsAllSuccess() {
            // given
            OrderItem item1 = OrderFixtures.reconstitutedOrderItem(1L, OrderItemStatus.READY);
            OrderItem item2 = OrderFixtures.reconstitutedOrderItem(2L, OrderItemStatus.READY);

            ConfirmShipmentBatchCommand command =
                    new ConfirmShipmentBatchCommand(
                            List.of(ORDER_ITEM_ID_1, ORDER_ITEM_ID_2), null);

            Instant changedAt = Instant.parse("2026-02-18T10:00:00Z");
            BulkStatusChangeContext<OrderItemId> confirmContexts =
                    new BulkStatusChangeContext<>(
                            List.of(OrderItemId.of(ORDER_ITEM_ID_1), OrderItemId.of(ORDER_ITEM_ID_2)),
                            changedAt);

            given(
                            orderItemReadManager.findAllByIds(
                                    List.of(
                                            OrderItemId.of(ORDER_ITEM_ID_1),
                                            OrderItemId.of(ORDER_ITEM_ID_2))))
                    .willReturn(List.of(item1, item2));
            given(commandFactory.createConfirmContexts(command)).willReturn(confirmContexts);
            given(commandFactory.createConfirmBundle(anyList(), eq(changedAt)))
                    .willReturn(
                            ShipmentPersistenceBundle.of(List.of(), List.of(), List.of(item1, item2)));

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.successCount()).isEqualTo(2);
            assertThat(result.failureCount()).isZero();
            then(persistFacade).should().persistAll(any(ShipmentPersistenceBundle.class));
        }

        @Test
        @DisplayName("이미 확인된 항목은 실패 처리한다")
        void execute_AlreadyConfirmed_ReturnsFailure() {
            // given
            OrderItem item1 = OrderFixtures.reconstitutedOrderItem(1L, OrderItemStatus.CONFIRMED);

            ConfirmShipmentBatchCommand command =
                    new ConfirmShipmentBatchCommand(List.of(ORDER_ITEM_ID_1), null);

            Instant changedAt = Instant.parse("2026-02-18T10:00:00Z");
            BulkStatusChangeContext<OrderItemId> confirmContexts =
                    new BulkStatusChangeContext<>(
                            List.of(OrderItemId.of(ORDER_ITEM_ID_1)), changedAt);

            given(orderItemReadManager.findAllByIds(List.of(OrderItemId.of(ORDER_ITEM_ID_1))))
                    .willReturn(List.of(item1));
            given(commandFactory.createConfirmContexts(command)).willReturn(confirmContexts);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isZero();
            assertThat(result.failureCount()).isEqualTo(1);
            assertThat(result.results().get(0).errorCode()).isEqualTo("INVALID_STATUS");
            then(persistFacade).should(never()).persistAll(any(ShipmentPersistenceBundle.class));
        }

        @Test
        @DisplayName("셀러 소유권 검증 실패 시 FORBIDDEN 에러를 반환한다")
        void execute_ForbiddenSeller_ReturnsForbidden() {
            // given
            OrderItem item1 = OrderFixtures.reconstitutedOrderItem(1L, OrderItemStatus.READY);

            ConfirmShipmentBatchCommand command =
                    new ConfirmShipmentBatchCommand(List.of(ORDER_ITEM_ID_1), 999L);

            Instant changedAt = Instant.parse("2026-02-18T10:00:00Z");
            BulkStatusChangeContext<OrderItemId> confirmContexts =
                    new BulkStatusChangeContext<>(
                            List.of(OrderItemId.of(ORDER_ITEM_ID_1)), changedAt);

            given(orderItemReadManager.findAllByIds(List.of(OrderItemId.of(ORDER_ITEM_ID_1))))
                    .willReturn(List.of(item1));
            given(commandFactory.createConfirmContexts(command)).willReturn(confirmContexts);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isZero();
            assertThat(result.failureCount()).isEqualTo(1);
            assertThat(result.results().get(0).errorCode()).isEqualTo("FORBIDDEN");
            then(persistFacade).should(never()).persistAll(any(ShipmentPersistenceBundle.class));
        }

        @Test
        @DisplayName("SUPER_ADMIN(sellerId=null)이면 소유권 검증을 건너뛴다")
        void execute_SuperAdmin_SkipsOwnershipCheck() {
            // given
            OrderItem item1 = OrderFixtures.reconstitutedOrderItem(1L, OrderItemStatus.READY);

            ConfirmShipmentBatchCommand command =
                    new ConfirmShipmentBatchCommand(List.of(ORDER_ITEM_ID_1), null);

            Instant changedAt = Instant.parse("2026-02-18T10:00:00Z");
            BulkStatusChangeContext<OrderItemId> confirmContexts =
                    new BulkStatusChangeContext<>(
                            List.of(OrderItemId.of(ORDER_ITEM_ID_1)), changedAt);

            given(orderItemReadManager.findAllByIds(List.of(OrderItemId.of(ORDER_ITEM_ID_1))))
                    .willReturn(List.of(item1));
            given(commandFactory.createConfirmContexts(command)).willReturn(confirmContexts);
            given(commandFactory.createConfirmBundle(anyList(), eq(changedAt)))
                    .willReturn(ShipmentPersistenceBundle.of(List.of(), List.of(), List.of(item1)));

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isEqualTo(1);
            then(persistFacade).should().persistAll(any(ShipmentPersistenceBundle.class));
        }

        @Test
        @DisplayName("빈 목록이면 빈 결과를 반환한다")
        void execute_EmptyList_ReturnsEmptyResult() {
            // given
            ConfirmShipmentBatchCommand command = new ConfirmShipmentBatchCommand(List.of(), null);

            Instant changedAt = Instant.parse("2026-02-18T10:00:00Z");
            BulkStatusChangeContext<OrderItemId> confirmContexts =
                    new BulkStatusChangeContext<>(List.of(), changedAt);

            given(orderItemReadManager.findAllByIds(List.<OrderItemId>of())).willReturn(List.of());
            given(commandFactory.createConfirmContexts(command)).willReturn(confirmContexts);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isZero();
            then(persistFacade).should(never()).persistAll(any(ShipmentPersistenceBundle.class));
        }
    }
}
