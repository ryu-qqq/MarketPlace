package com.ryuqq.marketplace.domain.inboundsource.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundSourceCode 단위 테스트")
class InboundSourceCodeTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 코드 문자열로 생성한다")
        void createWithValidValue() {
            InboundSourceCode code = InboundSourceCode.of("SETOF");

            assertThat(code.value()).isEqualTo("SETOF");
        }

        @Test
        @DisplayName("앞뒤 공백이 trim된다")
        void createWithTrimmedValue() {
            InboundSourceCode code = InboundSourceCode.of("  SETOF  ");

            assertThat(code.value()).isEqualTo("SETOF");
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> InboundSourceCode.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            assertThatThrownBy(() -> InboundSourceCode.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("100자를 초과하면 예외가 발생한다")
        void createWithTooLongValue_ThrowsException() {
            String tooLong = "A".repeat(101);

            assertThatThrownBy(() -> InboundSourceCode.of(tooLong))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100");
        }

        @Test
        @DisplayName("정확히 100자이면 생성 가능하다")
        void createWithExactMaxLength() {
            String maxLength = "A".repeat(100);
            InboundSourceCode code = InboundSourceCode.of(maxLength);

            assertThat(code.value()).hasSize(100);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("동일한 코드는 같다")
        void sameCodeAreEqual() {
            InboundSourceCode code1 = InboundSourceCode.of("SETOF");
            InboundSourceCode code2 = InboundSourceCode.of("SETOF");

            assertThat(code1).isEqualTo(code2);
            assertThat(code1.hashCode()).isEqualTo(code2.hashCode());
        }

        @Test
        @DisplayName("다른 코드는 같지 않다")
        void differentCodesAreNotEqual() {
            InboundSourceCode code1 = InboundSourceCode.of("SETOF");
            InboundSourceCode code2 = InboundSourceCode.of("MUSTIT");

            assertThat(code1).isNotEqualTo(code2);
        }
    }
}
