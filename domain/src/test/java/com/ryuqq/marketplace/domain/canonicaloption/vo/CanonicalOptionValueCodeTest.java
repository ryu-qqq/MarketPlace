package com.ryuqq.marketplace.domain.canonicaloption.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CanonicalOptionValueCode Value Object 단위 테스트")
class CanonicalOptionValueCodeTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 생성한다")
        void createWithValidValue() {
            // given & when
            CanonicalOptionValueCode code = CanonicalOptionValueCode.of("BLACK");

            // then
            assertThat(code.value()).isEqualTo("BLACK");
        }

        @Test
        @DisplayName("공백이 트림된다")
        void valueIsTrimmed() {
            // when
            CanonicalOptionValueCode code = CanonicalOptionValueCode.of("  WHITE  ");

            // then
            assertThat(code.value()).isEqualTo("WHITE");
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void nullThrowsException() {
            // when & then
            assertThatThrownBy(() -> CanonicalOptionValueCode.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void emptyStringThrowsException() {
            // when & then
            assertThatThrownBy(() -> CanonicalOptionValueCode.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("공백만 있으면 예외가 발생한다")
        void blankStringThrowsException() {
            // when & then
            assertThatThrownBy(() -> CanonicalOptionValueCode.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("50자를 초과하면 예외가 발생한다")
        void tooLongThrowsException() {
            // given
            String longCode = "a".repeat(51);

            // when & then
            assertThatThrownBy(() -> CanonicalOptionValueCode.of(longCode))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("50자");
        }

        @Test
        @DisplayName("50자는 허용된다")
        void maxLengthIsAllowed() {
            // given
            String maxCode = "a".repeat(50);

            // when
            CanonicalOptionValueCode code = CanonicalOptionValueCode.of(maxCode);

            // then
            assertThat(code.value()).hasSize(50);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            // given
            CanonicalOptionValueCode code1 = CanonicalOptionValueCode.of("BLACK");
            CanonicalOptionValueCode code2 = CanonicalOptionValueCode.of("BLACK");

            // then
            assertThat(code1).isEqualTo(code2);
            assertThat(code1.hashCode()).isEqualTo(code2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValueAreNotEqual() {
            // given
            CanonicalOptionValueCode code1 = CanonicalOptionValueCode.of("BLACK");
            CanonicalOptionValueCode code2 = CanonicalOptionValueCode.of("WHITE");

            // then
            assertThat(code1).isNotEqualTo(code2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("record 타입이므로 불변이다")
        void isImmutable() {
            // given
            CanonicalOptionValueCode code = CanonicalOptionValueCode.of("BLACK");

            // then
            assertThat(code.value()).isEqualTo("BLACK");
            // record는 final 클래스이므로 값 변경 불가능
        }
    }
}
