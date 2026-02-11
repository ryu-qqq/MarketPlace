package com.ryuqq.marketplace.domain.categorymapping.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.categorymapping.vo.CategoryMappingStatus;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryMappingSearchCriteria 테스트")
class CategoryMappingSearchCriteriaTest {

    @Nested
    @DisplayName("of() - 정적 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("모든 파라미터로 검색 조건을 생성한다")
        void createWithAllParameters() {
            // given
            List<Long> salesChannelCategoryIds = List.of(100L, 200L);
            List<Long> internalCategoryIds = List.of(10L, 20L);
            List<Long> salesChannelIds = List.of(1L, 2L);
            List<CategoryMappingStatus> statuses = List.of(CategoryMappingStatus.ACTIVE);
            CategoryMappingSearchField searchField =
                    CategoryMappingSearchField.EXTERNAL_CATEGORY_NAME;
            String searchWord = "테스트";
            QueryContext<CategoryMappingSortKey> queryContext =
                    QueryContext.of(
                            CategoryMappingSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.of(
                            salesChannelCategoryIds,
                            internalCategoryIds,
                            salesChannelIds,
                            statuses,
                            searchField,
                            searchWord,
                            queryContext);

            // then
            assertThat(criteria.salesChannelCategoryIds()).isEqualTo(salesChannelCategoryIds);
            assertThat(criteria.internalCategoryIds()).isEqualTo(internalCategoryIds);
            assertThat(criteria.salesChannelIds()).isEqualTo(salesChannelIds);
            assertThat(criteria.statuses()).isEqualTo(statuses);
            assertThat(criteria.searchField()).isEqualTo(searchField);
            assertThat(criteria.searchWord()).isEqualTo(searchWord);
            assertThat(criteria.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("null 파라미터로 검색 조건을 생성한다")
        void createWithNullParameters() {
            // given
            QueryContext<CategoryMappingSortKey> queryContext =
                    QueryContext.defaultOf(CategoryMappingSortKey.defaultKey());

            // when
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.of(
                            null, null, null, null, null, null, queryContext);

            // then
            assertThat(criteria.salesChannelCategoryIds()).isEmpty();
            assertThat(criteria.internalCategoryIds()).isEmpty();
            assertThat(criteria.salesChannelIds()).isEmpty();
            assertThat(criteria.statuses()).isEmpty();
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
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.salesChannelCategoryIds()).isEmpty();
            assertThat(criteria.internalCategoryIds()).isEmpty();
            assertThat(criteria.salesChannelIds()).isEmpty();
            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.queryContext()).isNotNull();
        }
    }

    @Nested
    @DisplayName("hasSalesChannelCategoryFilter() - 외부 카테고리 필터 존재 여부")
    class HasSalesChannelCategoryFilterTest {

        @Test
        @DisplayName("외부 카테고리 ID가 있으면 true를 반환한다")
        void returnTrueWhenSalesChannelCategoryIdsExist() {
            // given
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.of(
                            List.of(100L),
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CategoryMappingSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSalesChannelCategoryFilter()).isTrue();
        }

        @Test
        @DisplayName("외부 카테고리 ID가 비어있으면 false를 반환한다")
        void returnFalseWhenSalesChannelCategoryIdsEmpty() {
            // given
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasSalesChannelCategoryFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasInternalCategoryFilter() - 내부 카테고리 필터 존재 여부")
    class HasInternalCategoryFilterTest {

        @Test
        @DisplayName("내부 카테고리 ID가 있으면 true를 반환한다")
        void returnTrueWhenInternalCategoryIdsExist() {
            // given
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.of(
                            null,
                            List.of(10L),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CategoryMappingSortKey.defaultKey()));

            // then
            assertThat(criteria.hasInternalCategoryFilter()).isTrue();
        }

        @Test
        @DisplayName("내부 카테고리 ID가 비어있으면 false를 반환한다")
        void returnFalseWhenInternalCategoryIdsEmpty() {
            // given
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasInternalCategoryFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSalesChannelFilter() - 판매 채널 필터 존재 여부")
    class HasSalesChannelFilterTest {

        @Test
        @DisplayName("판매 채널 ID가 있으면 true를 반환한다")
        void returnTrueWhenSalesChannelIdsExist() {
            // given
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.of(
                            null,
                            null,
                            List.of(1L),
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CategoryMappingSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSalesChannelFilter()).isTrue();
        }

        @Test
        @DisplayName("판매 채널 ID가 비어있으면 false를 반환한다")
        void returnFalseWhenSalesChannelIdsEmpty() {
            // given
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.defaultCriteria();

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
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.of(
                            null,
                            null,
                            null,
                            List.of(CategoryMappingStatus.ACTIVE),
                            null,
                            null,
                            QueryContext.defaultOf(CategoryMappingSortKey.defaultKey()));

            // then
            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 비어있으면 false를 반환한다")
        void returnFalseWhenStatusesEmpty() {
            // given
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.defaultCriteria();

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
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            CategoryMappingSearchField.EXTERNAL_CATEGORY_NAME,
                            "검색어",
                            QueryContext.defaultOf(CategoryMappingSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("검색어가 null이면 false를 반환한다")
        void returnFalseWhenSearchWordIsNull() {
            // given
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("검색어가 빈 문자열이면 false를 반환한다")
        void returnFalseWhenSearchWordIsBlank() {
            // given
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            CategoryMappingSearchField.EXTERNAL_CATEGORY_NAME,
                            "   ",
                            QueryContext.defaultOf(CategoryMappingSortKey.defaultKey()));

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
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            CategoryMappingSearchField.EXTERNAL_CATEGORY_NAME,
                            null,
                            QueryContext.defaultOf(CategoryMappingSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchField()).isTrue();
        }

        @Test
        @DisplayName("검색 필드가 null이면 false를 반환한다")
        void returnFalseWhenSearchFieldIsNull() {
            // given
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.defaultCriteria();

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
            QueryContext<CategoryMappingSortKey> queryContext =
                    QueryContext.of(
                            CategoryMappingSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.of(
                            null, null, null, null, null, null, queryContext);

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("offset()은 QueryContext의 offset을 반환한다")
        void offsetReturnsQueryContextOffset() {
            // given
            QueryContext<CategoryMappingSortKey> queryContext =
                    QueryContext.of(
                            CategoryMappingSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(2, 10));
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.of(
                            null, null, null, null, null, null, queryContext);

            // then
            assertThat(criteria.offset()).isEqualTo(20L);
        }

        @Test
        @DisplayName("page()는 QueryContext의 page를 반환한다")
        void pageReturnsQueryContextPage() {
            // given
            QueryContext<CategoryMappingSortKey> queryContext =
                    QueryContext.of(
                            CategoryMappingSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(3, 10));
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.of(
                            null, null, null, null, null, null, queryContext);

            // then
            assertThat(criteria.page()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("리스트 파라미터는 불변 리스트로 저장된다")
        void listsAreImmutable() {
            // given
            List<Long> originalSalesChannelCategoryIds = List.of(100L, 200L);
            QueryContext<CategoryMappingSortKey> queryContext =
                    QueryContext.defaultOf(CategoryMappingSortKey.defaultKey());

            // when
            CategoryMappingSearchCriteria criteria =
                    CategoryMappingSearchCriteria.of(
                            originalSalesChannelCategoryIds,
                            null,
                            null,
                            null,
                            null,
                            null,
                            queryContext);

            // then
            assertThat(criteria.salesChannelCategoryIds()).isEqualTo(originalSalesChannelCategoryIds);
            assertThatThrownBy(() -> criteria.salesChannelCategoryIds().add(300L))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
