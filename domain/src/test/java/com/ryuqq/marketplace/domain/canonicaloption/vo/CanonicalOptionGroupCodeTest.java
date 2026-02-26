package com.ryuqq.marketplace.domain.canonicaloption.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CanonicalOptionGroupCode Value Object 단위 테스트")
class CanonicalOptionGroupCodeTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 생성한다")
        void createWithValidValue() {
            // given & when
            CanonicalOptionGroupCode code = CanonicalOptionGroupCode.of("COLOR");

            // then
            assertThat(code.value()).isEqualTo("COLOR");
        }

        @Test
        @DisplayName("공백이 트림된다")
        void valueIsTrimmed() {
            // when
            CanonicalOptionGroupCode code = CanonicalOptionGroupCode.of("  SIZE  ");

            // then
            assertThat(code.value()).isEqualTo("SIZE");
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void nullThrowsException() {
            // when & then
            assertThatThrownBy(() -> CanonicalOptionGroupCode.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void emptyStringThrowsException() {
            // when & then
            assertThatThrownBy(() -> CanonicalOptionGroupCode.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("공백만 있으면 예외가 발생한다")
        void blankStringThrowsException() {
            // when & then
            assertThatThrownBy(() -> CanonicalOptionGroupCode.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("50자를 초과하면 예외가 발생한다")
        void tooLongThrowsException() {
            // given
            String longCode = "a".repeat(51);

            // when & then
            assertThatThrownBy(() -> CanonicalOptionGroupCode.of(longCode))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("50자");
        }

        @Test
        @DisplayName("50자는 허용된다")
        void maxLengthIsAllowed() {
            // given
            String maxCode = "a".repeat(50);

            // when
            CanonicalOptionGroupCode code = CanonicalOptionGroupCode.of(maxCode);

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
            CanonicalOptionGroupCode code1 = CanonicalOptionGroupCode.of("COLOR");
            CanonicalOptionGroupCode code2 = CanonicalOptionGroupCode.of("COLOR");

            // then
            assertThat(code1).isEqualTo(code2);
            assertThat(code1.hashCode()).isEqualTo(code2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValueAreNotEqual() {
            // given
            CanonicalOptionGroupCode code1 = CanonicalOptionGroupCode.of("COLOR");
            CanonicalOptionGroupCode code2 = CanonicalOptionGroupCode.of("SIZE");

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
            CanonicalOptionGroupCode code = CanonicalOptionGroupCode.of("COLOR");

            // then
            assertThat(code.value()).isEqualTo("COLOR");
            // record는 final 클래스이므로 값 변경 불가능
        }
    }
}
