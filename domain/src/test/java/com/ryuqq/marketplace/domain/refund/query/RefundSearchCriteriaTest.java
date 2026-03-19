package com.ryuqq.marketplace.domain.refund.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundSearchCriteria 검색 조건 단위 테스트")
class RefundSearchCriteriaTest {

    @Nested
    @DisplayName("of() - 정적 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("모든 파라미터로 검색 조건을 생성한다")
        void createWithAllParameters() {
            // given
            List<RefundStatus> statuses = List.of(RefundStatus.REQUESTED, RefundStatus.COLLECTING);
            RefundSearchField searchField = RefundSearchField.CLAIM_NUMBER;
            String searchWord = "RFD-20260218";
            QueryContext<RefundSortKey> queryContext =
                    QueryContext.of(
                            RefundSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 10));

            // when
            RefundSearchCriteria criteria =
                    RefundSearchCriteria.of(
                            statuses,
                            null,
                            searchField,
                            searchWord,
                            null,
                            RefundDateField.REQUESTED,
                            queryContext);

            // then
            assertThat(criteria.statuses())
                    .containsExactlyInAnyOrder(RefundStatus.REQUESTED, RefundStatus.COLLECTING);
            assertThat(criteria.searchField()).isEqualTo(RefundSearchField.CLAIM_NUMBER);
            assertThat(criteria.searchWord()).isEqualTo("RFD-20260218");
            assertThat(criteria.dateField()).isEqualTo(RefundDateField.REQUESTED);
            assertThat(criteria.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("null 파라미터로 생성하면 statuses는 빈 리스트이다")
        void createWithNullStatuses() {
            // when
            RefundSearchCriteria criteria =
                    RefundSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(RefundSortKey.defaultKey()));

            // then
            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.isHold()).isNull();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.dateField()).isNull();
        }

        @Test
        @DisplayName("isHold 파라미터를 설정할 수 있다")
        void createWithIsHold() {
            // when
            RefundSearchCriteria criteria =
                    RefundSearchCriteria.of(
                            null,
                            true,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(RefundSortKey.defaultKey()));

            // then
            assertThat(criteria.isHold()).isTrue();
        }
    }

    @Nested
    @DisplayName("defaultCriteria() - 기본 검색 조건")
    class DefaultCriteriaTest {

        @Test
        @DisplayName("기본 검색 조건을 생성한다")
        void createDefaultCriteria() {
            // when
            RefundSearchCriteria criteria = RefundSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.isHold()).isNull();
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
            RefundSearchCriteria criteria =
                    RefundSearchCriteria.of(
                            List.of(RefundStatus.REQUESTED),
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(RefundSortKey.defaultKey()));

            // then
            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("statuses가 비어있으면 false를 반환한다")
        void returnFalseWhenStatusesEmpty() {
            // given
            RefundSearchCriteria criteria = RefundSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("statuses가 null이면 false를 반환한다")
        void returnFalseWhenStatusesNull() {
            // given
            RefundSearchCriteria criteria =
                    RefundSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(RefundSortKey.defaultKey()));

            // then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasHoldFilter() - 보류 필터 존재 여부")
    class HasHoldFilterTest {

        @Test
        @DisplayName("isHold가 true이면 hasHoldFilter()는 true이다")
        void returnTrueWhenIsHoldIsTrue() {
            // given
            RefundSearchCriteria criteria =
                    RefundSearchCriteria.of(
                            null,
                            true,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(RefundSortKey.defaultKey()));

            // then
            assertThat(criteria.hasHoldFilter()).isTrue();
        }

        @Test
        @DisplayName("isHold가 false이면 hasHoldFilter()는 true이다")
        void returnTrueWhenIsHoldIsFalse() {
            // given
            RefundSearchCriteria criteria =
                    RefundSearchCriteria.of(
                            null,
                            false,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(RefundSortKey.defaultKey()));

            // then
            assertThat(criteria.hasHoldFilter()).isTrue();
        }

        @Test
        @DisplayName("isHold가 null이면 hasHoldFilter()는 false이다")
        void returnFalseWhenIsHoldIsNull() {
            // given
            RefundSearchCriteria criteria = RefundSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasHoldFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchCondition() - 검색어 존재 여부")
    class HasSearchConditionTest {

        @Test
        @DisplayName("searchWord가 있으면 true를 반환한다")
        void returnTrueWhenSearchWordExists() {
            // given
            RefundSearchCriteria criteria =
                    RefundSearchCriteria.of(
                            null,
                            null,
                            RefundSearchField.CLAIM_NUMBER,
                            "RFD-20260218-0001",
                            null,
                            null,
                            QueryContext.defaultOf(RefundSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("searchWord가 null이면 false를 반환한다")
        void returnFalseWhenSearchWordIsNull() {
            // given
            RefundSearchCriteria criteria = RefundSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("searchWord가 빈 문자열이면 false를 반환한다")
        void returnFalseWhenSearchWordIsBlank() {
            // given
            RefundSearchCriteria criteria =
                    RefundSearchCriteria.of(
                            null,
                            null,
                            RefundSearchField.CLAIM_NUMBER,
                            "   ",
                            null,
                            null,
                            QueryContext.defaultOf(RefundSortKey.defaultKey()));

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
            RefundSearchCriteria criteria =
                    RefundSearchCriteria.of(
                            null,
                            null,
                            RefundSearchField.ORDER_NUMBER,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(RefundSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchField()).isTrue();
        }

        @Test
        @DisplayName("searchField가 null이면 false를 반환한다")
        void returnFalseWhenSearchFieldIsNull() {
            // given
            RefundSearchCriteria criteria = RefundSearchCriteria.defaultCriteria();

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
            RefundSearchCriteria criteria = RefundSearchCriteria.defaultCriteria();

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
            QueryContext<RefundSortKey> queryContext =
                    QueryContext.of(
                            RefundSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 20));
            RefundSearchCriteria criteria =
                    RefundSearchCriteria.of(null, null, null, null, null, null, queryContext);

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("offset()은 QueryContext의 offset을 반환한다")
        void offsetReturnsQueryContextOffset() {
            // given
            QueryContext<RefundSortKey> queryContext =
                    QueryContext.of(
                            RefundSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(2, 10));
            RefundSearchCriteria criteria =
                    RefundSearchCriteria.of(null, null, null, null, null, null, queryContext);

            // then
            assertThat(criteria.offset()).isEqualTo(20L);
        }

        @Test
        @DisplayName("page()는 QueryContext의 page를 반환한다")
        void pageReturnsQueryContextPage() {
            // given
            QueryContext<RefundSortKey> queryContext =
                    QueryContext.of(
                            RefundSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(3, 10));
            RefundSearchCriteria criteria =
                    RefundSearchCriteria.of(null, null, null, null, null, null, queryContext);

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
            List<RefundStatus> statuses =
                    java.util.Arrays.asList(RefundStatus.REQUESTED, RefundStatus.COLLECTING);
            RefundSearchCriteria criteria =
                    RefundSearchCriteria.of(
                            statuses,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(RefundSortKey.defaultKey()));

            // when & then
            assertThat(criteria.statuses())
                    .isNotSameAs(statuses)
                    .containsExactlyInAnyOrder(RefundStatus.REQUESTED, RefundStatus.COLLECTING);
        }
    }
}
