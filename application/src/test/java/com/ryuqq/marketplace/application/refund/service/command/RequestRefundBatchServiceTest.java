package com.ryuqq.marketplace.application.refund.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.refund.RefundCommandFixtures;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand.RefundRequestItem;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory.RefundBundle;
import com.ryuqq.marketplace.application.refund.internal.RefundPersistenceBundle;
import com.ryuqq.marketplace.application.refund.internal.RefundPersistenceFacade;
import com.ryuqq.marketplace.application.refund.validator.RefundBatchValidator;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
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
@DisplayName("RequestRefundBatchService 단위 테스트")
class RequestRefundBatchServiceTest {

    @InjectMocks private RequestRefundBatchService sut;

    @Mock private RefundCommandFactory commandFactory;
    @Mock private RefundPersistenceFacade persistenceFacade;
    @Mock private OrderItemReadManager orderItemReadManager;
    @Mock private RefundBatchValidator validator;
    @Mock private RefundOutbox refundOutbox;

    @Nested
    @DisplayName("execute() - 환불 요청 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 환불 요청 항목들을 일괄 처리하고 성공 결과를 반환한다")
        void execute_ValidItems_ReturnsSuccessResult() {
            // given
            RequestRefundBatchCommand command = RefundCommandFixtures.requestBatchCommand();
            RefundRequestItem item = command.items().get(0);
            RefundClaim claim = RefundFixtures.newRefundClaim();
            ClaimHistory history = ClaimHistoryFixtures.refundStatusChangeHistory();
            RefundBundle bundle = new RefundBundle(claim, refundOutbox, history);

            given(validator.hasActiveClaim(item.orderItemId())).willReturn(false);
            given(
                            commandFactory.createRefundRequest(
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
        @DisplayName("진행 중인 클레임이 있는 항목은 실패로 처리한다")
        void execute_ActiveClaimExists_SkipsItem() {
            // given
            RequestRefundBatchCommand command = RefundCommandFixtures.requestBatchCommand();
            RefundRequestItem item = command.items().get(0);

            given(validator.hasActiveClaim(item.orderItemId())).willReturn(true);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isEqualTo(0);
            assertThat(result.failureCount()).isEqualTo(1);
            then(persistenceFacade).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("항목이 없으면 저장을 수행하지 않는다")
        void execute_EmptyItems_NoPersistence() {
            // given
            RequestRefundBatchCommand command =
                    RefundCommandFixtures.requestBatchCommand(List.of());

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(0);
            then(persistenceFacade).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("OrderItem이 존재하고 전환 가능한 경우 OrderItem도 함께 처리한다")
        void execute_WithExistingOrderItem_PersistsOrderItemToo() {
            // given
            RequestRefundBatchCommand command = RefundCommandFixtures.requestBatchCommand();
            RefundRequestItem item = command.items().get(0);
            RefundClaim claim = RefundFixtures.newRefundClaim();
            ClaimHistory history = ClaimHistoryFixtures.refundStatusChangeHistory();
            RefundBundle bundle = new RefundBundle(claim, refundOutbox, history);

            com.ryuqq.marketplace.domain.order.aggregate.OrderItem orderItem =
                    org.mockito.Mockito.mock(
                            com.ryuqq.marketplace.domain.order.aggregate.OrderItem.class);

            given(validator.hasActiveClaim(item.orderItemId())).willReturn(false);
            given(
                            commandFactory.createRefundRequest(
                                    item, command.requestedBy(), command.sellerId()))
                    .willReturn(bundle);
            given(orderItemReadManager.findById(OrderItemId.of(item.orderItemId())))
                    .willReturn(Optional.of(orderItem));
            given(orderItem.status())
                    .willReturn(
                            com.ryuqq.marketplace.domain.order.vo.OrderItemStatus.CONFIRMED);
            given(commandFactory.createRequestOrderItemContext(item.orderItemId()))
                    .willReturn(
                            new StatusChangeContext<>(
                                    OrderItemId.of(item.orderItemId()), Instant.now()));

            // when
            sut.execute(command);

            // then
            then(persistenceFacade).should().persistAll(any(RefundPersistenceBundle.class));
        }
    }
}
