package com.ryuqq.marketplace.domain.shop.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShopSearchCriteria 단위 테스트")
class ShopSearchCriteriaTest {

    @Nested
    @DisplayName("of() - 정적 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("모든 파라미터로 검색 조건을 생성한다")
        void createWithAllParameters() {
            // given
            List<ShopStatus> statuses = List.of(ShopStatus.ACTIVE);
            ShopSearchField searchField = ShopSearchField.SHOP_NAME;
            String searchWord = "테스트";
            QueryContext<ShopSortKey> queryContext =
                    QueryContext.of(
                            ShopSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 10));

            // when
            ShopSearchCriteria criteria =
                    ShopSearchCriteria.of(statuses, searchField, searchWord, queryContext);

            // then
            assertThat(criteria.statuses()).containsExactly(ShopStatus.ACTIVE);
            assertThat(criteria.searchField()).isEqualTo(ShopSearchField.SHOP_NAME);
            assertThat(criteria.searchWord()).isEqualTo("테스트");
            assertThat(criteria.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("null 상태 리스트는 빈 리스트로 생성된다")
        void createWithNullStatusesList() {
            // given
            QueryContext<ShopSortKey> queryContext =
                    QueryContext.defaultOf(ShopSortKey.defaultKey());

            // when
            ShopSearchCriteria criteria = ShopSearchCriteria.of(null, null, null, queryContext);

            // then
            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
        }

        @Test
        @DisplayName("여러 상태로 검색 조건을 생성한다")
        void createWithMultipleStatuses() {
            // given
            List<ShopStatus> statuses = List.of(ShopStatus.ACTIVE, ShopStatus.INACTIVE);
            QueryContext<ShopSortKey> queryContext =
                    QueryContext.defaultOf(ShopSortKey.defaultKey());

            // when
            ShopSearchCriteria criteria = ShopSearchCriteria.of(statuses, null, null, queryContext);

            // then
            assertThat(criteria.statuses()).containsExactly(ShopStatus.ACTIVE, ShopStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("defaultCriteria() - 기본 검색 조건")
    class DefaultCriteriaTest {

        @Test
        @DisplayName("기본 검색 조건을 생성한다")
        void createDefaultCriteria() {
            // when
            ShopSearchCriteria criteria = ShopSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.queryContext()).isNotNull();
        }
    }

    @Nested
    @DisplayName("activeOnly() - 활성화 항목만 조회")
    class ActiveOnlyTest {

        @Test
        @DisplayName("활성화된 항목만 조회하는 조건을 생성한다")
        void createActiveOnlyCriteria() {
            // when
            ShopSearchCriteria criteria = ShopSearchCriteria.activeOnly();

            // then
            assertThat(criteria.statuses()).containsExactly(ShopStatus.ACTIVE);
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
        }
    }

    @Nested
    @DisplayName("hasStatusFilter() - 상태 필터 존재 여부")
    class HasStatusFilterTest {

        @Test
        @DisplayName("상태 필터가 있으면 true를 반환한다")
        void returnTrueWhenStatusFilterExists() {
            // given
            ShopSearchCriteria criteria = ShopSearchCriteria.activeOnly();

            // then
            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 빈 리스트면 false를 반환한다")
        void returnFalseWhenStatusFilterIsEmpty() {
            // given
            ShopSearchCriteria criteria = ShopSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchCondition() - 검색 조건 존재 여부")
    class HasSearchConditionTest {

        @Test
        @DisplayName("검색어가 있으면 true를 반환한다")
        void returnTrueWhenSearchWordExists() {
            // given
            ShopSearchCriteria criteria =
                    ShopSearchCriteria.of(
                            List.of(),
                            ShopSearchField.SHOP_NAME,
                            "검색어",
                            QueryContext.defaultOf(ShopSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("검색어가 null이면 false를 반환한다")
        void returnFalseWhenSearchWordIsNull() {
            // given
            ShopSearchCriteria criteria = ShopSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("검색어가 빈 문자열이면 false를 반환한다")
        void returnFalseWhenSearchWordIsBlank() {
            // given
            ShopSearchCriteria criteria =
                    ShopSearchCriteria.of(
                            List.of(),
                            ShopSearchField.SHOP_NAME,
                            "   ",
                            QueryContext.defaultOf(ShopSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchCondition()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchField() - 검색 필드 존재 여부")
    class HasSearchFieldTest {

        @Test
        @DisplayName("검색 필드가 있으면 true를 반환한다")
        void returnTrueWhenSearchFieldExists() {
            // given
            ShopSearchCriteria criteria =
                    ShopSearchCriteria.of(
                            List.of(),
                            ShopSearchField.SHOP_NAME,
                            null,
                            QueryContext.defaultOf(ShopSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchField()).isTrue();
        }

        @Test
        @DisplayName("검색 필드가 null이면 false를 반환한다")
        void returnFalseWhenSearchFieldIsNull() {
            // given
            ShopSearchCriteria criteria = ShopSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasSearchField()).isFalse();
        }
    }

    @Nested
    @DisplayName("편의 메서드 테스트")
    class ConvenienceMethodsTest {

        @Test
        @DisplayName("size()는 QueryContext의 size를 반환한다")
        void sizeReturnsQueryContextSize() {
            // given
            QueryContext<ShopSortKey> queryContext =
                    QueryContext.of(
                            ShopSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 20));
            ShopSearchCriteria criteria =
                    ShopSearchCriteria.of(List.of(), null, null, queryContext);

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("offset()은 QueryContext의 offset을 반환한다")
        void offsetReturnsQueryContextOffset() {
            // given
            QueryContext<ShopSortKey> queryContext =
                    QueryContext.of(
                            ShopSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(2, 10));
            ShopSearchCriteria criteria =
                    ShopSearchCriteria.of(List.of(), null, null, queryContext);

            // then
            assertThat(criteria.offset()).isEqualTo(20L);
        }

        @Test
        @DisplayName("page()는 QueryContext의 page를 반환한다")
        void pageReturnsQueryContextPage() {
            // given
            QueryContext<ShopSortKey> queryContext =
                    QueryContext.of(
                            ShopSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(3, 10));
            ShopSearchCriteria criteria =
                    ShopSearchCriteria.of(List.of(), null, null, queryContext);

            // then
            assertThat(criteria.page()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("statuses 리스트는 불변이다")
        void statusesListIsImmutable() {
            // given
            List<ShopStatus> statuses = List.of(ShopStatus.ACTIVE);
            ShopSearchCriteria criteria =
                    ShopSearchCriteria.of(
                            statuses, null, null, QueryContext.defaultOf(ShopSortKey.defaultKey()));

            // when & then
            assertThatThrownBy(() -> criteria.statuses().add(ShopStatus.INACTIVE))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
