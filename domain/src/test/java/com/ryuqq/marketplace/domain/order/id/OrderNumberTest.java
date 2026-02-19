package com.ryuqq.marketplace.domain.order.id;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderNumber Value Object 테스트")
class OrderNumberTest {

    @Nested
    @DisplayName("of() - 기존 주문번호 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 문자열로 OrderNumber를 생성한다")
        void createWithValidValue() {
            // given
            String value = "ORD-20260218-0001";

            // when
            OrderNumber orderNumber = OrderNumber.of(value);

            // then
            assertThat(orderNumber.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> OrderNumber.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> OrderNumber.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("generate() - 주문번호 자동 생성")
    class GenerateTest {

        @Test
        @DisplayName("generate()는 null이 아닌 OrderNumber를 생성한다")
        void generateCreatesNonNullOrderNumber() {
            // when
            OrderNumber orderNumber = OrderNumber.generate();

            // then
            assertThat(orderNumber).isNotNull();
            assertThat(orderNumber.value()).isNotBlank();
        }

        @Test
        @DisplayName("generate()는 ORD- 접두사로 시작하는 주문번호를 생성한다")
        void generateCreatesOrderNumberWithPrefix() {
            // when
            OrderNumber orderNumber = OrderNumber.generate();

            // then
            assertThat(orderNumber.value()).startsWith("ORD-");
        }

        @Test
        @DisplayName("generate()를 두 번 호출하면 다른 값이 생성될 수 있다")
        void generateCreatesDifferentValuesOnMultipleCalls() {
            // when
            OrderNumber first = OrderNumber.generate();
            OrderNumber second = OrderNumber.generate();

            // then - 같을 수도 있지만 형식은 동일해야 한다
            assertThat(first.value()).matches("ORD-\\d{8}-\\d{4}");
            assertThat(second.value()).matches("ORD-\\d{8}-\\d{4}");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값의 OrderNumber는 동일하다")
        void sameValueAreEqual() {
            // given
            String value = "ORD-20260218-0001";

            // when
            OrderNumber on1 = OrderNumber.of(value);
            OrderNumber on2 = OrderNumber.of(value);

            // then
            assertThat(on1).isEqualTo(on2);
            assertThat(on1.hashCode()).isEqualTo(on2.hashCode());
        }

        @Test
        @DisplayName("다른 값의 OrderNumber는 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            OrderNumber on1 = OrderNumber.of("ORD-20260218-0001");
            OrderNumber on2 = OrderNumber.of("ORD-20260218-0002");

            // then
            assertThat(on1).isNotEqualTo(on2);
        }
    }
}
