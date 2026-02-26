package com.ryuqq.marketplace.domain.imagevariant.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageDimension VO 테스트")
class ImageDimensionTest {

    @Nested
    @DisplayName("of() - 생성")
    class OfTest {

        @Test
        @DisplayName("너비와 높이로 생성한다")
        void createWithValues() {
            ImageDimension dimension = ImageDimension.of(300, 300);
            assertThat(dimension.width()).isEqualTo(300);
            assertThat(dimension.height()).isEqualTo(300);
        }

        @Test
        @DisplayName("null 값으로 생성할 수 있다")
        void createWithNullValues() {
            ImageDimension dimension = ImageDimension.of(null, null);
            assertThat(dimension.width()).isNull();
            assertThat(dimension.height()).isNull();
        }
    }

    @Nested
    @DisplayName("hasValues() - 값 존재 여부")
    class HasValuesTest {

        @Test
        @DisplayName("너비와 높이가 모두 있으면 true를 반환한다")
        void hasValues_WhenBothPresent() {
            assertThat(ImageDimension.of(300, 300).hasValues()).isTrue();
        }

        @Test
        @DisplayName("너비가 null이면 false를 반환한다")
        void hasValues_WhenWidthNull() {
            assertThat(ImageDimension.of(null, 300).hasValues()).isFalse();
        }

        @Test
        @DisplayName("높이가 null이면 false를 반환한다")
        void hasValues_WhenHeightNull() {
            assertThat(ImageDimension.of(300, null).hasValues()).isFalse();
        }

        @Test
        @DisplayName("모두 null이면 false를 반환한다")
        void hasValues_WhenBothNull() {
            assertThat(ImageDimension.of(null, null).hasValues()).isFalse();
        }
    }
}
