package com.ryuqq.marketplace.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QueryContext Value Object 단위 테스트")
class QueryContextTest {

    /** 테스트용 SortKey */
    enum TestSortKey implements SortKey {
        ID("id"),
        NAME("name");

        private final String fieldName;

        TestSortKey(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public String fieldName() {
            return fieldName;
        }
    }

    @Nested
    @DisplayName("생성 및 null 방어 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 파라미터로 QueryContext를 생성한다")
        void createWithValidParameters() {
            QueryContext<TestSortKey> context = QueryContext.of(
                    TestSortKey.ID, SortDirection.DESC, PageRequest.of(0, 20));

            assertThat(context.sortKey()).isEqualTo(TestSortKey.ID);
            assertThat(context.sortDirection()).isEqualTo(SortDirection.DESC);
            assertThat(context.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("sortKey가 null이면 예외가 발생한다")
        void nullSortKeyThrowsException() {
            assertThatThrownBy(() -> QueryContext.of(null, SortDirection.DESC, PageRequest.defaultPage()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("sortKey");
        }

        @Test
        @DisplayName("sortDirection이 null이면 기본값 DESC를 사용한다")
        void nullSortDirectionDefaultsToDesc() {
            QueryContext<TestSortKey> context = QueryContext.of(
                    TestSortKey.ID, null, PageRequest.defaultPage());

            assertThat(context.sortDirection()).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("pageRequest가 null이면 기본 PageRequest를 사용한다")
        void nullPageRequestDefaultsToDefaultPage() {
            QueryContext<TestSortKey> context = QueryContext.of(
                    TestSortKey.ID, SortDirection.DESC, null);

            assertThat(context.size()).isEqualTo(PageRequest.DEFAULT_SIZE);
        }

        @Test
        @DisplayName("defaultOf()는 기본 설정으로 QueryContext를 생성한다")
        void defaultOfCreatesDefaultContext() {
            QueryContext<TestSortKey> context = QueryContext.defaultOf(TestSortKey.ID);

            assertThat(context.sortKey()).isEqualTo(TestSortKey.ID);
            assertThat(context.sortDirection()).isEqualTo(SortDirection.DESC);
            assertThat(context.isFirstPage()).isTrue();
            assertThat(context.includeDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("페이지 이동 테스트")
    class NavigationTest {

        @Test
        @DisplayName("nextPage()는 다음 페이지 QueryContext를 반환한다")
        void nextPageReturnsNewContext() {
            QueryContext<TestSortKey> context = QueryContext.of(
                    TestSortKey.ID, SortDirection.DESC, PageRequest.of(0, 20));

            QueryContext<TestSortKey> next = context.nextPage();

            assertThat(next.page()).isEqualTo(1);
            assertThat(next.sortKey()).isEqualTo(TestSortKey.ID);
        }

        @Test
        @DisplayName("previousPage()는 이전 페이지 QueryContext를 반환한다")
        void previousPageReturnsNewContext() {
            QueryContext<TestSortKey> context = QueryContext.of(
                    TestSortKey.ID, SortDirection.DESC, PageRequest.of(2, 20));

            QueryContext<TestSortKey> prev = context.previousPage();

            assertThat(prev.page()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("변환 메서드 테스트")
    class TransformTest {

        @Test
        @DisplayName("reverseSortDirection()은 정렬 방향이 반전된다")
        void reverseSortDirectionReturnsReversed() {
            QueryContext<TestSortKey> context = QueryContext.of(
                    TestSortKey.ID, SortDirection.DESC, PageRequest.defaultPage());

            QueryContext<TestSortKey> reversed = context.reverseSortDirection();

            assertThat(reversed.sortDirection()).isEqualTo(SortDirection.ASC);
            assertThat(reversed.isAscending()).isTrue();
        }

        @Test
        @DisplayName("withSortKey()는 정렬 키가 변경된다")
        void withSortKeyChangesKey() {
            QueryContext<TestSortKey> context = QueryContext.defaultOf(TestSortKey.ID);

            QueryContext<TestSortKey> changed = context.withSortKey(TestSortKey.NAME);

            assertThat(changed.sortKey()).isEqualTo(TestSortKey.NAME);
        }

        @Test
        @DisplayName("withPageSize()는 페이지 크기가 변경된다")
        void withPageSizeChangesSize() {
            QueryContext<TestSortKey> context = QueryContext.defaultOf(TestSortKey.ID);

            QueryContext<TestSortKey> changed = context.withPageSize(50);

            assertThat(changed.size()).isEqualTo(50);
        }

        @Test
        @DisplayName("withIncludeDeleted()는 삭제 포함 여부가 변경된다")
        void withIncludeDeletedChangesFlag() {
            QueryContext<TestSortKey> context = QueryContext.defaultOf(TestSortKey.ID);

            QueryContext<TestSortKey> changed = context.withIncludeDeleted(true);

            assertThat(changed.includeDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("offset 및 페이지 정보 테스트")
    class OffsetTest {

        @Test
        @DisplayName("offset()은 page * size이다")
        void offsetIsPageTimesSize() {
            QueryContext<TestSortKey> context = QueryContext.of(
                    TestSortKey.ID, SortDirection.DESC, PageRequest.of(2, 20));

            assertThat(context.offset()).isEqualTo(40L);
        }

        @Test
        @DisplayName("isFirstPage()는 첫 페이지일 때 true이다")
        void isFirstPageReturnsTrueOnFirstPage() {
            QueryContext<TestSortKey> context = QueryContext.defaultOf(TestSortKey.ID);

            assertThat(context.isFirstPage()).isTrue();
        }
    }
}
