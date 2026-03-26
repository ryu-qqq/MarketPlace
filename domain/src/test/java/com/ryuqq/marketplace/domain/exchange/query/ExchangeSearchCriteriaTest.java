package com.ryuqq.marketplace.domain.exchange.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeSearchCriteria 검색 조건 단위 테스트")
class ExchangeSearchCriteriaTest {

    @Nested
    @DisplayName("of() - 정적 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("모든 파라미터로 검색 조건을 생성한다")
        void createWithAllParameters() {
            // given
            List<ExchangeStatus> statuses =
                    List.of(ExchangeStatus.REQUESTED, ExchangeStatus.COLLECTING);
            ExchangeSearchField searchField = ExchangeSearchField.CLAIM_NUMBER;
            String searchWord = "EXC-20260218";
            QueryContext<ExchangeSortKey> queryContext =
                    QueryContext.of(
                            ExchangeSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 10));

            // when
            ExchangeSearchCriteria criteria =
                    ExchangeSearchCriteria.of(
                            statuses,
                            searchField,
                            searchWord,
                            null,
                            ExchangeDateField.REQUESTED,
                            queryContext);

            // then
            assertThat(criteria.statuses())
                    .containsExactlyInAnyOrder(ExchangeStatus.REQUESTED, ExchangeStatus.COLLECTING);
            assertThat(criteria.searchField()).isEqualTo(ExchangeSearchField.CLAIM_NUMBER);
            assertThat(criteria.searchWord()).isEqualTo("EXC-20260218");
            assertThat(criteria.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("null 파라미터로 생성하면 statuses는 빈 리스트이다")
        void createWithNullStatuses() {
            // when
            ExchangeSearchCriteria criteria =
                    ExchangeSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ExchangeSortKey.defaultKey()));

            // then
            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.dateField()).isNull();
        }
    }

    @Nested
    @DisplayName("defaultCriteria() - 기본 검색 조건")
    class DefaultCriteriaTest {

        @Test
        @DisplayName("기본 검색 조건을 생성한다")
        void createDefaultCriteria() {
            // when
            ExchangeSearchCriteria criteria = ExchangeSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.dateRange()).isNull();
            assertThat(criteria.dateField()).isNull();
            assertThat(criteria.queryContext()).isNotNull();
        }
    }

    @Nested
    @DisplayName("hasStatusFilter() - 상태 필터 존재 여부")
    class HasStatusFilterTest {

        @Test
        @DisplayName("statuses가 비어있지 않으면 true를 반환한다")
        void returnTrueWhenStatusesNotEmpty() {
            // given
            ExchangeSearchCriteria criteria =
                    ExchangeSearchCriteria.of(
                            List.of(ExchangeStatus.REQUESTED),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ExchangeSortKey.defaultKey()));

            // then
            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("statuses가 비어있으면 false를 반환한다")
        void returnFalseWhenStatusesEmpty() {
            // given
            ExchangeSearchCriteria criteria = ExchangeSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("statuses가 null이면 false를 반환한다")
        void returnFalseWhenStatusesNull() {
            // given
            ExchangeSearchCriteria criteria =
                    ExchangeSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ExchangeSortKey.defaultKey()));

            // then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchCondition() - 검색어 존재 여부")
    class HasSearchConditionTest {

        @Test
        @DisplayName("searchWord가 있으면 true를 반환한다")
        void returnTrueWhenSearchWordExists() {
            // given
            ExchangeSearchCriteria criteria =
                    ExchangeSearchCriteria.of(
                            null,
                            ExchangeSearchField.CLAIM_NUMBER,
                            "EXC-20260218-0001",
                            null,
                            null,
                            QueryContext.defaultOf(ExchangeSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("searchWord가 null이면 false를 반환한다")
        void returnFalseWhenSearchWordIsNull() {
            // given
            ExchangeSearchCriteria criteria = ExchangeSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("searchWord가 빈 문자열이면 false를 반환한다")
        void returnFalseWhenSearchWordIsBlank() {
            // given
            ExchangeSearchCriteria criteria =
                    ExchangeSearchCriteria.of(
                            null,
                            ExchangeSearchField.CLAIM_NUMBER,
                            "   ",
                            null,
                            null,
                            QueryContext.defaultOf(ExchangeSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchCondition()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchField() - 검색 필드 존재 여부")
    class HasSearchFieldTest {

        @Test
        @DisplayName("searchField가 있으면 true를 반환한다")
        void returnTrueWhenSearchFieldExists() {
            // given
            ExchangeSearchCriteria criteria =
                    ExchangeSearchCriteria.of(
                            null,
                            ExchangeSearchField.ORDER_NUMBER,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ExchangeSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchField()).isTrue();
        }

        @Test
        @DisplayName("searchField가 null이면 false를 반환한다")
        void returnFalseWhenSearchFieldIsNull() {
            // given
            ExchangeSearchCriteria criteria = ExchangeSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasSearchField()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasDateRange() - 날짜 범위 존재 여부")
    class HasDateRangeTest {

        @Test
        @DisplayName("dateRange가 null이면 false를 반환한다")
        void returnFalseWhenDateRangeIsNull() {
            // given
            ExchangeSearchCriteria criteria = ExchangeSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasDateRange()).isFalse();
        }
    }

    @Nested
    @DisplayName("편의 메서드 테스트")
    class ConvenienceMethodsTest {

        @Test
        @DisplayName("size()는 QueryContext의 size를 반환한다")
        void sizeReturnsQueryContextSize() {
            // given
            QueryContext<ExchangeSortKey> queryContext =
                    QueryContext.of(
                            ExchangeSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 20));
            ExchangeSearchCriteria criteria =
                    ExchangeSearchCriteria.of(null, null, null, null, null, queryContext);

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("offset()은 QueryContext의 offset을 반환한다")
        void offsetReturnsQueryContextOffset() {
            // given
            QueryContext<ExchangeSortKey> queryContext =
                    QueryContext.of(
                            ExchangeSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(2, 10));
            ExchangeSearchCriteria criteria =
                    ExchangeSearchCriteria.of(null, null, null, null, null, queryContext);

            // then
            assertThat(criteria.offset()).isEqualTo(20L);
        }

        @Test
        @DisplayName("page()는 QueryContext의 page를 반환한다")
        void pageReturnsQueryContextPage() {
            // given
            QueryContext<ExchangeSortKey> queryContext =
                    QueryContext.of(
                            ExchangeSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(3, 10));
            ExchangeSearchCriteria criteria =
                    ExchangeSearchCriteria.of(null, null, null, null, null, queryContext);

            // then
            assertThat(criteria.page()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("statuses는 불변 리스트이다")
        void statusesIsUnmodifiable() {
            // given
            List<ExchangeStatus> statuses =
                    java.util.Arrays.asList(ExchangeStatus.REQUESTED, ExchangeStatus.COLLECTING);
            ExchangeSearchCriteria criteria =
                    ExchangeSearchCriteria.of(
                            statuses,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ExchangeSortKey.defaultKey()));

            // when & then
            assertThat(criteria.statuses())
                    .isNotSameAs(statuses)
                    .containsExactlyInAnyOrder(ExchangeStatus.REQUESTED, ExchangeStatus.COLLECTING);
        }
    }
}
