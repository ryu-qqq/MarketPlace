package com.ryuqq.marketplace.application.exchange.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.ExchangeCommandFixtures;
import com.ryuqq.marketplace.application.exchange.dto.command.RequestExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.RequestExchangeBatchCommand.ExchangeRequestItem;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory.ExchangeClaimWithHistory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceBundle;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.validator.ExchangeBatchValidator;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
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
@DisplayName("RequestExchangeBatchService 단위 테스트")
class RequestExchangeBatchServiceTest {

    @InjectMocks private RequestExchangeBatchService sut;

    @Mock private ExchangeCommandFactory commandFactory;
    @Mock private ExchangePersistenceFacade persistenceFacade;
    @Mock private OrderItemReadManager orderItemReadManager;
    @Mock private ExchangeBatchValidator validator;

    @Nested
    @DisplayName("execute() - 교환 요청 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 교환 요청을 처리하고 성공 결과를 반환한다")
        void execute_ValidCommand_ReturnsSuccessResult() {
            // given
            RequestExchangeBatchCommand command = ExchangeCommandFixtures.requestCommand();
            ExchangeRequestItem item = command.items().get(0);
            ExchangeClaim claim = ExchangeFixtures.newExchangeClaim();
            ClaimHistory history = org.mockito.Mockito.mock(ClaimHistory.class);
            ExchangeClaimWithHistory bundle = new ExchangeClaimWithHistory(claim, history);

            given(validator.hasActiveClaim(item.orderItemId())).willReturn(false);
            given(
                            commandFactory.createExchangeRequest(
                                    item, command.requestedBy(), command.sellerId()))
                    .willReturn(bundle);
            given(commandFactory.createRequestOrderItemContext(item.orderItemId()))
                    .willReturn(new StatusChangeContext<>(OrderItemId.of(item.orderItemId()), Instant.now()));
            given(orderItemReadManager.findById(OrderItemId.of(item.orderItemId())))
                    .willReturn(Optional.empty());

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.successCount()).isEqualTo(1);
            assertThat(result.failureCount()).isEqualTo(0);
            then(persistenceFacade).should().persistAll(any(ExchangePersistenceBundle.class));
        }

        @Test
        @DisplayName("진행 중인 클레임이 있는 경우 해당 항목을 실패로 처리한다")
        void execute_HasActiveClaim_AddsFailure() {
            // given
            RequestExchangeBatchCommand command = ExchangeCommandFixtures.requestCommand();
            ExchangeRequestItem item = command.items().get(0);

            given(validator.hasActiveClaim(item.orderItemId())).willReturn(true);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.successCount()).isEqualTo(0);
            assertThat(result.failureCount()).isEqualTo(1);
            then(persistenceFacade).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("빈 항목 목록이면 처리 없이 빈 결과를 반환한다")
        void execute_EmptyItems_ReturnsEmptyResult() {
            // given
            RequestExchangeBatchCommand command =
                    new RequestExchangeBatchCommand(List.of(), "buyer@example.com", 100L);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.successCount()).isEqualTo(0);
            assertThat(result.failureCount()).isEqualTo(0);
            then(persistenceFacade).shouldHaveNoInteractions();
        }
    }
}
