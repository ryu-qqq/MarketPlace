package com.ryuqq.marketplace.domain.categorypreset.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryPresetSearchCriteria 테스트")
class CategoryPresetSearchCriteriaTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("모든 파라미터로 검색 조건을 생성한다")
        void createWithAllParameters() {
            List<Long> salesChannelIds = List.of(1L, 2L);
            List<String> statuses = List.of("ACTIVE");
            QueryContext<CategoryPresetSortKey> queryContext =
                    QueryContext.defaultOf(CategoryPresetSortKey.defaultKey());

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            salesChannelIds,
                            statuses,
                            "presetName",
                            "테스트",
                            LocalDate.now(),
                            LocalDate.now(),
                            queryContext);

            assertThat(criteria.salesChannelIds()).containsExactly(1L, 2L);
            assertThat(criteria.statuses()).containsExactly("ACTIVE");
            assertThat(criteria.searchField()).isEqualTo("presetName");
            assertThat(criteria.searchWord()).isEqualTo("테스트");
        }

        @Test
        @DisplayName("null 리스트는 빈 리스트로 변환된다")
        void nullListsConvertedToEmpty() {
            QueryContext<CategoryPresetSortKey> queryContext =
                    QueryContext.defaultOf(CategoryPresetSortKey.defaultKey());

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, null, null, null, null, null, queryContext);

            assertThat(criteria.salesChannelIds()).isEmpty();
            assertThat(criteria.statuses()).isEmpty();
        }

        @Test
        @DisplayName("queryContext가 null이면 예외가 발생한다")
        void nullQueryContextThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new CategoryPresetSearchCriteria(
                                            null, null, null, null, null, null, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("필터 존재 여부 테스트")
    class FilterCheckTest {

        @Test
        @DisplayName("판매채널 필터가 있으면 true를 반환한다")
        void hasSalesChannelFilter() {
            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            List.of(1L),
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CategoryPresetSortKey.defaultKey()));

            assertThat(criteria.hasSalesChannelFilter()).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 있으면 true를 반환한다")
        void hasStatusFilter() {
            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null,
                            List.of("ACTIVE"),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CategoryPresetSortKey.defaultKey()));

            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("검색 필터가 있으면 true를 반환한다")
        void hasSearchFilter() {
            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null,
                            null,
                            "presetName",
                            "테스트",
                            null,
                            null,
                            QueryContext.defaultOf(CategoryPresetSortKey.defaultKey()));

            assertThat(criteria.hasSearchFilter()).isTrue();
        }

        @Test
        @DisplayName("시작일/종료일 필터를 확인한다")
        void hasDateFilters() {
            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null,
                            null,
                            null,
                            null,
                            LocalDate.now(),
                            LocalDate.now(),
                            QueryContext.defaultOf(CategoryPresetSortKey.defaultKey()));

            assertThat(criteria.hasStartDateFilter()).isTrue();
            assertThat(criteria.hasEndDateFilter()).isTrue();
        }
    }

    @Nested
    @DisplayName("편의 메서드 테스트")
    class ConvenienceMethodsTest {

        @Test
        @DisplayName("size()는 QueryContext의 size를 반환한다")
        void sizeReturnsQueryContextSize() {
            QueryContext<CategoryPresetSortKey> queryContext =
                    QueryContext.of(
                            CategoryPresetSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));
            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, null, null, null, null, null, queryContext);

            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("offset()은 QueryContext의 offset을 반환한다")
        void offsetReturnsQueryContextOffset() {
            QueryContext<CategoryPresetSortKey> queryContext =
                    QueryContext.of(
                            CategoryPresetSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(2, 10));
            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, null, null, null, null, null, queryContext);

            assertThat(criteria.offset()).isEqualTo(20L);
        }
    }
}
