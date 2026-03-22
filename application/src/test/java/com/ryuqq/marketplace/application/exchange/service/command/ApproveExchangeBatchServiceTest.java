package com.ryuqq.marketplace.application.exchange.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.ExchangeCommandFixtures;
import com.ryuqq.marketplace.application.exchange.dto.command.ApproveExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceBundle;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.validator.ExchangeBatchValidator;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
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
@DisplayName("ApproveExchangeBatchService 단위 테스트")
class ApproveExchangeBatchServiceTest {

    @InjectMocks private ApproveExchangeBatchService sut;

    @Mock private ExchangeBatchValidator validator;
    @Mock private ExchangeCommandFactory commandFactory;
    @Mock private ExchangePersistenceFacade persistenceFacade;

    @Nested
    @DisplayName("execute() - 교환 승인 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 클레임 목록을 승인하고 성공 결과를 반환한다")
        void execute_ValidClaims_ReturnsSuccessResult() {
            // given
            ApproveExchangeBatchCommand command = ExchangeCommandFixtures.approveCommand();
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();
            ClaimHistory history = Mockito.mock(ClaimHistory.class);

            given(validator.validateAndGet(command.exchangeClaimIds(), command.sellerId()))
                    .willReturn(List.of(claim));
            given(commandFactory.createApproveBundle(claim, command.processedBy()))
                    .willReturn(history);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.successCount()).isEqualTo(1);
            assertThat(result.failureCount()).isEqualTo(0);
            then(persistenceFacade).should().persistAll(any(ExchangePersistenceBundle.class));
        }

        @Test
        @DisplayName("유효하지 않은 클레임이 없으면 저장이 호출되지 않는다")
        void execute_NoClaims_PersistenceNotCalled() {
            // given
            ApproveExchangeBatchCommand command = ExchangeCommandFixtures.approveCommand();

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
