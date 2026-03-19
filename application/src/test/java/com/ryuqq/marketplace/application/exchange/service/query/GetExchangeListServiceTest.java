package com.ryuqq.marketplace.application.exchange.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.exchange.ExchangeQueryFixtures;
import com.ryuqq.marketplace.application.exchange.assembler.ExchangeAssembler;
import com.ryuqq.marketplace.application.exchange.dto.query.ExchangeSearchParams;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeListResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangePageResult;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeQueryFactory;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeReadManager;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSearchCriteria;
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
@DisplayName("GetExchangeListService 단위 테스트")
class GetExchangeListServiceTest {

    @InjectMocks private GetExchangeListService sut;

    @Mock private ExchangeReadManager exchangeReadManager;
    @Mock private ExchangeQueryFactory queryFactory;
    @Mock private ExchangeAssembler assembler;

    @Nested
    @DisplayName("execute() - 교환 목록 조회")
    class ExecuteTest {

        @Test
        @DisplayName("검색 파라미터로 교환 목록을 조회하고 PageResult를 반환한다")
        void execute_ValidParams_ReturnsPageResult() {
            // given
            ExchangeSearchParams params = ExchangeQueryFixtures.searchParams();
            ExchangeSearchCriteria criteria = Mockito.mock(ExchangeSearchCriteria.class);
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();
            ExchangeListResult listResult =
                    ExchangeQueryFixtures.exchangeListResult(
                            "01900000-0000-7000-0000-000000000001");
            ExchangePageResult expectedPageResult = ExchangeQueryFixtures.exchangePageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(exchangeReadManager.findByCriteria(criteria)).willReturn(List.of(claim));
            given(exchangeReadManager.countByCriteria(criteria)).willReturn(1L);
            given(assembler.toListResult(claim)).willReturn(listResult);
            given(assembler.toPageResult(List.of(listResult), params.page(), params.size(), 1L))
                    .willReturn(expectedPageResult);

            // when
            ExchangePageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expectedPageResult);
            then(queryFactory).should().createCriteria(params);
            then(exchangeReadManager).should().findByCriteria(criteria);
            then(exchangeReadManager).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("결과가 없으면 빈 PageResult를 반환한다")
        void execute_NoResults_ReturnsEmptyPageResult() {
            // given
            ExchangeSearchParams params = ExchangeQueryFixtures.searchParams();
            ExchangeSearchCriteria criteria = Mockito.mock(ExchangeSearchCriteria.class);
            ExchangePageResult emptyResult = ExchangeQueryFixtures.emptyExchangePageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(exchangeReadManager.findByCriteria(criteria)).willReturn(List.of());
            given(exchangeReadManager.countByCriteria(criteria)).willReturn(0L);
            given(assembler.toPageResult(List.of(), params.page(), params.size(), 0L))
                    .willReturn(emptyResult);

            // when
            ExchangePageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.exchanges()).isEmpty();
        }
    }
}
