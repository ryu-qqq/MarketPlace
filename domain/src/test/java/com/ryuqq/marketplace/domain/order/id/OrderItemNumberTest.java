package com.ryuqq.marketplace.domain.order.id;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderItemNumber Value Object 테스트")
class OrderItemNumberTest {

    @Nested
    @DisplayName("of() - 번호 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 문자열로 OrderItemNumber를 생성한다")
        void createWithValidValue() {
            // given
            String value = "ORD-20260218-0001-001";

            // when
            OrderItemNumber number = OrderItemNumber.of(value);

            // then
            assertThat(number.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> OrderItemNumber.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> OrderItemNumber.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("generate() - OrderNumber 기반 생성")
    class GenerateTest {

        @Test
        @DisplayName("OrderNumber와 순번으로 OrderItemNumber를 생성한다")
        void generateFromOrderNumberAndSequence() {
            // given
            OrderNumber orderNumber = OrderNumber.of("ORD-20260218-0001");
            int sequence = 1;

            // when
            OrderItemNumber itemNumber = OrderItemNumber.generate(orderNumber, sequence);

            // then
            assertThat(itemNumber.value()).isEqualTo("ORD-20260218-0001-001");
        }

        @Test
        @DisplayName("순번이 두 자리여도 세 자리로 패딩된다")
        void generatePadsSequenceToThreeDigits() {
            // given
            OrderNumber orderNumber = OrderNumber.of("ORD-20260218-0001");

            // when
            OrderItemNumber itemNumber = OrderItemNumber.generate(orderNumber, 10);

            // then
            assertThat(itemNumber.value()).isEqualTo("ORD-20260218-0001-010");
        }

        @Test
        @DisplayName("순번이 한 자리여도 세 자리로 패딩된다")
        void generatePadsSingleDigitSequence() {
            // given
            OrderNumber orderNumber = OrderNumber.of("ORD-20260218-0001");

            // when
            OrderItemNumber itemNumber = OrderItemNumber.generate(orderNumber, 5);

            // then
            assertThat(itemNumber.value()).isEqualTo("ORD-20260218-0001-005");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값의 OrderItemNumber는 동일하다")
        void sameValueAreEqual() {
            // given
            String value = "ORD-20260218-0001-001";

            // when
            OrderItemNumber num1 = OrderItemNumber.of(value);
            OrderItemNumber num2 = OrderItemNumber.of(value);

            // then
            assertThat(num1).isEqualTo(num2);
            assertThat(num1.hashCode()).isEqualTo(num2.hashCode());
        }

        @Test
        @DisplayName("다른 값의 OrderItemNumber는 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            OrderItemNumber num1 = OrderItemNumber.of("ORD-20260218-0001-001");
            OrderItemNumber num2 = OrderItemNumber.of("ORD-20260218-0001-002");

            // then
            assertThat(num1).isNotEqualTo(num2);
        }
    }
}
