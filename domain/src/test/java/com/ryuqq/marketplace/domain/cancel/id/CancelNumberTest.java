package com.ryuqq.marketplace.domain.cancel.id;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelNumber 테스트")
class CancelNumberTest {

    @Nested
    @DisplayName("of() - 취소 번호 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 값으로 취소 번호를 생성한다")
        void createWithValidValue() {
            // when
            CancelNumber number = CancelNumber.of("CAN-20240101-0001");

            // then
            assertThat(number.value()).isEqualTo("CAN-20240101-0001");
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> CancelNumber.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> CancelNumber.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("공백 문자열이면 예외가 발생한다")
        void createWithWhitespace_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> CancelNumber.of("  "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("generate() - 취소 번호 자동 생성")
    class GenerateTest {

        @Test
        @DisplayName("생성된 취소 번호는 CAN- 으로 시작한다")
        void generatedNumberStartsWithCan() {
            // when
            CancelNumber number = CancelNumber.generate();

            // then
            assertThat(number.value()).startsWith("CAN-");
        }

        @Test
        @DisplayName("생성된 취소 번호는 CAN-YYYYMMDD-XXXX 포맷이다")
        void generatedNumberMatchesFormat() {
            // when
            CancelNumber number = CancelNumber.generate();

            // then
            assertThat(number.value()).matches("CAN-\\d{8}-\\d{4}");
        }

        @Test
        @DisplayName("생성된 취소 번호는 총 17자이다")
        void generatedNumberHasCorrectLength() {
            // CAN-YYYYMMDD-XXXX = 3 + 1 + 8 + 1 + 4 = 17
            // when
            CancelNumber number = CancelNumber.generate();

            // then
            assertThat(number.value()).hasSize(17);
        }

        @Test
        @DisplayName("두 번 생성한 번호는 값이 존재한다")
        void generateReturnsNonNullValue() {
            // when
            CancelNumber number1 = CancelNumber.generate();
            CancelNumber number2 = CancelNumber.generate();

            // then
            assertThat(number1.value()).isNotBlank();
            assertThat(number2.value()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            // given
            CancelNumber number1 = CancelNumber.of("CAN-20240101-0001");
            CancelNumber number2 = CancelNumber.of("CAN-20240101-0001");

            // then
            assertThat(number1).isEqualTo(number2);
            assertThat(number1.hashCode()).isEqualTo(number2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            CancelNumber number1 = CancelNumber.of("CAN-20240101-0001");
            CancelNumber number2 = CancelNumber.of("CAN-20240101-0002");

            // then
            assertThat(number1).isNotEqualTo(number2);
        }
    }
}
