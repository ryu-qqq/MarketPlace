package com.ryuqq.marketplace.domain.commoncode.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("unit")
@DisplayName("CommonCode VO 테스트")
class CommonCodeVoTest {

    @Nested
    @DisplayName("CommonCodeValue 테스트")
    class CommonCodeValueTest {

        @Test
        @DisplayName("유효한 코드값을 생성한다")
        void createValidCodeValue() {
            var codeValue = CommonCodeValue.of("CREDIT_CARD");
            assertThat(codeValue.value()).isEqualTo("CREDIT_CARD");
        }

        @Test
        @DisplayName("소문자는 대문자로 변환된다")
        void convertToUpperCase() {
            var codeValue = CommonCodeValue.of("credit_card");
            assertThat(codeValue.value()).isEqualTo("CREDIT_CARD");
        }

        @Test
        @DisplayName("공백은 트림된다")
        void trimWhitespace() {
            var codeValue = CommonCodeValue.of("  CREDIT_CARD  ");
            assertThat(codeValue.value()).isEqualTo("CREDIT_CARD");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null 또는 빈 값이면 예외가 발생한다")
        void throwExceptionForNullOrEmpty(String value) {
            assertThatThrownBy(() -> CommonCodeValue.of(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @ParameterizedTest
        @ValueSource(strings = {"1CREDIT", "_CREDIT", "123", "CREDIT-CARD"})
        @DisplayName("영문 대문자로 시작하지 않거나 허용되지 않은 문자가 있으면 예외가 발생한다")
        void throwExceptionForInvalidFormat(String value) {
            assertThatThrownBy(() -> CommonCodeValue.of(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 대문자");
        }

        @Test
        @DisplayName("50자 초과 시 예외가 발생한다")
        void throwExceptionForTooLong() {
            String longCode = "A" + "B".repeat(50);
            assertThatThrownBy(() -> CommonCodeValue.of(longCode))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("50");
        }

        @Test
        @DisplayName("동등한 코드값은 같다")
        void equalityTest() {
            var code1 = CommonCodeValue.of("CREDIT_CARD");
            var code2 = CommonCodeValue.of("CREDIT_CARD");
            assertThat(code1).isEqualTo(code2);
        }
    }

    @Nested
    @DisplayName("CommonCodeDisplayName 테스트")
    class CommonCodeDisplayNameTest {

        @Test
        @DisplayName("유효한 표시명을 생성한다")
        void createValidDisplayName() {
            var displayName = CommonCodeDisplayName.of("신용카드");
            assertThat(displayName.value()).isEqualTo("신용카드");
        }

        @Test
        @DisplayName("공백은 트림된다")
        void trimWhitespace() {
            var displayName = CommonCodeDisplayName.of("  신용카드  ");
            assertThat(displayName.value()).isEqualTo("신용카드");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null 또는 빈 값이면 예외가 발생한다")
        void throwExceptionForNullOrEmpty(String value) {
            assertThatThrownBy(() -> CommonCodeDisplayName.of(value))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("DisplayOrder 테스트")
    class DisplayOrderTest {

        @Test
        @DisplayName("유효한 표시 순서를 생성한다")
        void createValidDisplayOrder() {
            var order = DisplayOrder.of(1);
            assertThat(order.value()).isEqualTo(1);
        }

        @Test
        @DisplayName("0은 유효한 값이다")
        void zeroIsValid() {
            var order = DisplayOrder.of(0);
            assertThat(order.value()).isZero();
        }

        @Test
        @DisplayName("기본 순서는 0이다")
        void defaultOrderIsZero() {
            var order = DisplayOrder.defaultOrder();
            assertThat(order.value()).isZero();
        }

        @Test
        @DisplayName("음수 값이면 예외가 발생한다")
        void throwExceptionForNegative() {
            assertThatThrownBy(() -> DisplayOrder.of(-1))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("동등한 순서는 같다")
        void equalityTest() {
            var order1 = DisplayOrder.of(5);
            var order2 = DisplayOrder.of(5);
            assertThat(order1).isEqualTo(order2);
        }
    }
}
