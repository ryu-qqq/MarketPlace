package com.ryuqq.marketplace.domain.notice.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NoticeCategoryCode Value Object 테스트")
class NoticeCategoryCodeTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 생성한다")
        void createWithValidValue() {
            // given & when
            NoticeCategoryCode code = NoticeCategoryCode.of("CLOTHING");

            // then
            assertThat(code.value()).isEqualTo("CLOTHING");
        }

        @Test
        @DisplayName("공백이 트림된다")
        void valueIsTrimmed() {
            // given & when
            NoticeCategoryCode code = NoticeCategoryCode.of("  CLOTHING  ");

            // then
            assertThat(code.value()).isEqualTo("CLOTHING");
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void createWithNullThrowsException() {
            // when & then
            assertThatThrownBy(() -> NoticeCategoryCode.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithEmptyStringThrowsException() {
            // when & then
            assertThatThrownBy(() -> NoticeCategoryCode.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("공백만 있으면 예외가 발생한다")
        void createWithBlankStringThrowsException() {
            // when & then
            assertThatThrownBy(() -> NoticeCategoryCode.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("50자를 초과하면 예외가 발생한다")
        void createWithTooLongValueThrowsException() {
            // given
            String longCode = "A".repeat(51);

            // when & then
            assertThatThrownBy(() -> NoticeCategoryCode.of(longCode))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("50자");
        }

        @Test
        @DisplayName("50자 이하는 허용된다")
        void createWith50CharsIsAllowed() {
            // given
            String code = "A".repeat(50);

            // when
            NoticeCategoryCode result = NoticeCategoryCode.of(code);

            // then
            assertThat(result.value()).hasSize(50);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 코드는 동등하다")
        void sameValueEquals() {
            // given
            NoticeCategoryCode code1 = NoticeCategoryCode.of("FOOD");
            NoticeCategoryCode code2 = NoticeCategoryCode.of("FOOD");

            // then
            assertThat(code1).isEqualTo(code2);
            assertThat(code1.hashCode()).isEqualTo(code2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 코드는 동등하지 않다")
        void differentValueNotEquals() {
            // given
            NoticeCategoryCode code1 = NoticeCategoryCode.of("FOOD");
            NoticeCategoryCode code2 = NoticeCategoryCode.of("CLOTHING");

            // then
            assertThat(code1).isNotEqualTo(code2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("NoticeCategoryCode는 불변 객체다")
        void isImmutable() {
            // given
            NoticeCategoryCode code = NoticeCategoryCode.of("ELECTRONICS");

            // when
            String originalValue = code.value();

            // then - record는 불변이므로 값 변경 불가
            assertThat(code.value()).isEqualTo(originalValue);
        }
    }
}
