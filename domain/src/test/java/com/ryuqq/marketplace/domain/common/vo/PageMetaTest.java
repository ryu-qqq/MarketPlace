package com.ryuqq.marketplace.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("PageMeta Value Object 단위 테스트")
class PageMetaTest {

    @Nested
    @DisplayName("of() 팩토리 테스트")
    class FactoryTest {

        @Test
        @DisplayName("totalElements 기반으로 totalPages를 자동 계산한다")
        void totalPagesIsCalculatedAutomatically() {
            PageMeta meta = PageMeta.of(0, 20, 41L);

            assertThat(meta.totalPages()).isEqualTo(3);
        }

        @Test
        @DisplayName("totalElements가 size의 배수이면 totalPages는 나누기 결과이다")
        void totalPagesWhenExactlyDivisible() {
            PageMeta meta = PageMeta.of(0, 20, 40L);

            assertThat(meta.totalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("totalElements가 0이면 totalPages는 0이다")
        void totalPagesIsZeroWhenEmpty() {
            PageMeta meta = PageMeta.of(0, 20, 0L);

            assertThat(meta.totalPages()).isEqualTo(0);
        }

        @Test
        @DisplayName("empty()는 빈 PageMeta를 반환한다")
        void emptyReturnsEmptyMeta() {
            PageMeta meta = PageMeta.empty(20);

            assertThat(meta.totalElements()).isEqualTo(0);
            assertThat(meta.totalPages()).isEqualTo(0);
            assertThat(meta.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("size가 0 이하이면 DEFAULT_SIZE로 정규화된다")
        void invalidSizeIsNormalized() {
            PageMeta meta = PageMeta.of(0, 0, 100L);

            assertThat(meta.size()).isEqualTo(PageMeta.DEFAULT_SIZE);
        }
    }

    @Nested
    @DisplayName("페이지 상태 확인 테스트")
    class StatusTest {

        @Test
        @DisplayName("hasNext()는 다음 페이지가 있을 때 true이다")
        void hasNextReturnsTrueWhenMorePages() {
            PageMeta meta = PageMeta.of(0, 20, 100L);

            assertThat(meta.hasNext()).isTrue();
        }

        @Test
        @DisplayName("hasNext()는 마지막 페이지이면 false이다")
        void hasNextReturnsFalseOnLastPage() {
            PageMeta meta = PageMeta.of(4, 20, 100L);

            assertThat(meta.hasNext()).isFalse();
        }

        @Test
        @DisplayName("hasPrevious()는 첫 페이지에서 false이다")
        void hasPreviousReturnsFalseOnFirstPage() {
            PageMeta meta = PageMeta.of(0, 20, 100L);

            assertThat(meta.hasPrevious()).isFalse();
        }

        @Test
        @DisplayName("hasPrevious()는 두 번째 페이지부터 true이다")
        void hasPreviousReturnsTrueFromSecondPage() {
            PageMeta meta = PageMeta.of(1, 20, 100L);

            assertThat(meta.hasPrevious()).isTrue();
        }

        @Test
        @DisplayName("isFirst()는 첫 페이지에서 true이다")
        void isFirstReturnsTrueOnFirstPage() {
            PageMeta meta = PageMeta.of(0, 20, 100L);

            assertThat(meta.isFirst()).isTrue();
        }

        @Test
        @DisplayName("isLast()는 마지막 페이지에서 true이다")
        void isLastReturnsTrueOnLastPage() {
            PageMeta meta = PageMeta.of(4, 20, 100L);

            assertThat(meta.isLast()).isTrue();
        }
    }

    @Nested
    @DisplayName("offset 및 요소 번호 계산 테스트")
    class CalculationTest {

        @Test
        @DisplayName("offset은 page * size이다")
        void offsetIsCalculatedCorrectly() {
            PageMeta meta = PageMeta.of(2, 20, 100L);

            assertThat(meta.offset()).isEqualTo(40L);
        }

        @Test
        @DisplayName("startElement는 현재 페이지 첫 번째 요소 번호이다")
        void startElementIsCorrect() {
            PageMeta meta = PageMeta.of(1, 20, 100L);

            assertThat(meta.startElement()).isEqualTo(21L);
        }

        @Test
        @DisplayName("endElement는 현재 페이지 마지막 요소 번호이다")
        void endElementIsCorrect() {
            PageMeta meta = PageMeta.of(0, 20, 100L);

            assertThat(meta.endElement()).isEqualTo(20L);
        }

        @Test
        @DisplayName("마지막 페이지의 endElement는 totalElements이다")
        void endElementOnLastPageIsTotal() {
            PageMeta meta = PageMeta.of(4, 20, 95L);

            assertThat(meta.endElement()).isEqualTo(95L);
        }

        @Test
        @DisplayName("비어있으면 startElement와 endElement는 0이다")
        void emptyPageHasZeroElements() {
            PageMeta meta = PageMeta.empty(20);

            assertThat(meta.startElement()).isEqualTo(0L);
            assertThat(meta.endElement()).isEqualTo(0L);
        }
    }
}
