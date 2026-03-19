package com.ryuqq.marketplace.application.exchange.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.exchange.ExchangeQueryFixtures;
import com.ryuqq.marketplace.application.exchange.dto.query.ExchangeSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSearchCriteria;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSortKey;
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
@DisplayName("ExchangeQueryFactory 단위 테스트")
class ExchangeQueryFactoryTest {

    @InjectMocks private ExchangeQueryFactory sut;

    @Mock private CommonVoFactory commonVoFactory;

    @Nested
    @DisplayName("createCriteria() - 검색 조건 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("기본 파라미터로 ExchangeSearchCriteria를 생성한다")
        void createCriteria_DefaultParams_ReturnsCriteria() {
            // given
            ExchangeSearchParams params = ExchangeQueryFixtures.searchParams();
            PageRequest pageRequest = Mockito.mock(PageRequest.class);

            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);

            // when
            ExchangeSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.statuses()).isEmpty();
            assertThat(result.searchField()).isNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.dateRange()).isNull();
        }

        @Test
        @DisplayName("상태 필터가 있는 파라미터로 ExchangeSearchCriteria를 생성한다")
        void createCriteria_WithStatuses_ReturnsCriteriaWithStatuses() {
            // given
            ExchangeSearchParams params =
                    ExchangeQueryFixtures.searchParams(List.of("REQUESTED", "COLLECTING"));
            PageRequest pageRequest = Mockito.mock(PageRequest.class);

            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);

            // when
            ExchangeSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.statuses()).hasSize(2);
        }

        @Test
        @DisplayName("날짜 범위가 있는 파라미터로 DateRange를 포함한 Criteria를 생성한다")
        void createCriteria_WithDateRange_ReturnsCriteriaWithDateRange() {
            // given
            ExchangeSearchParams params =
                    ExchangeQueryFixtures.searchParamsWithDateRange("2026-01-01", "2026-03-31");
            PageRequest pageRequest = Mockito.mock(PageRequest.class);

            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);
            given(commonVoFactory.createDateRange(any(), any()))
                    .willReturn(
                            Mockito.mock(com.ryuqq.marketplace.domain.common.vo.DateRange.class));

            // when
            ExchangeSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.dateRange()).isNotNull();
        }

        @Test
        @DisplayName("sortKey가 null이면 기본 CREATED_AT으로 정렬한다")
        void createCriteria_NullSortKey_UsesDefaultCreatedAt() {
            // given
            ExchangeSearchParams params =
                    new ExchangeSearchParams(null, null, null, null, null, null, null, null, 0, 20);
            PageRequest pageRequest = Mockito.mock(PageRequest.class);

            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);

            // when
            ExchangeSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.queryContext().sortKey()).isEqualTo(ExchangeSortKey.CREATED_AT);
        }
    }
}
