package com.ryuqq.marketplace.domain.cancel.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelSearchCriteria 단위 테스트")
class CancelSearchCriteriaTest {

    @Nested
    @DisplayName("defaultCriteria() - 기본 검색 조건 생성")
    class DefaultCriteriaTest {

        @Test
        @DisplayName("기본 검색 조건은 상태 필터 없이 생성된다")
        void defaultCriteriaHasEmptyStatuses() {
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            assertThat(criteria.statuses()).isEmpty();
        }

        @Test
        @DisplayName("기본 검색 조건은 유형 필터 없이 생성된다")
        void defaultCriteriaHasEmptyTypes() {
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            assertThat(criteria.types()).isEmpty();
        }

        @Test
        @DisplayName("기본 검색 조건은 searchField가 null이다")
        void defaultCriteriaHasNullSearchField() {
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            assertThat(criteria.searchField()).isNull();
        }

        @Test
        @DisplayName("기본 검색 조건은 searchWord가 null이다")
        void defaultCriteriaHasNullSearchWord() {
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            assertThat(criteria.searchWord()).isNull();
        }

        @Test
        @DisplayName("기본 검색 조건은 dateRange가 null이다")
        void defaultCriteriaHasNullDateRange() {
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            assertThat(criteria.dateRange()).isNull();
        }

        @Test
        @DisplayName("기본 검색 조건은 dateField가 null이다")
        void defaultCriteriaHasNullDateField() {
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            assertThat(criteria.dateField()).isNull();
        }

        @Test
        @DisplayName("기본 검색 조건은 QueryContext를 가진다")
        void defaultCriteriaHasQueryContext() {
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            assertThat(criteria.queryContext()).isNotNull();
        }
    }

    @Nested
    @DisplayName("of() - 직접 생성")
    class OfTest {

        @Test
        @DisplayName("상태 필터와 유형 필터를 포함한 검색 조건을 생성한다")
        void createWithStatusAndTypeFilter() {
            List<CancelStatus> statuses = List.of(CancelStatus.REQUESTED, CancelStatus.APPROVED);
            List<CancelType> types = List.of(CancelType.BUYER_CANCEL);
            QueryContext<CancelSortKey> queryContext =
                    QueryContext.defaultOf(CancelSortKey.defaultKey());

            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(statuses, types, null, null, null, null, queryContext);

            assertThat(criteria.statuses())
                    .containsExactlyInAnyOrder(CancelStatus.REQUESTED, CancelStatus.APPROVED);
            assertThat(criteria.types()).containsOnly(CancelType.BUYER_CANCEL);
        }

        @Test
        @DisplayName("null 상태 리스트는 빈 리스트로 처리된다")
        void nullStatusListBecomesEmpty() {
            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.types()).isEmpty();
        }

        @Test
        @DisplayName("검색어와 검색 필드를 포함한 조건을 생성한다")
        void createWithSearchWordAndField() {
            QueryContext<CancelSortKey> queryContext =
                    QueryContext.defaultOf(CancelSortKey.defaultKey());

            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            null,
                            null,
                            CancelSearchField.CANCEL_NUMBER,
                            "CAN-20240101-0001",
                            null,
                            null,
                            queryContext);

            assertThat(criteria.searchField()).isEqualTo(CancelSearchField.CANCEL_NUMBER);
            assertThat(criteria.searchWord()).isEqualTo("CAN-20240101-0001");
        }
    }

    @Nested
    @DisplayName("hasStatusFilter() 테스트")
    class HasStatusFilterTest {

        @Test
        @DisplayName("상태 필터가 있으면 true를 반환한다")
        void returnsTrueWhenStatusFilterExists() {
            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            List.of(CancelStatus.REQUESTED),
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 없으면 false를 반환한다")
        void returnsFalseWhenStatusFilterIsEmpty() {
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            assertThat(criteria.hasStatusFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasTypeFilter() 테스트")
    class HasTypeFilterTest {

        @Test
        @DisplayName("유형 필터가 있으면 true를 반환한다")
        void returnsTrueWhenTypeFilterExists() {
            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            null,
                            List.of(CancelType.SELLER_CANCEL),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            assertThat(criteria.hasTypeFilter()).isTrue();
        }

        @Test
        @DisplayName("유형 필터가 없으면 false를 반환한다")
        void returnsFalseWhenTypeFilterIsEmpty() {
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            assertThat(criteria.hasTypeFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchCondition() 테스트")
    class HasSearchConditionTest {

        @Test
        @DisplayName("검색어가 있으면 true를 반환한다")
        void returnsTrueWhenSearchWordExists() {
            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            null,
                            null,
                            CancelSearchField.CUSTOMER_NAME,
                            "홍길동",
                            null,
                            null,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("검색어가 null이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsNull() {
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("검색어가 공백이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsBlank() {
            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            null,
                            null,
                            null,
                            "   ",
                            null,
                            null,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            assertThat(criteria.hasSearchCondition()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchField() 테스트")
    class HasSearchFieldTest {

        @Test
        @DisplayName("검색 필드가 있으면 true를 반환한다")
        void returnsTrueWhenSearchFieldExists() {
            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            null,
                            null,
                            CancelSearchField.PRODUCT_NAME,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            assertThat(criteria.hasSearchField()).isTrue();
        }

        @Test
        @DisplayName("검색 필드가 null이면 false를 반환한다")
        void returnsFalseWhenSearchFieldIsNull() {
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            assertThat(criteria.hasSearchField()).isFalse();
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("statuses 리스트는 외부 변경에 영향을 받지 않는다")
        void statusListIsImmutable() {
            List<CancelStatus> mutableStatuses = new ArrayList<>(List.of(CancelStatus.REQUESTED));

            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            mutableStatuses,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            mutableStatuses.add(CancelStatus.APPROVED);

            assertThat(criteria.statuses()).hasSize(1);
            assertThat(criteria.statuses()).containsOnly(CancelStatus.REQUESTED);
        }

        @Test
        @DisplayName("types 리스트는 외부 변경에 영향을 받지 않는다")
        void typeListIsImmutable() {
            List<CancelType> mutableTypes = new ArrayList<>(List.of(CancelType.BUYER_CANCEL));

            CancelSearchCriteria criteria =
                    CancelSearchCriteria.of(
                            null,
                            mutableTypes,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CancelSortKey.defaultKey()));

            mutableTypes.add(CancelType.SELLER_CANCEL);

            assertThat(criteria.types()).hasSize(1);
            assertThat(criteria.types()).containsOnly(CancelType.BUYER_CANCEL);
        }
    }

    @Nested
    @DisplayName("QueryContext 페이징 위임 테스트")
    class QueryContextDelegationTest {

        @Test
        @DisplayName("size()는 QueryContext의 size를 반환한다")
        void sizeDelegate() {
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            assertThat(criteria.size()).isPositive();
        }

        @Test
        @DisplayName("page()는 QueryContext의 page를 반환한다")
        void pageDelegate() {
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            assertThat(criteria.page()).isNotNegative();
        }
    }
}
