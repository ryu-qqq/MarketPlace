package com.ryuqq.marketplace.application.exchange.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.ExchangeCommandFixtures;
import com.ryuqq.marketplace.application.exchange.dto.command.HoldExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceBundle;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.validator.ExchangeBatchValidator;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.util.List;
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
@DisplayName("HoldExchangeBatchService 단위 테스트")
class HoldExchangeBatchServiceTest {

    @InjectMocks private HoldExchangeBatchService sut;

    @Mock private ExchangeBatchValidator validator;
    @Mock private ExchangeCommandFactory commandFactory;
    @Mock private ExchangePersistenceFacade persistenceFacade;

    @Nested
    @DisplayName("execute() - 교환 보류 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("보류 요청 시 교환을 보류 상태로 전환하고 성공 결과를 반환한다")
        void execute_HoldRequest_ReturnsSuccessResult() {
            // given
            HoldExchangeBatchCommand command = ExchangeCommandFixtures.holdCommand();
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();
            ExchangeOutbox outbox = Mockito.mock(ExchangeOutbox.class);
            ClaimHistory history = Mockito.mock(ClaimHistory.class);
            OutboxWithHistory bundle = new OutboxWithHistory(outbox, history);

            given(validator.validateAndGet(command.exchangeClaimIds(), command.sellerId()))
                    .willReturn(List.of(claim));
            given(commandFactory.createHoldBundle(claim, command.memo(), command.processedBy()))
                    .willReturn(bundle);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.successCount()).isEqualTo(1);
            assertThat(result.failureCount()).isEqualTo(0);
            then(persistenceFacade).should().persistAll(any(ExchangePersistenceBundle.class));
        }

        @Test
        @DisplayName("보류 해제 요청 시 교환을 보류 해제하고 성공 결과를 반환한다")
        void execute_ReleaseHoldRequest_ReturnsSuccessResult() {
            // given
            HoldExchangeBatchCommand command = ExchangeCommandFixtures.releaseHoldCommand();
            ExchangeClaim claim = ExchangeFixtures.holdExchangeClaim();
            ExchangeOutbox outbox = Mockito.mock(ExchangeOutbox.class);
            ClaimHistory history = Mockito.mock(ClaimHistory.class);
            OutboxWithHistory bundle = new OutboxWithHistory(outbox, history);

            given(validator.validateAndGet(command.exchangeClaimIds(), command.sellerId()))
                    .willReturn(List.of(claim));
            given(commandFactory.createReleaseHoldBundle(claim, command.processedBy()))
                    .willReturn(bundle);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.successCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("빈 클레임 목록이면 저장을 수행하지 않는다")
        void execute_EmptyClaimList_NoPersistence() {
            // given
            HoldExchangeBatchCommand command = ExchangeCommandFixtures.holdCommand();

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
