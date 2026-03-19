package com.ryuqq.marketplace.application.exchange.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.ExchangeCommandFixtures;
import com.ryuqq.marketplace.application.exchange.dto.command.ConvertToRefundBatchCommand;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.validator.ExchangeBatchValidator;
import com.ryuqq.marketplace.application.refund.port.in.command.RequestRefundBatchUseCase;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import java.time.Instant;
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
@DisplayName("ConvertToRefundBatchService 단위 테스트")
class ConvertToRefundBatchServiceTest {

    @InjectMocks private ConvertToRefundBatchService sut;

    @Mock private ExchangeBatchValidator validator;
    @Mock private ExchangeCommandFactory commandFactory;
    @Mock private ExchangePersistenceFacade persistenceFacade;
    @Mock private RequestRefundBatchUseCase requestRefundBatchUseCase;

    @Nested
    @DisplayName("execute() - 교환→환불 전환 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("교환 클레임을 취소하고 환불 요청을 생성하여 성공 결과를 반환한다")
        void execute_ValidClaims_CancelsExchangeAndRequestsRefund() {
            // given
            ConvertToRefundBatchCommand command = ExchangeCommandFixtures.convertToRefundCommand();
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();
            ClaimHistory history = Mockito.mock(ClaimHistory.class);

            given(validator.validateAndGet(command.exchangeClaimIds(), command.sellerId()))
                    .willReturn(List.of(claim));
            given(commandFactory.now()).willReturn(Instant.now());
            given(
                            commandFactory.createConvertToRefundHistory(
                                    claim, claim.status().name(), command.processedBy()))
                    .willReturn(history);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.successCount()).isEqualTo(1);
            assertThat(result.failureCount()).isEqualTo(0);
            then(persistenceFacade).should().persistClaimsWithHistories(anyList(), anyList());
            then(requestRefundBatchUseCase).should().execute(any());
        }

        @Test
        @DisplayName("빈 클레임 목록이면 저장 및 환불 요청을 수행하지 않는다")
        void execute_EmptyClaimList_NoPersistenceAndNoRefund() {
            // given
            ConvertToRefundBatchCommand command = ExchangeCommandFixtures.convertToRefundCommand();

            given(validator.validateAndGet(command.exchangeClaimIds(), command.sellerId()))
                    .willReturn(List.of());

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.successCount()).isEqualTo(0);
            then(persistenceFacade).shouldHaveNoInteractions();
            then(requestRefundBatchUseCase).shouldHaveNoInteractions();
        }
    }
}
