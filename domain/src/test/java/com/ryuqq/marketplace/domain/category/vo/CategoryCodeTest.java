package com.ryuqq.marketplace.domain.category.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryCode Value Object 단위 테스트")
class CategoryCodeTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 코드로 생성한다")
        void createWithValidCode() {
            CategoryCode code = CategoryCode.of("FASHION");

            assertThat(code.value()).isEqualTo("FASHION");
        }

        @Test
        @DisplayName("앞뒤 공백은 trim된다")
        void createWithWhitespaceTrimmed() {
            CategoryCode code = CategoryCode.of("  FASHION  ");

            assertThat(code.value()).isEqualTo("FASHION");
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> CategoryCode.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            assertThatThrownBy(() -> CategoryCode.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("공백 문자열이면 예외가 발생한다")
        void createWithWhitespace_ThrowsException() {
            assertThatThrownBy(() -> CategoryCode.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("100자 초과이면 예외가 발생한다")
        void createWithTooLong_ThrowsException() {
            String longCode = "A".repeat(101);
            assertThatThrownBy(() -> CategoryCode.of(longCode))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100자");
        }

        @Test
        @DisplayName("정확히 100자인 코드로 생성한다")
        void createWith100CharCode() {
            String code100 = "A".repeat(100);
            CategoryCode code = CategoryCode.of(code100);

            assertThat(code.value()).isEqualTo(code100);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            CategoryCode code1 = CategoryCode.of("FASHION");
            CategoryCode code2 = CategoryCode.of("FASHION");

            assertThat(code1).isEqualTo(code2);
            assertThat(code1.hashCode()).isEqualTo(code2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            CategoryCode code1 = CategoryCode.of("FASHION");
            CategoryCode code2 = CategoryCode.of("SHOES");

            assertThat(code1).isNotEqualTo(code2);
        }
    }
}
