package com.ryuqq.marketplace.domain.order.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderSearchCriteria 테스트")
class OrderSearchCriteriaTest {

    @Nested
    @DisplayName("of() - 정적 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("모든 파라미터로 검색 조건을 생성한다")
        void createWithAllParameters() {
            // given
            List<OrderItemStatus> statuses =
                    List.of(OrderItemStatus.READY, OrderItemStatus.CONFIRMED);
            OrderSearchField searchField = OrderSearchField.ORDER_NUMBER;
            String searchWord = "ORD-2026";
            DateRange dateRange = DateRange.of(LocalDate.now().minusDays(1), LocalDate.now());
            OrderDateField dateField = OrderDateField.ORDERED;
            QueryContext<OrderSortKey> queryContext =
                    QueryContext.of(
                            OrderSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 10));

            // when
            OrderSearchCriteria criteria =
                    OrderSearchCriteria.of(
                            statuses, searchField, searchWord, dateRange, dateField, queryContext);

            // then
            assertThat(criteria.statuses())
                    .containsExactly(OrderItemStatus.READY, OrderItemStatus.CONFIRMED);
            assertThat(criteria.searchField()).isEqualTo(OrderSearchField.ORDER_NUMBER);
            assertThat(criteria.searchWord()).isEqualTo("ORD-2026");
            assertThat(criteria.dateRange()).isEqualTo(dateRange);
            assertThat(criteria.dateField()).isEqualTo(OrderDateField.ORDERED);
            assertThat(criteria.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("null statuses는 빈 목록으로 변환된다")
        void nullStatusesConvertsToEmptyList() {
            // when
            OrderSearchCriteria criteria =
                    OrderSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(OrderSortKey.defaultKey()));

            // then
            assertThat(criteria.statuses()).isEmpty();
        }
    }

    @Nested
    @DisplayName("defaultCriteria() - 기본 검색 조건")
    class DefaultCriteriaTest {

        @Test
        @DisplayName("기본 검색 조건을 생성한다")
        void createDefaultCriteria() {
            // when
            OrderSearchCriteria criteria = OrderSearchCriteria.defaultCriteria();

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
        @DisplayName("statuses가 있으면 true를 반환한다")
        void returnsTrueWhenStatusesExist() {
            // given
            OrderSearchCriteria criteria =
                    OrderSearchCriteria.of(
                            List.of(OrderItemStatus.READY),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(OrderSortKey.defaultKey()));

            // then
            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("statuses가 비어있으면 false를 반환한다")
        void returnsFalseWhenStatusesEmpty() {
            // given
            OrderSearchCriteria criteria = OrderSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchCondition() - 검색어 존재 여부")
    class HasSearchConditionTest {

        @Test
        @DisplayName("검색어가 있으면 true를 반환한다")
        void returnsTrueWhenSearchWordExists() {
            // given
            OrderSearchCriteria criteria =
                    OrderSearchCriteria.of(
                            null,
                            OrderSearchField.ORDER_NUMBER,
                            "ORD-2026",
                            null,
                            null,
                            QueryContext.defaultOf(OrderSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("검색어가 null이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsNull() {
            // given
            OrderSearchCriteria criteria = OrderSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("검색어가 빈 문자열이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsBlank() {
            // given
            OrderSearchCriteria criteria =
                    OrderSearchCriteria.of(
                            null,
                            null,
                            "   ",
                            null,
                            null,
                            QueryContext.defaultOf(OrderSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchCondition()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchField() - 검색 필드 존재 여부")
    class HasSearchFieldTest {

        @Test
        @DisplayName("검색 필드가 있으면 true를 반환한다")
        void returnsTrueWhenSearchFieldExists() {
            // given
            OrderSearchCriteria criteria =
                    OrderSearchCriteria.of(
                            null,
                            OrderSearchField.ORDER_NUMBER,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(OrderSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchField()).isTrue();
        }

        @Test
        @DisplayName("검색 필드가 null이면 false를 반환한다")
        void returnsFalseWhenSearchFieldIsNull() {
            // given
            OrderSearchCriteria criteria = OrderSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasSearchField()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasDateRange() - 날짜 범위 존재 여부")
    class HasDateRangeTest {

        @Test
        @DisplayName("날짜 범위가 있으면 true를 반환한다")
        void returnsTrueWhenDateRangeExists() {
            // given
            DateRange dateRange = DateRange.of(LocalDate.now().minusDays(1), LocalDate.now());
            OrderSearchCriteria criteria =
                    OrderSearchCriteria.of(
                            null,
                            null,
                            null,
                            dateRange,
                            null,
                            QueryContext.defaultOf(OrderSortKey.defaultKey()));

            // then
            assertThat(criteria.hasDateRange()).isTrue();
        }

        @Test
        @DisplayName("날짜 범위가 null이면 false를 반환한다")
        void returnsFalseWhenDateRangeIsNull() {
            // given
            OrderSearchCriteria criteria = OrderSearchCriteria.defaultCriteria();

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
            QueryContext<OrderSortKey> queryContext =
                    QueryContext.of(
                            OrderSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 20));
            OrderSearchCriteria criteria =
                    OrderSearchCriteria.of(null, null, null, null, null, queryContext);

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("offset()은 QueryContext의 offset을 반환한다")
        void offsetReturnsQueryContextOffset() {
            // given
            QueryContext<OrderSortKey> queryContext =
                    QueryContext.of(
                            OrderSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(2, 10));
            OrderSearchCriteria criteria =
                    OrderSearchCriteria.of(null, null, null, null, null, queryContext);

            // then
            assertThat(criteria.offset()).isEqualTo(20L);
        }

        @Test
        @DisplayName("page()는 QueryContext의 page를 반환한다")
        void pageReturnsQueryContextPage() {
            // given
            QueryContext<OrderSortKey> queryContext =
                    QueryContext.of(
                            OrderSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(3, 10));
            OrderSearchCriteria criteria =
                    OrderSearchCriteria.of(null, null, null, null, null, queryContext);

            // then
            assertThat(criteria.page()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("statuses 불변성 테스트")
    class StatusesImmutabilityTest {

        @Test
        @DisplayName("statuses 목록은 불변이다")
        void statusesListIsUnmodifiable() {
            // given
            OrderSearchCriteria criteria =
                    OrderSearchCriteria.of(
                            List.of(OrderItemStatus.READY),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(OrderSortKey.defaultKey()));

            // when & then
            assertThatThrownBy(() -> criteria.statuses().add(OrderItemStatus.CONFIRMED))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
