package com.ryuqq.marketplace.domain.commoncodetype.vo;

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
@DisplayName("CommonCodeType VO 테스트")
class CommonCodeTypeVoTest {

    @Nested
    @DisplayName("CommonCodeTypeCode 테스트")
    class CommonCodeTypeCodeTest {

        @Test
        @DisplayName("유효한 코드를 생성한다")
        void createValidCode() {
            var code = CommonCodeTypeCode.of("PAYMENT_METHOD");
            assertThat(code.value()).isEqualTo("PAYMENT_METHOD");
        }

        @Test
        @DisplayName("소문자는 대문자로 변환된다")
        void convertToUpperCase() {
            var code = CommonCodeTypeCode.of("payment_method");
            assertThat(code.value()).isEqualTo("PAYMENT_METHOD");
        }

        @Test
        @DisplayName("공백은 트림된다")
        void trimWhitespace() {
            var code = CommonCodeTypeCode.of("  PAYMENT_METHOD  ");
            assertThat(code.value()).isEqualTo("PAYMENT_METHOD");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null 또는 빈 값이면 예외가 발생한다")
        void throwExceptionForNullOrEmpty(String value) {
            assertThatThrownBy(() -> CommonCodeTypeCode.of(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @ParameterizedTest
        @ValueSource(strings = {"1PAYMENT", "_PAYMENT", "123", "PAYMENT-METHOD"})
        @DisplayName("영문 대문자로 시작하지 않거나 허용되지 않은 문자가 있으면 예외가 발생한다")
        void throwExceptionForInvalidFormat(String value) {
            assertThatThrownBy(() -> CommonCodeTypeCode.of(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 대문자");
        }

        @Test
        @DisplayName("50자 초과 시 예외가 발생한다")
        void throwExceptionForTooLong() {
            String longCode = "A" + "B".repeat(50);
            assertThatThrownBy(() -> CommonCodeTypeCode.of(longCode))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("동등한 코드는 같다")
        void equalityTest() {
            var code1 = CommonCodeTypeCode.of("PAYMENT_METHOD");
            var code2 = CommonCodeTypeCode.of("PAYMENT_METHOD");
            assertThat(code1).isEqualTo(code2);
        }
    }

    @Nested
    @DisplayName("CommonCodeTypeName 테스트")
    class CommonCodeTypeNameTest {

        @Test
        @DisplayName("유효한 이름을 생성한다")
        void createValidName() {
            var name = CommonCodeTypeName.of("결제수단");
            assertThat(name.value()).isEqualTo("결제수단");
        }

        @Test
        @DisplayName("공백은 트림된다")
        void trimWhitespace() {
            var name = CommonCodeTypeName.of("  결제수단  ");
            assertThat(name.value()).isEqualTo("결제수단");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null 또는 빈 값이면 예외가 발생한다")
        void throwExceptionForNullOrEmpty(String value) {
            assertThatThrownBy(() -> CommonCodeTypeName.of(value))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("동등한 이름은 같다")
        void equalityTest() {
            var name1 = CommonCodeTypeName.of("결제수단");
            var name2 = CommonCodeTypeName.of("결제수단");
            assertThat(name1).isEqualTo(name2);
        }
    }

    @Nested
    @DisplayName("CommonCodeTypeDescription 테스트")
    class CommonCodeTypeDescriptionTest {

        @Test
        @DisplayName("유효한 설명을 생성한다")
        void createValidDescription() {
            var desc = CommonCodeTypeDescription.of("결제수단 공통코드 타입");
            assertThat(desc.value()).isEqualTo("결제수단 공통코드 타입");
        }

        @Test
        @DisplayName("빈 설명을 생성한다")
        void createEmptyDescription() {
            var desc = CommonCodeTypeDescription.empty();
            assertThat(desc.value()).isNull();
        }

        @Test
        @DisplayName("null 값으로 빈 설명이 생성된다")
        void nullCreatesEmptyDescription() {
            var desc = CommonCodeTypeDescription.of(null);
            assertThat(desc.value()).isNull();
        }

        @Test
        @DisplayName("공백은 트림된다")
        void trimWhitespace() {
            var desc = CommonCodeTypeDescription.of("  설명입니다  ");
            assertThat(desc.value()).isEqualTo("설명입니다");
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
    }
}
