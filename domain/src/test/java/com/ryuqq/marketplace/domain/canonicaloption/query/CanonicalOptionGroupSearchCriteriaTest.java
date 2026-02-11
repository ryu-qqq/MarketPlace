package com.ryuqq.marketplace.domain.canonicaloption.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CanonicalOptionGroupSearchCriteria 단위 테스트")
class CanonicalOptionGroupSearchCriteriaTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("모든 파라미터로 검색 조건을 생성한다")
        void createWithAllParameters() {
            // given
            Boolean active = true;
            String searchField = "code";
            String searchWord = "COLOR";
            QueryContext<CanonicalOptionGroupSortKey> queryContext =
                    QueryContext.of(
                            CanonicalOptionGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            active, searchField, searchWord, queryContext);

            // then
            assertThat(criteria.active()).isTrue();
            assertThat(criteria.searchField()).isEqualTo("code");
            assertThat(criteria.searchWord()).isEqualTo("COLOR");
            assertThat(criteria.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("null 파라미터로 검색 조건을 생성한다")
        void createWithNullParameters() {
            // given
            QueryContext<CanonicalOptionGroupSortKey> queryContext =
                    QueryContext.defaultOf(CanonicalOptionGroupSortKey.defaultKey());

            // when
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(null, null, null, queryContext);

            // then
            assertThat(criteria.active()).isNull();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
        }

        @Test
        @DisplayName("queryContext가 null이면 예외가 발생한다")
        void nullQueryContextThrowsException() {
            // when & then
            assertThatThrownBy(
                            () -> new CanonicalOptionGroupSearchCriteria(null, null, null, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("hasActiveFilter() - 활성화 필터 존재 여부")
    class HasActiveFilterTest {

        @Test
        @DisplayName("활성화 필터가 true이면 true를 반환한다")
        void returnTrueWhenActiveFilterIsTrue() {
            // given
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            true,
                            null,
                            null,
                            QueryContext.defaultOf(CanonicalOptionGroupSortKey.defaultKey()));

            // then
            assertThat(criteria.hasActiveFilter()).isTrue();
        }

        @Test
        @DisplayName("활성화 필터가 false이면 true를 반환한다")
        void returnTrueWhenActiveFilterIsFalse() {
            // given
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            false,
                            null,
                            null,
                            QueryContext.defaultOf(CanonicalOptionGroupSortKey.defaultKey()));

            // then
            assertThat(criteria.hasActiveFilter()).isTrue();
        }

        @Test
        @DisplayName("활성화 필터가 null이면 false를 반환한다")
        void returnFalseWhenActiveFilterIsNull() {
            // given
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CanonicalOptionGroupSortKey.defaultKey()));

            // then
            assertThat(criteria.hasActiveFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchFilter() - 검색 필터 존재 여부")
    class HasSearchFilterTest {

        @Test
        @DisplayName("검색 필드와 검색어가 모두 있으면 true를 반환한다")
        void returnTrueWhenBothFieldAndWordExist() {
            // given
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            null,
                            "code",
                            "COLOR",
                            QueryContext.defaultOf(CanonicalOptionGroupSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchFilter()).isTrue();
        }

        @Test
        @DisplayName("검색 필드가 null이면 false를 반환한다")
        void returnFalseWhenSearchFieldIsNull() {
            // given
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            null,
                            null,
                            "COLOR",
                            QueryContext.defaultOf(CanonicalOptionGroupSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchFilter()).isFalse();
        }

        @Test
        @DisplayName("검색어가 null이면 false를 반환한다")
        void returnFalseWhenSearchWordIsNull() {
            // given
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            null,
                            "code",
                            null,
                            QueryContext.defaultOf(CanonicalOptionGroupSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchFilter()).isFalse();
        }

        @Test
        @DisplayName("검색 필드가 빈 문자열이면 false를 반환한다")
        void returnFalseWhenSearchFieldIsBlank() {
            // given
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            null,
                            "   ",
                            "COLOR",
                            QueryContext.defaultOf(CanonicalOptionGroupSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchFilter()).isFalse();
        }

        @Test
        @DisplayName("검색어가 빈 문자열이면 false를 반환한다")
        void returnFalseWhenSearchWordIsBlank() {
            // given
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            null,
                            "code",
                            "   ",
                            QueryContext.defaultOf(CanonicalOptionGroupSortKey.defaultKey()));

            // then
            assertThat(criteria.hasSearchFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("편의 메서드 테스트")
    class ConvenienceMethodsTest {

        @Test
        @DisplayName("size()는 QueryContext의 size를 반환한다")
        void sizeReturnsQueryContextSize() {
            // given
            QueryContext<CanonicalOptionGroupSortKey> queryContext =
                    QueryContext.of(
                            CanonicalOptionGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(null, null, null, queryContext);

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("offset()은 QueryContext의 offset을 반환한다")
        void offsetReturnsQueryContextOffset() {
            // given
            QueryContext<CanonicalOptionGroupSortKey> queryContext =
                    QueryContext.of(
                            CanonicalOptionGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(2, 10));
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(null, null, null, queryContext);

            // then
            assertThat(criteria.offset()).isEqualTo(20L);
        }

        @Test
        @DisplayName("page()는 QueryContext의 page를 반환한다")
        void pageReturnsQueryContextPage() {
            // given
            QueryContext<CanonicalOptionGroupSortKey> queryContext =
                    QueryContext.of(
                            CanonicalOptionGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(3, 10));
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(null, null, null, queryContext);

            // then
            assertThat(criteria.page()).isEqualTo(3);
        }
    }
}
