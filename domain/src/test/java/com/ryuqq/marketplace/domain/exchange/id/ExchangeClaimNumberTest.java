package com.ryuqq.marketplace.domain.exchange.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeClaimNumber Value Object 단위 테스트")
class ExchangeClaimNumberTest {

    private static final String VALID_NUMBER = "EXC-20260218-0001";

    @Nested
    @DisplayName("of() - 생성 테스트")
    class OfTest {

        @Test
        @DisplayName("유효한 클레임 번호로 생성한다")
        void createWithValidNumber() {
            // when
            ExchangeClaimNumber number = ExchangeClaimNumber.of(VALID_NUMBER);

            // then
            assertThat(number).isNotNull();
            assertThat(number.value()).isEqualTo(VALID_NUMBER);
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void createWithNullValue_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> ExchangeClaimNumber.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ExchangeClaimNumber 값은 null 또는 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열로 생성하면 예외가 발생한다")
        void createWithBlankValue_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> ExchangeClaimNumber.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ExchangeClaimNumber 값은 null 또는 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("공백만 있는 문자열로 생성하면 예외가 발생한다")
        void createWithWhitespaceValue_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> ExchangeClaimNumber.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ExchangeClaimNumber 값은 null 또는 빈 문자열일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("generate() - 자동 생성 테스트")
    class GenerateTest {

        @Test
        @DisplayName("EXC-YYYYMMDD-XXXX 포맷으로 생성된다")
        void generatedNumberMatchesFormat() {
            // when
            ExchangeClaimNumber number = ExchangeClaimNumber.generate();

            // then
            assertThat(number.value()).isNotBlank();
            assertThat(number.value()).matches("EXC-\\d{8}-\\d{4}");
        }

        @Test
        @DisplayName("EXC 접두사로 시작한다")
        void generatedNumberStartsWithPrefix() {
            // when
            ExchangeClaimNumber number = ExchangeClaimNumber.generate();

            // then
            assertThat(number.value()).startsWith("EXC-");
        }

        @Test
        @DisplayName("생성할 때마다 고유한 번호가 생성될 수 있다")
        void generateProducesValidNumber() {
            // when
            ExchangeClaimNumber number1 = ExchangeClaimNumber.generate();
            ExchangeClaimNumber number2 = ExchangeClaimNumber.generate();

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
            ExchangeClaimNumber number1 = ExchangeClaimNumber.of(VALID_NUMBER);
            ExchangeClaimNumber number2 = ExchangeClaimNumber.of(VALID_NUMBER);

            // then
            assertThat(number1).isEqualTo(number2);
            assertThat(number1.hashCode()).isEqualTo(number2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            ExchangeClaimNumber number1 = ExchangeClaimNumber.of("EXC-20260218-0001");
            ExchangeClaimNumber number2 = ExchangeClaimNumber.of("EXC-20260218-0002");

            // then
            assertThat(number1).isNotEqualTo(number2);
        }
    }
}
