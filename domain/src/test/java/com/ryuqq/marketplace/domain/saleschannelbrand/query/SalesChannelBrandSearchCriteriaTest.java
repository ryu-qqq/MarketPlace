package com.ryuqq.marketplace.domain.saleschannelbrand.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.saleschannelbrand.vo.SalesChannelBrandStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelBrandSearchCriteria 단위 테스트")
class SalesChannelBrandSearchCriteriaTest {

    private QueryContext<SalesChannelBrandSortKey> defaultQueryContext() {
        return QueryContext.defaultOf(SalesChannelBrandSortKey.defaultKey());
    }

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("모든 필드로 SalesChannelBrandSearchCriteria를 생성한다")
        void createWithAllFields() {
            // given
            List<Long> salesChannelIds = List.of(1L, 2L);
            List<SalesChannelBrandStatus> statuses = List.of(SalesChannelBrandStatus.ACTIVE);
            SalesChannelBrandSearchField searchField = SalesChannelBrandSearchField.EXTERNAL_NAME;
            String searchWord = "테스트";
            QueryContext<SalesChannelBrandSortKey> queryContext = defaultQueryContext();

            // when
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            salesChannelIds, statuses, searchField, searchWord, queryContext);

            // then
            assertThat(criteria.salesChannelIds()).containsExactly(1L, 2L);
            assertThat(criteria.statuses()).containsExactly(SalesChannelBrandStatus.ACTIVE);
            assertThat(criteria.searchField())
                    .isEqualTo(SalesChannelBrandSearchField.EXTERNAL_NAME);
            assertThat(criteria.searchWord()).isEqualTo("테스트");
            assertThat(criteria.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("null 컬렉션은 빈 리스트로 변환된다")
        void createWithNullCollections() {
            // when
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            null, null, null, null, defaultQueryContext());

            // then
            assertThat(criteria.salesChannelIds()).isEmpty();
            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
        }

        @Test
        @DisplayName("queryContext가 null이면 예외가 발생한다")
        void createWithNullQueryContext_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    SalesChannelBrandSearchCriteria.of(
                                            List.of(), List.of(), null, null, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("queryContext");
        }
    }

    @Nested
    @DisplayName("필터 존재 여부 테스트")
    class FilterExistenceTest {

        @Test
        @DisplayName("salesChannelIds가 있으면 hasSalesChannelFilter()는 true를 반환한다")
        void hasSalesChannelFilterReturnsTrueWhenIdsExist() {
            // given
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            List.of(1L), List.of(), null, null, defaultQueryContext());

            // then
            assertThat(criteria.hasSalesChannelFilter()).isTrue();
        }

        @Test
        @DisplayName("salesChannelIds가 없으면 hasSalesChannelFilter()는 false를 반환한다")
        void hasSalesChannelFilterReturnsFalseWhenIdsEmpty() {
            // given
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            List.of(), List.of(), null, null, defaultQueryContext());

            // then
            assertThat(criteria.hasSalesChannelFilter()).isFalse();
        }

        @Test
        @DisplayName("statuses가 있으면 hasStatusFilter()는 true를 반환한다")
        void hasStatusFilterReturnsTrueWhenStatusesExist() {
            // given
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            List.of(),
                            List.of(SalesChannelBrandStatus.ACTIVE),
                            null,
                            null,
                            defaultQueryContext());

            // then
            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("statuses가 없으면 hasStatusFilter()는 false를 반환한다")
        void hasStatusFilterReturnsFalseWhenStatusesEmpty() {
            // given
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            List.of(), List.of(), null, null, defaultQueryContext());

            // then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("searchWord가 있으면 hasSearchCondition()은 true를 반환한다")
        void hasSearchConditionReturnsTrueWhenSearchWordExists() {
            // given
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            List.of(), List.of(), null, "테스트", defaultQueryContext());

            // then
            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("searchWord가 null이면 hasSearchCondition()은 false를 반환한다")
        void hasSearchConditionReturnsFalseWhenSearchWordNull() {
            // given
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            List.of(), List.of(), null, null, defaultQueryContext());

            // then
            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("searchWord가 빈 문자열이면 hasSearchCondition()은 false를 반환한다")
        void hasSearchConditionReturnsFalseWhenSearchWordBlank() {
            // given
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            List.of(), List.of(), null, "   ", defaultQueryContext());

            // then
            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("searchField가 있으면 hasSearchField()는 true를 반환한다")
        void hasSearchFieldReturnsTrueWhenSearchFieldExists() {
            // given
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            List.of(),
                            List.of(),
                            SalesChannelBrandSearchField.EXTERNAL_NAME,
                            null,
                            defaultQueryContext());

            // then
            assertThat(criteria.hasSearchField()).isTrue();
        }

        @Test
        @DisplayName("searchField가 null이면 hasSearchField()는 false를 반환한다")
        void hasSearchFieldReturnsFalseWhenSearchFieldNull() {
            // given
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            List.of(), List.of(), null, null, defaultQueryContext());

            // then
            assertThat(criteria.hasSearchField()).isFalse();
        }
    }

    @Nested
    @DisplayName("QueryContext 위임 테스트")
    class QueryContextDelegationTest {

        @Test
        @DisplayName("size()는 queryContext의 size를 반환한다")
        void sizeReturnsQueryContextSize() {
            // given
            QueryContext<SalesChannelBrandSortKey> queryContext =
                    QueryContext.of(
                            SalesChannelBrandSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            List.of(), List.of(), null, null, queryContext);

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("offset()은 queryContext의 offset을 반환한다")
        void offsetReturnsQueryContextOffset() {
            // given
            QueryContext<SalesChannelBrandSortKey> queryContext =
                    QueryContext.of(
                            SalesChannelBrandSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(1, 10));
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            List.of(), List.of(), null, null, queryContext);

            // then
            assertThat(criteria.offset()).isEqualTo(10L);
        }

        @Test
        @DisplayName("page()는 queryContext의 page를 반환한다")
        void pageReturnsQueryContextPage() {
            // given
            QueryContext<SalesChannelBrandSortKey> queryContext =
                    QueryContext.of(
                            SalesChannelBrandSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(3, 10));
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            List.of(), List.of(), null, null, queryContext);

            // then
            assertThat(criteria.page()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("생성 후 salesChannelIds를 수정해도 원본은 변하지 않는다")
        void salesChannelIdsIsImmutable() {
            // given
            List<Long> originalIds = List.of(1L, 2L);
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            originalIds, List.of(), null, null, defaultQueryContext());

            // when & then
            assertThatThrownBy(() -> criteria.salesChannelIds().add(3L))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("생성 후 statuses를 수정해도 원본은 변하지 않는다")
        void statusesIsImmutable() {
            // given
            List<SalesChannelBrandStatus> originalStatuses =
                    List.of(SalesChannelBrandStatus.ACTIVE);
            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            List.of(), originalStatuses, null, null, defaultQueryContext());

            // when & then
            assertThatThrownBy(() -> criteria.statuses().add(SalesChannelBrandStatus.INACTIVE))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
