package com.ryuqq.marketplace.application.exchange.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.ExchangeCommandFixtures;
import com.ryuqq.marketplace.application.exchange.dto.command.CompleteExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CompleteExchangeBatchService 단위 테스트")
class CompleteExchangeBatchServiceTest {

    @InjectMocks private CompleteExchangeBatchService sut;

    @Mock private ExchangeBatchValidator validator;
    @Mock private ExchangeCommandFactory commandFactory;
    @Mock private ExchangePersistenceFacade persistenceFacade;
    @Mock private OrderItemReadManager orderItemReadManager;

    @Nested
    @DisplayName("execute() - 교환 완료 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("배송 중 상태 클레임을 완료 처리하고 성공 결과를 반환한다")
        void execute_ShippingClaims_ReturnsSuccessResult() {
            // given
            CompleteExchangeBatchCommand command = ExchangeCommandFixtures.completeCommand();
            ExchangeClaim claim = ExchangeFixtures.shippingExchangeClaim();
            ClaimHistory history = Mockito.mock(ClaimHistory.class);

            given(validator.validateAndGet(command.exchangeClaimIds(), command.sellerId()))
                    .willReturn(List.of(claim));
            given(commandFactory.createCompleteBundle(claim, command.processedBy()))
                    .willReturn(history);
            given(commandFactory.createCompleteOrderItemContext(claim.orderItemIdValue()))
                    .willReturn(
                            new StatusChangeContext<>(
                                    OrderItemId.of(claim.orderItemIdValue()), Instant.now()));
            given(orderItemReadManager.findById(OrderItemId.of(claim.orderItemIdValue())))
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
        @DisplayName("클레임이 없으면 저장이 호출되지 않는다")
        void execute_NoClaims_PersistenceNotCalled() {
            // given
            CompleteExchangeBatchCommand command = ExchangeCommandFixtures.completeCommand();

            given(validator.validateAndGet(command.exchangeClaimIds(), command.sellerId()))
                    .willReturn(List.of());

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.successCount()).isEqualTo(0);
            then(persistenceFacade).shouldHaveNoInteractions();
        }
    }
}
