package com.ryuqq.marketplace.domain.commoncodetype.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeTypeSearchCriteria 테스트")
class CommonCodeTypeSearchCriteriaTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("of()로 검색 조건을 생성한다")
        void createWithOf() {
            // given
            QueryContext<CommonCodeTypeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeTypeSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when
            CommonCodeTypeSearchCriteria criteria =
                    CommonCodeTypeSearchCriteria.of(
                            true, CommonCodeTypeSearchField.CODE, "결제", null, queryContext);

            // then
            assertThat(criteria.active()).isTrue();
            assertThat(criteria.searchField()).isEqualTo(CommonCodeTypeSearchField.CODE);
            assertThat(criteria.searchWord()).isEqualTo("결제");
            assertThat(criteria.type()).isNull();
            assertThat(criteria.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("defaultCriteria()로 기본 검색 조건을 생성한다")
        void createDefaultCriteria() {
            // when
            CommonCodeTypeSearchCriteria criteria = CommonCodeTypeSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.active()).isNull();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.type()).isNull();
            assertThat(criteria.queryContext().sortKey())
                    .isEqualTo(CommonCodeTypeSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("activeOnly()로 활성화된 항목만 조회하는 조건을 생성한다")
        void createActiveOnly() {
            // when
            CommonCodeTypeSearchCriteria criteria = CommonCodeTypeSearchCriteria.activeOnly();

            // then
            assertThat(criteria.active()).isTrue();
            assertThat(criteria.searchWord()).isNull();
        }
    }

    @Nested
    @DisplayName("편의 메서드 테스트")
    class ConvenienceMethodTest {

        @Test
        @DisplayName("hasSearchWord()는 검색어가 있으면 true를 반환한다")
        void hasSearchWordReturnsTrueWhenSearchWordExists() {
            // given
            CommonCodeTypeSearchCriteria criteria =
                    CommonCodeTypeSearchCriteria.of(
                            null,
                            null,
                            "결제",
                            null,
                            QueryContext.defaultOf(CommonCodeTypeSortKey.CREATED_AT));

            // then
            assertThat(criteria.hasSearchWord()).isTrue();
        }

        @Test
        @DisplayName("hasSearchWord()는 검색어가 없으면 false를 반환한다")
        void hasSearchWordReturnsFalseWhenNoSearchWord() {
            // given
            CommonCodeTypeSearchCriteria criteria = CommonCodeTypeSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasSearchWord()).isFalse();
        }

        @Test
        @DisplayName("hasSearchWord()는 빈 검색어에서 false를 반환한다")
        void hasSearchWordReturnsFalseForBlankSearchWord() {
            // given
            CommonCodeTypeSearchCriteria criteria =
                    CommonCodeTypeSearchCriteria.of(
                            null,
                            null,
                            "   ",
                            null,
                            QueryContext.defaultOf(CommonCodeTypeSortKey.CREATED_AT));

            // then
            assertThat(criteria.hasSearchWord()).isFalse();
        }

        @Test
        @DisplayName("hasType()는 type이 있으면 true를 반환한다")
        void hasTypeReturnsTrueWhenTypeExists() {
            // given
            CommonCodeTypeSearchCriteria criteria =
                    CommonCodeTypeSearchCriteria.of(
                            null,
                            null,
                            null,
                            "CARD",
                            QueryContext.defaultOf(CommonCodeTypeSortKey.CREATED_AT));

            // then
            assertThat(criteria.hasType()).isTrue();
        }

        @Test
        @DisplayName("hasType()는 type이 없으면 false를 반환한다")
        void hasTypeReturnsFalseWhenNoType() {
            // given
            CommonCodeTypeSearchCriteria criteria = CommonCodeTypeSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasType()).isFalse();
        }

        @Test
        @DisplayName("hasActiveFilter()는 활성화 필터가 있으면 true를 반환한다")
        void hasActiveFilterReturnsTrueWhenActiveExists() {
            // given
            CommonCodeTypeSearchCriteria criteria = CommonCodeTypeSearchCriteria.activeOnly();

            // then
            assertThat(criteria.hasActiveFilter()).isTrue();
        }

        @Test
        @DisplayName("hasActiveFilter()는 활성화 필터가 없으면 false를 반환한다")
        void hasActiveFilterReturnsFalseWhenNoActive() {
            // given
            CommonCodeTypeSearchCriteria criteria = CommonCodeTypeSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.hasActiveFilter()).isFalse();
        }

        @Test
        @DisplayName("size()는 페이지 크기를 반환한다")
        void returnsSize() {
            // given
            CommonCodeTypeSearchCriteria criteria = CommonCodeTypeSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.size()).isEqualTo(PageRequest.DEFAULT_SIZE);
        }

        @Test
        @DisplayName("offset()은 오프셋을 반환한다")
        void returnsOffset() {
            // given
            QueryContext<CommonCodeTypeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeTypeSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(2, 20));
            CommonCodeTypeSearchCriteria criteria =
                    CommonCodeTypeSearchCriteria.of(null, null, null, null, queryContext);

            // then
            assertThat(criteria.offset()).isEqualTo(40);
        }

        @Test
        @DisplayName("page()는 현재 페이지를 반환한다")
        void returnsPage() {
            // given
            QueryContext<CommonCodeTypeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeTypeSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(3, 20));
            CommonCodeTypeSearchCriteria criteria =
                    CommonCodeTypeSearchCriteria.of(null, null, null, null, queryContext);

            // then
            assertThat(criteria.page()).isEqualTo(3);
        }
    }
}
