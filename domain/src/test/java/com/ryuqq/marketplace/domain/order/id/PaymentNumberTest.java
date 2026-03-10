package com.ryuqq.marketplace.domain.order.id;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("PaymentNumber Value Object 테스트")
class PaymentNumberTest {

    @Nested
    @DisplayName("of() - 기존 결제번호 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 문자열로 PaymentNumber를 생성한다")
        void createWithValidValue() {
            // given
            String value = "PAY-20260218-0001";

            // when
            PaymentNumber paymentNumber = PaymentNumber.of(value);

            // then
            assertThat(paymentNumber.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> PaymentNumber.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> PaymentNumber.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("공백 문자열이면 예외가 발생한다")
        void createWithWhitespace_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> PaymentNumber.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("generate() - 결제번호 자동 생성")
    class GenerateTest {

        @Test
        @DisplayName("generate()는 null이 아닌 PaymentNumber를 생성한다")
        void generateCreatesNonNullPaymentNumber() {
            // when
            PaymentNumber paymentNumber = PaymentNumber.generate();

            // then
            assertThat(paymentNumber).isNotNull();
            assertThat(paymentNumber.value()).isNotBlank();
        }

        @Test
        @DisplayName("generate()는 PAY- 접두사로 시작하는 결제번호를 생성한다")
        void generateCreatesPaymentNumberWithPrefix() {
            // when
            PaymentNumber paymentNumber = PaymentNumber.generate();

            // then
            assertThat(paymentNumber.value()).startsWith("PAY-");
        }

        @Test
        @DisplayName("generate()를 두 번 호출하면 PAY-YYYYMMDD-XXXX 포맷이다")
        void generateCreatesBothMatchingFormat() {
            // when
            PaymentNumber first = PaymentNumber.generate();
            PaymentNumber second = PaymentNumber.generate();

            // then
            assertThat(first.value()).matches("PAY-\\d{8}-\\d{4}");
            assertThat(second.value()).matches("PAY-\\d{8}-\\d{4}");
        }

        @Test
        @DisplayName("생성된 결제번호는 총 17자이다")
        void generatedNumberHasCorrectLength() {
            // PAY-YYYYMMDD-XXXX = 3 + 1 + 8 + 1 + 4 = 17
            // when
            PaymentNumber paymentNumber = PaymentNumber.generate();

            // then
            assertThat(paymentNumber.value()).hasSize(17);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값의 PaymentNumber는 동일하다")
        void sameValueAreEqual() {
            // given
            String value = "PAY-20260218-0001";

            // when
            PaymentNumber pn1 = PaymentNumber.of(value);
            PaymentNumber pn2 = PaymentNumber.of(value);

            // then
            assertThat(pn1).isEqualTo(pn2);
            assertThat(pn1.hashCode()).isEqualTo(pn2.hashCode());
        }

        @Test
        @DisplayName("다른 값의 PaymentNumber는 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            PaymentNumber pn1 = PaymentNumber.of("PAY-20260218-0001");
            PaymentNumber pn2 = PaymentNumber.of("PAY-20260218-0002");

            // then
            assertThat(pn1).isNotEqualTo(pn2);
        }
    }
}
