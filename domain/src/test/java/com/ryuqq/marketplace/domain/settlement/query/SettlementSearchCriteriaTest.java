package com.ryuqq.marketplace.domain.settlement.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementSearchCriteria 단위 테스트")
class SettlementSearchCriteriaTest {

    @Nested
    @DisplayName("defaultCriteria() - 기본 검색 조건 생성")
    class DefaultCriteriaTest {

        @Test
        @DisplayName("기본 검색 조건은 필터 없는 상태로 생성된다")
        void createDefaultCriteria() {
            SettlementSearchCriteria criteria = SettlementSearchCriteria.defaultCriteria();

            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.sellerIds()).isEmpty();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.dateRange()).isNull();
            assertThat(criteria.dateField()).isNull();
        }

        @Test
        @DisplayName("기본 검색 조건은 QueryContext를 가진다")
        void defaultCriteriaHasQueryContext() {
            SettlementSearchCriteria criteria = SettlementSearchCriteria.defaultCriteria();

            assertThat(criteria.queryContext()).isNotNull();
        }
    }

    @Nested
    @DisplayName("of() - 직접 생성")
    class OfTest {

        @Test
        @DisplayName("상태 필터와 셀러 필터를 포함한 검색 조건을 생성한다")
        void createWithStatusAndSellerFilter() {
            List<SettlementStatus> statuses =
                    List.of(SettlementStatus.CALCULATING, SettlementStatus.CONFIRMED);
            List<Long> sellerIds = List.of(1L, 2L);
            QueryContext<SettlementSortKey> queryContext =
                    QueryContext.defaultOf(SettlementSortKey.defaultKey());

            SettlementSearchCriteria criteria =
                    SettlementSearchCriteria.of(
                            statuses, sellerIds, null, null, null, null, queryContext);

            assertThat(criteria.statuses())
                    .containsExactlyInAnyOrder(
                            SettlementStatus.CALCULATING, SettlementStatus.CONFIRMED);
            assertThat(criteria.sellerIds()).containsExactlyInAnyOrder(1L, 2L);
        }

        @Test
        @DisplayName("null 상태 리스트는 빈 리스트로 처리된다")
        void nullStatusListBecomesEmpty() {
            SettlementSearchCriteria criteria =
                    SettlementSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SettlementSortKey.defaultKey()));

            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.sellerIds()).isEmpty();
        }

        @Test
        @DisplayName("검색어와 검색 필드를 포함한 조건을 생성한다")
        void createWithSearchWordAndField() {
            QueryContext<SettlementSortKey> queryContext =
                    QueryContext.defaultOf(SettlementSortKey.defaultKey());

            SettlementSearchCriteria criteria =
                    SettlementSearchCriteria.of(
                            null,
                            null,
                            SettlementSearchField.ORDER_ID,
                            "oi-12345",
                            null,
                            null,
                            queryContext);

            assertThat(criteria.searchField()).isEqualTo(SettlementSearchField.ORDER_ID);
            assertThat(criteria.searchWord()).isEqualTo("oi-12345");
        }
    }

    @Nested
    @DisplayName("hasStatusFilter() 테스트")
    class HasStatusFilterTest {

        @Test
        @DisplayName("상태 필터가 있으면 true를 반환한다")
        void returnsTrueWhenStatusFilterExists() {
            SettlementSearchCriteria criteria =
                    SettlementSearchCriteria.of(
                            List.of(SettlementStatus.CALCULATING),
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SettlementSortKey.defaultKey()));

            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 없으면 false를 반환한다")
        void returnsFalseWhenStatusFilterIsEmpty() {
            SettlementSearchCriteria criteria = SettlementSearchCriteria.defaultCriteria();

            assertThat(criteria.hasStatusFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSellerFilter() 테스트")
    class HasSellerFilterTest {

        @Test
        @DisplayName("셀러 필터가 있으면 true를 반환한다")
        void returnsTrueWhenSellerFilterExists() {
            SettlementSearchCriteria criteria =
                    SettlementSearchCriteria.of(
                            null,
                            List.of(1L, 2L),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SettlementSortKey.defaultKey()));

            assertThat(criteria.hasSellerFilter()).isTrue();
        }

        @Test
        @DisplayName("셀러 필터가 없으면 false를 반환한다")
        void returnsFalseWhenSellerFilterIsEmpty() {
            SettlementSearchCriteria criteria = SettlementSearchCriteria.defaultCriteria();

            assertThat(criteria.hasSellerFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchCondition() 테스트")
    class HasSearchConditionTest {

        @Test
        @DisplayName("검색어가 있으면 true를 반환한다")
        void returnsTrueWhenSearchWordExists() {
            SettlementSearchCriteria criteria =
                    SettlementSearchCriteria.of(
                            null,
                            null,
                            SettlementSearchField.BUYER_NAME,
                            "홍길동",
                            null,
                            null,
                            QueryContext.defaultOf(SettlementSortKey.defaultKey()));

            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("검색어가 null이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsNull() {
            SettlementSearchCriteria criteria = SettlementSearchCriteria.defaultCriteria();

            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("검색어가 공백이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsBlank() {
            SettlementSearchCriteria criteria =
                    SettlementSearchCriteria.of(
                            null,
                            null,
                            null,
                            "   ",
                            null,
                            null,
                            QueryContext.defaultOf(SettlementSortKey.defaultKey()));

            assertThat(criteria.hasSearchCondition()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchField() 테스트")
    class HasSearchFieldTest {

        @Test
        @DisplayName("검색 필드가 있으면 true를 반환한다")
        void returnsTrueWhenSearchFieldExists() {
            SettlementSearchCriteria criteria =
                    SettlementSearchCriteria.of(
                            null,
                            null,
                            SettlementSearchField.PRODUCT_NAME,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SettlementSortKey.defaultKey()));

            assertThat(criteria.hasSearchField()).isTrue();
        }

        @Test
        @DisplayName("검색 필드가 null이면 false를 반환한다")
        void returnsFalseWhenSearchFieldIsNull() {
            SettlementSearchCriteria criteria = SettlementSearchCriteria.defaultCriteria();

            assertThat(criteria.hasSearchField()).isFalse();
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("statuses 리스트는 외부 변경에 영향을 받지 않는다")
        void statusListIsImmutable() {
            List<SettlementStatus> mutableStatuses =
                    new java.util.ArrayList<>(List.of(SettlementStatus.CALCULATING));

            SettlementSearchCriteria criteria =
                    SettlementSearchCriteria.of(
                            mutableStatuses,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SettlementSortKey.defaultKey()));

            mutableStatuses.add(SettlementStatus.COMPLETED);

            assertThat(criteria.statuses()).hasSize(1);
            assertThat(criteria.statuses()).containsOnly(SettlementStatus.CALCULATING);
        }

        @Test
        @DisplayName("sellerIds 리스트는 외부 변경에 영향을 받지 않는다")
        void sellerIdListIsImmutable() {
            List<Long> mutableSellerIds = new java.util.ArrayList<>(List.of(1L));

            SettlementSearchCriteria criteria =
                    SettlementSearchCriteria.of(
                            null,
                            mutableSellerIds,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SettlementSortKey.defaultKey()));

            mutableSellerIds.add(999L);

            assertThat(criteria.sellerIds()).hasSize(1);
            assertThat(criteria.sellerIds()).containsOnly(1L);
        }
    }
}
