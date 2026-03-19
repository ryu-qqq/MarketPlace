package com.ryuqq.marketplace.application.cancel.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.cancel.CancelCommandFixtures;
import com.ryuqq.marketplace.application.cancel.dto.command.ApproveCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.application.cancel.internal.CancelPersistenceFacade;
import com.ryuqq.marketplace.application.cancel.validator.CancelBatchValidator;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
@DisplayName("ApproveCancelBatchService 단위 테스트")
class ApproveCancelBatchServiceTest {

    @InjectMocks private ApproveCancelBatchService sut;

    @Mock private CancelBatchValidator validator;
    @Mock private CancelCommandFactory commandFactory;
    @Mock private CancelPersistenceFacade persistenceFacade;
    @Mock private OrderItemReadManager orderItemReadManager;
    @Mock private ShipmentReadManager shipmentReadManager;
    @Mock private CancelOutbox cancelOutbox;

    @Nested
    @DisplayName("execute() - 취소 승인 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 취소 목록을 승인하고 성공 결과를 반환한다")
        void execute_ValidCancels_ReturnsSuccessResult() {
            // given
            ApproveCancelBatchCommand command = CancelCommandFixtures.approveBatchCommand();
            Cancel cancel = CancelFixtures.requestedCancel();
            ClaimHistory history = ClaimHistoryFixtures.cancelStatusChangeHistory();
            OutboxWithHistory bundle = new OutboxWithHistory(cancelOutbox, history);

            given(validator.validateAndGet(command.cancelIds(), command.sellerId()))
                    .willReturn(List.of(cancel));
            given(commandFactory.now()).willReturn(Instant.now());
            given(commandFactory.createApproveBundle(cancel, command.processedBy()))
                    .willReturn(bundle);
            given(orderItemReadManager.findById(OrderItemId.of(cancel.orderItemIdValue())))
                    .willReturn(Optional.empty());

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isEqualTo(1);
            assertThat(result.failureCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("검증 실패한 취소는 배치 결과에 실패로 기록된다")
        void execute_ValidationFailed_ReturnsEmptySuccessResult() {
            // given
            ApproveCancelBatchCommand command = CancelCommandFixtures.approveBatchCommand();

            given(validator.validateAndGet(command.cancelIds(), command.sellerId()))
                    .willReturn(List.of());

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(0);
            assertThat(result.successCount()).isEqualTo(0);
            then(persistenceFacade).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("OrderItem이 존재하는 경우 OrderItem도 함께 취소 처리한다")
        void execute_WithOrderItem_PersistsOrderItemToo() {
            // given
            ApproveCancelBatchCommand command = CancelCommandFixtures.approveBatchCommand();
            Cancel cancel = CancelFixtures.requestedCancel();
            ClaimHistory history = ClaimHistoryFixtures.cancelStatusChangeHistory();
            OutboxWithHistory bundle = new OutboxWithHistory(cancelOutbox, history);

            com.ryuqq.marketplace.domain.order.aggregate.OrderItem orderItem =
                    org.mockito.Mockito.mock(
                            com.ryuqq.marketplace.domain.order.aggregate.OrderItem.class);

            given(validator.validateAndGet(command.cancelIds(), command.sellerId()))
                    .willReturn(List.of(cancel));
            given(commandFactory.now()).willReturn(Instant.now());
            given(commandFactory.createApproveBundle(cancel, command.processedBy()))
                    .willReturn(bundle);
            given(orderItemReadManager.findById(OrderItemId.of(cancel.orderItemIdValue())))
                    .willReturn(Optional.of(orderItem));

            // when
            sut.execute(command);

            // then
            then(persistenceFacade)
                    .should()
                    .persistCancelsWithOutboxesAndHistoriesAndOrderItemsAndShipments(
                            org.mockito.ArgumentMatchers.anyList(),
                            org.mockito.ArgumentMatchers.anyList(),
                            org.mockito.ArgumentMatchers.anyList(),
                            org.mockito.ArgumentMatchers.anyList(),
                            org.mockito.ArgumentMatchers.anyList());
        }
    }
}
