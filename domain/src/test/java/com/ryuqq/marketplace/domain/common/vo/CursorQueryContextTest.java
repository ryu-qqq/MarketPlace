package com.ryuqq.marketplace.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CursorQueryContext Value Object 단위 테스트")
class CursorQueryContextTest {

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
        @DisplayName("유효한 파라미터로 CursorQueryContext를 생성한다")
        void createWithValidParameters() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.of(
                            TestSortKey.ID, SortDirection.DESC, CursorPageRequest.first(20));

            assertThat(context.sortKey()).isEqualTo(TestSortKey.ID);
            assertThat(context.sortDirection()).isEqualTo(SortDirection.DESC);
            assertThat(context.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("sortKey가 null이면 예외가 발생한다")
        void nullSortKeyThrowsException() {
            assertThatThrownBy(
                            () ->
                                    CursorQueryContext.of(
                                            null,
                                            SortDirection.DESC,
                                            CursorPageRequest.defaultPage()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("sortKey");
        }

        @Test
        @DisplayName("sortDirection이 null이면 기본값 DESC를 사용한다")
        void nullSortDirectionDefaultsToDesc() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.of(TestSortKey.ID, null, CursorPageRequest.defaultPage());

            assertThat(context.sortDirection()).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("cursorPageRequest가 null이면 기본 CursorPageRequest를 사용한다")
        void nullCursorPageRequestDefaultsToDefaultPage() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.of(TestSortKey.ID, SortDirection.DESC, null);

            assertThat(context.isFirstPage()).isTrue();
        }

        @Test
        @DisplayName("defaultOf()는 기본 설정으로 CursorQueryContext를 생성한다")
        void defaultOfCreatesDefaultContext() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.defaultOf(TestSortKey.ID);

            assertThat(context.sortKey()).isEqualTo(TestSortKey.ID);
            assertThat(context.sortDirection()).isEqualTo(SortDirection.DESC);
            assertThat(context.isFirstPage()).isTrue();
            assertThat(context.includeDeleted()).isFalse();
        }

        @Test
        @DisplayName("firstPage()는 커서 없이 첫 페이지 컨텍스트를 생성한다")
        void firstPageCreatesFirstPageContext() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.firstPage(TestSortKey.ID, SortDirection.ASC, 30);

            assertThat(context.size()).isEqualTo(30);
            assertThat(context.isFirstPage()).isTrue();
            assertThat(context.sortDirection()).isEqualTo(SortDirection.ASC);
        }

        @Test
        @DisplayName("includeDeleted를 포함한 of()로 생성한다")
        void createWithIncludeDeleted() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.of(
                            TestSortKey.ID, SortDirection.DESC, CursorPageRequest.first(20), true);

            assertThat(context.includeDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("커서 이동 테스트")
    class CursorNavigationTest {

        @Test
        @DisplayName("nextPage()는 다음 커서 값을 가진 CursorQueryContext를 반환한다")
        void nextPageReturnsNewContextWithCursor() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.defaultOf(TestSortKey.ID);

            CursorQueryContext<TestSortKey, Long> next = context.nextPage(100L);

            assertThat(next.cursor()).isEqualTo(100L);
            assertThat(next.sortKey()).isEqualTo(TestSortKey.ID);
            assertThat(next.isFirstPage()).isFalse();
        }

        @Test
        @DisplayName("hasCursor()는 커서가 없으면 false이다")
        void hasCursorFalseWhenNoCursor() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.defaultOf(TestSortKey.ID);

            assertThat(context.hasCursor()).isFalse();
        }

        @Test
        @DisplayName("hasCursor()는 커서가 있으면 true이다")
        void hasCursorTrueWhenCursorPresent() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.defaultOf(TestSortKey.ID);
            CursorQueryContext<TestSortKey, Long> next = context.nextPage(50L);

            assertThat(next.hasCursor()).isTrue();
            assertThat(next.cursor()).isEqualTo(50L);
        }
    }

    @Nested
    @DisplayName("변환 메서드 테스트")
    class TransformTest {

        @Test
        @DisplayName("reverseSortDirection()은 정렬 방향이 반전된다")
        void reverseSortDirectionReturnsReversed() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.of(
                            TestSortKey.ID, SortDirection.DESC, CursorPageRequest.defaultPage());

            CursorQueryContext<TestSortKey, Long> reversed = context.reverseSortDirection();

            assertThat(reversed.sortDirection()).isEqualTo(SortDirection.ASC);
            assertThat(reversed.isAscending()).isTrue();
        }

        @Test
        @DisplayName("withSortKey()는 정렬 키가 변경된다")
        void withSortKeyChangesKey() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.defaultOf(TestSortKey.ID);

            CursorQueryContext<TestSortKey, Long> changed = context.withSortKey(TestSortKey.NAME);

            assertThat(changed.sortKey()).isEqualTo(TestSortKey.NAME);
        }

        @Test
        @DisplayName("withPageSize()는 페이지 크기가 변경된다")
        void withPageSizeChangesSize() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.defaultOf(TestSortKey.ID);

            CursorQueryContext<TestSortKey, Long> changed = context.withPageSize(50);

            assertThat(changed.size()).isEqualTo(50);
        }

        @Test
        @DisplayName("withIncludeDeleted()는 삭제 포함 여부가 변경된다")
        void withIncludeDeletedChangesFlag() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.defaultOf(TestSortKey.ID);

            CursorQueryContext<TestSortKey, Long> changed = context.withIncludeDeleted(true);

            assertThat(changed.includeDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("fetchSize 및 페이지 정보 테스트")
    class FetchSizeTest {

        @Test
        @DisplayName("fetchSize()는 size + 1이다")
        void fetchSizeIsSizePlusOne() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.firstPage(TestSortKey.ID, SortDirection.DESC, 20);

            assertThat(context.fetchSize()).isEqualTo(21);
        }

        @Test
        @DisplayName("isFirstPage()는 커서가 없을 때 true이다")
        void isFirstPageReturnsTrueWhenNoCursor() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.defaultOf(TestSortKey.ID);

            assertThat(context.isFirstPage()).isTrue();
        }

        @Test
        @DisplayName("isAscending()은 ASC 방향일 때 true이다")
        void isAscendingReturnsTrueForAsc() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.firstPage(TestSortKey.ID, SortDirection.ASC, 20);

            assertThat(context.isAscending()).isTrue();
        }

        @Test
        @DisplayName("isAscending()은 DESC 방향일 때 false이다")
        void isAscendingReturnsFalseForDesc() {
            CursorQueryContext<TestSortKey, Long> context =
                    CursorQueryContext.defaultOf(TestSortKey.ID);

            assertThat(context.isAscending()).isFalse();
        }
    }
}
