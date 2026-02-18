package com.ryuqq.marketplace.domain.refund.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundClaimNumber Value Object 단위 테스트")
class RefundClaimNumberTest {

    @Nested
    @DisplayName("of() - 직접 생성 테스트")
    class OfTest {

        @Test
        @DisplayName("유효한 문자열로 클레임 번호를 생성한다")
        void createWithValidValue() {
            // when
            RefundClaimNumber number = RefundClaimNumber.of("RFD-20260218-0001");

            // then
            assertThat(number.value()).isEqualTo("RFD-20260218-0001");
        }

        @Test
        @DisplayName("value가 null이면 예외가 발생한다")
        void createWithNullValue_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> RefundClaimNumber.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("value가 빈 문자열이면 예외가 발생한다")
        void createWithBlankValue_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> RefundClaimNumber.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("generate() - 자동 생성 테스트")
    class GenerateTest {

        @Test
        @DisplayName("generate()로 클레임 번호를 생성한다")
        void generateClaimNumber() {
            // when
            RefundClaimNumber number = RefundClaimNumber.generate();

            // then
            assertThat(number).isNotNull();
            assertThat(number.value()).isNotBlank();
        }

        @Test
        @DisplayName("생성된 번호는 RFD- 접두사로 시작한다")
        void generatedNumberStartsWithPrefix() {
            // when
            RefundClaimNumber number = RefundClaimNumber.generate();

            // then
            assertThat(number.value()).startsWith("RFD-");
        }

        @Test
        @DisplayName("연속 생성된 번호들이 예외 없이 생성된다")
        void multipleGenerationsDoNotThrow() {
            // when & then
            assertThatCode(
                            () -> {
                                for (int i = 0; i < 5; i++) {
                                    RefundClaimNumber.generate();
                                }
                            })
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            // given
            RefundClaimNumber number1 = RefundClaimNumber.of("RFD-20260218-0001");
            RefundClaimNumber number2 = RefundClaimNumber.of("RFD-20260218-0001");

            // then
            assertThat(number1).isEqualTo(number2);
            assertThat(number1.hashCode()).isEqualTo(number2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            RefundClaimNumber number1 = RefundClaimNumber.of("RFD-20260218-0001");
            RefundClaimNumber number2 = RefundClaimNumber.of("RFD-20260218-0002");

            // then
            assertThat(number1).isNotEqualTo(number2);
        }
    }
}
