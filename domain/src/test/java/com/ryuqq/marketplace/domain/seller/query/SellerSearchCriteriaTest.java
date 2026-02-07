package com.ryuqq.marketplace.domain.seller.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerSearchCriteria 테스트")
class SellerSearchCriteriaTest {

    @Nested
    @DisplayName("of() - 정적 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("모든 파라미터로 검색 조건을 생성한다")
        void createWithAllParameters() {
            // given
            Boolean active = true;
            SellerSearchField searchField = SellerSearchField.SELLER_NAME;
            String searchWord = "테스트";
            QueryContext<SellerSortKey> queryContext =
                    QueryContext.of(
                            SellerSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 10));

            // when
            SellerSearchCriteria criteria =
                    SellerSearchCriteria.of(active, searchField, searchWord, queryContext);

            // then
            assertThat(criteria.active()).isTrue();
            assertThat(criteria.searchField()).isEqualTo(SellerSearchField.SELLER_NAME);
            assertThat(criteria.searchWord()).isEqualTo("테스트");
            assertThat(criteria.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("null 파라미터로 검색 조건을 생성한다")
        void createWithNullParameters() {
            // given
            QueryContext<SellerSortKey> queryContext =
                    QueryContext.defaultOf(SellerSortKey.defaultKey());

            // when
            SellerSearchCriteria criteria = SellerSearchCriteria.of(null, null, null, queryContext);

            // then
            assertThat(criteria.active()).isNull();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
        }
    }

    @Nested
    @DisplayName("defaultCriteria() - 기본 검색 조건")
    class DefaultCriteriaTest {

        @Test
        @DisplayName("기본 검색 조건을 생성한다")
        void createDefaultCriteria() {
            // when
            SellerSearchCriteria criteria = SellerSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.active()).isNull();
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
            SellerSearchCriteria criteria = SellerSearchCriteria.activeOnly();

            // then
            assertThat(criteria.active()).isTrue();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
        }
    }

    @Nested
    @DisplayName("hasSearchCondition() - 검색 조건 존재 여부")
    class HasSearchConditionTest {

        @Test
        @DisplayName("검색어가 있으면 true를 반환한다")
        void returnTrueWhenSearchWordExists() {
            // given
            SellerSearchCriteria criteria =
                    SellerSearchCriteria.of(
                            null,
                            SellerSearchField.SELLER_NAME,
                            "검색어",
                            QueryContext.defaultOf(SellerSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("검색어가 null이면 false를 반환한다")
        void returnFalseWhenSearchWordIsNull() {
            // given
            SellerSearchCriteria criteria = SellerSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("검색어가 빈 문자열이면 false를 반환한다")
        void returnFalseWhenSearchWordIsBlank() {
            // given
            SellerSearchCriteria criteria =
                    SellerSearchCriteria.of(
                            null,
                            SellerSearchField.SELLER_NAME,
                            "   ",
                            QueryContext.defaultOf(SellerSortKey.defaultKey()));

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
            SellerSearchCriteria criteria =
                    SellerSearchCriteria.of(
                            null,
                            SellerSearchField.SELLER_NAME,
                            null,
                            QueryContext.defaultOf(SellerSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchField()).isTrue();
        }

        @Test
        @DisplayName("검색 필드가 null이면 false를 반환한다")
        void returnFalseWhenSearchFieldIsNull() {
            // given
            SellerSearchCriteria criteria = SellerSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasSearchField()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasActiveFilter() - 활성화 필터 존재 여부")
    class HasActiveFilterTest {

        @Test
        @DisplayName("활성화 필터가 있으면 true를 반환한다")
        void returnTrueWhenActiveFilterExists() {
            // given
            SellerSearchCriteria criteria = SellerSearchCriteria.activeOnly();

            // then
            assertThat(criteria.hasActiveFilter()).isTrue();
        }

        @Test
        @DisplayName("활성화 필터가 null이면 false를 반환한다")
        void returnFalseWhenActiveFilterIsNull() {
            // given
            SellerSearchCriteria criteria = SellerSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasActiveFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("편의 메서드 테스트")
    class ConvenienceMethodsTest {

        @Test
        @DisplayName("size()는 QueryContext의 size를 반환한다")
        void sizeReturnsQueryContextSize() {
            // given
            QueryContext<SellerSortKey> queryContext =
                    QueryContext.of(
                            SellerSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 20));
            SellerSearchCriteria criteria = SellerSearchCriteria.of(null, null, null, queryContext);

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("offset()은 QueryContext의 offset을 반환한다")
        void offsetReturnsQueryContextOffset() {
            // given
            QueryContext<SellerSortKey> queryContext =
                    QueryContext.of(
                            SellerSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(2, 10));
            SellerSearchCriteria criteria = SellerSearchCriteria.of(null, null, null, queryContext);

            // then
            assertThat(criteria.offset()).isEqualTo(20L);
        }

        @Test
        @DisplayName("page()는 QueryContext의 page를 반환한다")
        void pageReturnsQueryContextPage() {
            // given
            QueryContext<SellerSortKey> queryContext =
                    QueryContext.of(
                            SellerSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(3, 10));
            SellerSearchCriteria criteria = SellerSearchCriteria.of(null, null, null, queryContext);

            // then
            assertThat(criteria.page()).isEqualTo(3);
        }
    }
}
