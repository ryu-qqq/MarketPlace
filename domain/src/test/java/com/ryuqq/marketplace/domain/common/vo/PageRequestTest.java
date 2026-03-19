package com.ryuqq.marketplace.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("PageRequest Value Object 단위 테스트")
class PageRequestTest {

    @Nested
    @DisplayName("생성 및 정규화 테스트")
    class CreationTest {

        @Test
        @DisplayName("page=0, size=20으로 PageRequest를 생성한다")
        void createWithValidValues() {
            PageRequest request = PageRequest.of(0, 20);

            assertThat(request.page()).isEqualTo(0);
            assertThat(request.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("음수 page는 0으로 정규화된다")
        void negativePageIsNormalized() {
            PageRequest request = PageRequest.of(-1, 20);

            assertThat(request.page()).isEqualTo(0);
        }

        @Test
        @DisplayName("0 이하 size는 DEFAULT_SIZE로 정규화된다")
        void zeroSizeIsNormalized() {
            PageRequest request = PageRequest.of(0, 0);

            assertThat(request.size()).isEqualTo(PageRequest.DEFAULT_SIZE);
        }

        @Test
        @DisplayName("MAX_SIZE를 초과하는 size는 MAX_SIZE로 정규화된다")
        void oversizedSizeIsNormalized() {
            PageRequest request = PageRequest.of(0, 200);

            assertThat(request.size()).isEqualTo(PageRequest.MAX_SIZE);
        }

        @Test
        @DisplayName("first()는 첫 페이지 요청을 반환한다")
        void firstReturnsFirstPage() {
            PageRequest request = PageRequest.first(30);

            assertThat(request.page()).isEqualTo(0);
            assertThat(request.size()).isEqualTo(30);
        }

        @Test
        @DisplayName("defaultPage()는 page=0, size=DEFAULT_SIZE를 반환한다")
        void defaultPageReturnsDefaultValues() {
            PageRequest request = PageRequest.defaultPage();

            assertThat(request.page()).isEqualTo(0);
            assertThat(request.size()).isEqualTo(PageRequest.DEFAULT_SIZE);
        }
    }

    @Nested
    @DisplayName("페이지 이동 테스트")
    class NavigationTest {

        @Test
        @DisplayName("next()는 다음 페이지를 반환한다")
        void nextReturnsNextPage() {
            PageRequest request = PageRequest.of(2, 20);

            PageRequest next = request.next();

            assertThat(next.page()).isEqualTo(3);
            assertThat(next.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("previous()는 이전 페이지를 반환한다")
        void previousReturnsPreviousPage() {
            PageRequest request = PageRequest.of(2, 20);

            PageRequest prev = request.previous();

            assertThat(prev.page()).isEqualTo(1);
        }

        @Test
        @DisplayName("첫 페이지에서 previous()는 그대로 반환한다")
        void previousOnFirstPageReturnsSelf() {
            PageRequest request = PageRequest.of(0, 20);

            PageRequest prev = request.previous();

            assertThat(prev.page()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("offset 및 페이지 계산 테스트")
    class CalculationTest {

        @Test
        @DisplayName("offset은 page * size이다")
        void offsetIsCalculatedCorrectly() {
            PageRequest request = PageRequest.of(2, 20);

            assertThat(request.offset()).isEqualTo(40L);
        }

        @Test
        @DisplayName("isFirst()는 첫 페이지일 때 true이다")
        void isFirstReturnsTrueOnFirstPage() {
            PageRequest request = PageRequest.of(0, 20);

            assertThat(request.isFirst()).isTrue();
        }

        @Test
        @DisplayName("isFirst()는 첫 페이지가 아닐 때 false이다")
        void isFirstReturnsFalseOnNonFirstPage() {
            PageRequest request = PageRequest.of(1, 20);

            assertThat(request.isFirst()).isFalse();
        }

        @Test
        @DisplayName("totalPages는 전체 요소 수를 size로 나눈 올림이다")
        void totalPagesIsCalculatedCorrectly() {
            PageRequest request = PageRequest.of(0, 20);

            assertThat(request.totalPages(41L)).isEqualTo(3);
            assertThat(request.totalPages(40L)).isEqualTo(2);
        }

        @Test
        @DisplayName("isLast()는 마지막 페이지일 때 true이다")
        void isLastReturnsTrueOnLastPage() {
            PageRequest request = PageRequest.of(1, 20);

            assertThat(request.isLast(40L)).isTrue();
        }
    }
}
