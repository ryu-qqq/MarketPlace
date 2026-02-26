package com.ryuqq.marketplace.domain.saleschannelcategory.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.saleschannelcategory.vo.SalesChannelCategoryStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelCategorySearchCriteria 테스트")
class SalesChannelCategorySearchCriteriaTest {

    @Nested
    @DisplayName("of() - 정적 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("모든 파라미터로 검색 조건을 생성한다")
        void createWithAllParameters() {
            // given
            List<Long> salesChannelIds = List.of(1L, 2L);
            List<SalesChannelCategoryStatus> statuses =
                    List.of(SalesChannelCategoryStatus.ACTIVE, SalesChannelCategoryStatus.INACTIVE);
            SalesChannelCategorySearchField searchField =
                    SalesChannelCategorySearchField.EXTERNAL_NAME;
            String searchWord = "테스트";
            QueryContext<SalesChannelCategorySortKey> queryContext =
                    QueryContext.of(
                            SalesChannelCategorySortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            salesChannelIds, statuses, searchField, searchWord, queryContext);

            // then
            assertThat(criteria.salesChannelIds()).containsExactly(1L, 2L);
            assertThat(criteria.statuses())
                    .containsExactly(
                            SalesChannelCategoryStatus.ACTIVE, SalesChannelCategoryStatus.INACTIVE);
            assertThat(criteria.searchField())
                    .isEqualTo(SalesChannelCategorySearchField.EXTERNAL_NAME);
            assertThat(criteria.searchWord()).isEqualTo("테스트");
            assertThat(criteria.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("null 파라미터로 검색 조건을 생성한다")
        void createWithNullParameters() {
            // given
            QueryContext<SalesChannelCategorySortKey> queryContext =
                    QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey());

            // when
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(null, null, null, null, queryContext);

            // then
            assertThat(criteria.salesChannelIds()).isEmpty();
            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
        }
    }

    @Nested
    @DisplayName("hasSalesChannelFilter() - 판매채널 필터 존재 여부")
    class HasSalesChannelFilterTest {

        @Test
        @DisplayName("판매채널 ID가 있으면 true를 반환한다")
        void returnTrueWhenSalesChannelIdsExist() {
            // given
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            List.of(1L),
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey()));

            // then
            assertThat(criteria.hasSalesChannelFilter()).isTrue();
        }

        @Test
        @DisplayName("판매채널 ID가 없으면 false를 반환한다")
        void returnFalseWhenSalesChannelIdsEmpty() {
            // given
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey()));

            // then
            assertThat(criteria.hasSalesChannelFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasStatusFilter() - 상태 필터 존재 여부")
    class HasStatusFilterTest {

        @Test
        @DisplayName("상태 필터가 있으면 true를 반환한다")
        void returnTrueWhenStatusesExist() {
            // given
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            List.of(SalesChannelCategoryStatus.ACTIVE),
                            null,
                            null,
                            QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey()));

            // then
            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 없으면 false를 반환한다")
        void returnFalseWhenStatusesEmpty() {
            // given
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey()));

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
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            SalesChannelCategorySearchField.EXTERNAL_NAME,
                            "검색어",
                            QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("검색어가 null이면 false를 반환한다")
        void returnFalseWhenSearchWordIsNull() {
            // given
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("검색어가 빈 문자열이면 false를 반환한다")
        void returnFalseWhenSearchWordIsBlank() {
            // given
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            SalesChannelCategorySearchField.EXTERNAL_NAME,
                            "   ",
                            QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey()));

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
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            SalesChannelCategorySearchField.EXTERNAL_NAME,
                            null,
                            QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchField()).isTrue();
        }

        @Test
        @DisplayName("검색 필드가 null이면 false를 반환한다")
        void returnFalseWhenSearchFieldIsNull() {
            // given
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey()));

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
            QueryContext<SalesChannelCategorySortKey> queryContext =
                    QueryContext.of(
                            SalesChannelCategorySortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(null, null, null, null, queryContext);

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("offset()은 QueryContext의 offset을 반환한다")
        void offsetReturnsQueryContextOffset() {
            // given
            QueryContext<SalesChannelCategorySortKey> queryContext =
                    QueryContext.of(
                            SalesChannelCategorySortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(2, 10));
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(null, null, null, null, queryContext);

            // then
            assertThat(criteria.offset()).isEqualTo(20L);
        }

        @Test
        @DisplayName("page()는 QueryContext의 page를 반환한다")
        void pageReturnsQueryContextPage() {
            // given
            QueryContext<SalesChannelCategorySortKey> queryContext =
                    QueryContext.of(
                            SalesChannelCategorySortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(3, 10));
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(null, null, null, null, queryContext);

            // then
            assertThat(criteria.page()).isEqualTo(3);
        }
    }
}
