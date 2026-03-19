package com.ryuqq.marketplace.application.cancel.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.cancel.CancelCommandFixtures;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand.SellerCancelItem;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory.CancelBundle;
import com.ryuqq.marketplace.application.cancel.internal.CancelPersistenceFacade;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
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
@DisplayName("SellerCancelBatchService 단위 테스트")
class SellerCancelBatchServiceTest {

    @InjectMocks private SellerCancelBatchService sut;

    @Mock private CancelCommandFactory commandFactory;
    @Mock private CancelPersistenceFacade persistenceFacade;
    @Mock private OrderItemReadManager orderItemReadManager;
    @Mock private ShipmentReadManager shipmentReadManager;
    @Mock private CancelOutbox cancelOutbox;

    @Nested
    @DisplayName("execute() - 판매자 취소 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("판매자 취소 항목들을 일괄 처리하고 성공 결과를 반환한다")
        void execute_ValidItems_ReturnsSuccessResult() {
            // given
            SellerCancelBatchCommand command = CancelCommandFixtures.sellerCancelBatchCommand();
            SellerCancelItem item = command.items().get(0);
            Cancel cancel = CancelFixtures.newSellerCancel();
            ClaimHistory history = ClaimHistoryFixtures.cancelStatusChangeHistory();
            CancelBundle bundle = new CancelBundle(cancel, cancelOutbox, history);

            given(
                            commandFactory.createSellerCancel(
                                    item, command.requestedBy(), command.sellerId()))
                    .willReturn(bundle);
            given(orderItemReadManager.findById(OrderItemId.of(item.orderItemId())))
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
        @DisplayName("OrderItem이 존재하는 경우 OrderItem도 함께 취소 처리한다")
        void execute_WithExistingOrderItem_PersistsOrderItemToo() {
            // given
            SellerCancelBatchCommand command = CancelCommandFixtures.sellerCancelBatchCommand();
            SellerCancelItem item = command.items().get(0);
            Cancel cancel = CancelFixtures.newSellerCancel();
            ClaimHistory history = ClaimHistoryFixtures.cancelStatusChangeHistory();
            CancelBundle bundle = new CancelBundle(cancel, cancelOutbox, history);

            com.ryuqq.marketplace.domain.order.aggregate.OrderItem orderItem =
                    org.mockito.Mockito.mock(
                            com.ryuqq.marketplace.domain.order.aggregate.OrderItem.class);

            given(
                            commandFactory.createSellerCancel(
                                    item, command.requestedBy(), command.sellerId()))
                    .willReturn(bundle);
            given(commandFactory.now()).willReturn(java.time.Instant.now());
            given(orderItemReadManager.findById(OrderItemId.of(item.orderItemId())))
                    .willReturn(Optional.of(orderItem));

            // when
            sut.execute(command);

            // then
            then(persistenceFacade)
                    .should()
                    .persistAllWithOutboxesAndHistoriesAndOrderItemsAndShipments(
                            org.mockito.ArgumentMatchers.anyList(),
                            org.mockito.ArgumentMatchers.anyList(),
                            org.mockito.ArgumentMatchers.anyList(),
                            org.mockito.ArgumentMatchers.anyList(),
                            org.mockito.ArgumentMatchers.anyList());
        }

        @Test
        @DisplayName("항목이 없으면 저장을 수행하지 않는다")
        void execute_EmptyItems_NoPersistence() {
            // given
            SellerCancelBatchCommand command =
                    CancelCommandFixtures.sellerCancelBatchCommand(java.util.List.of());

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(0);
            then(persistenceFacade).shouldHaveNoInteractions();
        }
    }
}
