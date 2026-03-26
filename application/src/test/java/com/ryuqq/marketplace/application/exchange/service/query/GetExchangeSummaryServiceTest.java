package com.ryuqq.marketplace.application.exchange.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.exchange.ExchangeQueryFixtures;
import com.ryuqq.marketplace.application.exchange.assembler.ExchangeAssembler;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeSummaryResult;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeReadManager;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import java.util.Map;
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
@DisplayName("GetExchangeSummaryService 단위 테스트")
class GetExchangeSummaryServiceTest {

    @InjectMocks private GetExchangeSummaryService sut;

    @Mock private ExchangeReadManager exchangeReadManager;
    @Mock private ExchangeAssembler assembler;

    @Nested
    @DisplayName("execute() - 교환 상태별 요약 조회")
    class ExecuteTest {

        @Test
        @DisplayName("상태별 카운트로 요약 결과를 반환한다")
        void execute_ReturnsExchangeSummaryResult() {
            // given
            Map<ExchangeStatus, Long> statusCounts =
                    Map.of(
                            ExchangeStatus.REQUESTED, 5L,
                            ExchangeStatus.COLLECTING, 3L,
                            ExchangeStatus.COMPLETED, 10L);
            ExchangeSummaryResult expectedResult = ExchangeQueryFixtures.exchangeSummaryResult();

            given(exchangeReadManager.countByStatus()).willReturn(statusCounts);
            given(assembler.toSummaryResult(statusCounts)).willReturn(expectedResult);

            // when
            ExchangeSummaryResult result = sut.execute();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expectedResult);
            then(exchangeReadManager).should().countByStatus();
            then(assembler).should().toSummaryResult(statusCounts);
        }

        @Test
        @DisplayName("모든 상태 카운트가 0인 경우 빈 요약 결과를 반환한다")
        void execute_EmptyCounts_ReturnsZeroSummary() {
            // given
            Map<ExchangeStatus, Long> emptyStatusCounts = Map.of();
            ExchangeSummaryResult emptyResult = ExchangeQueryFixtures.emptySummaryResult();

            given(exchangeReadManager.countByStatus()).willReturn(emptyStatusCounts);
            given(assembler.toSummaryResult(emptyStatusCounts)).willReturn(emptyResult);

            // when
            ExchangeSummaryResult result = sut.execute();

            // then
            assertThat(result).isNotNull();
            assertThat(result.requested()).isEqualTo(0L);
            assertThat(result.completed()).isEqualTo(0L);
        }
    }
}
