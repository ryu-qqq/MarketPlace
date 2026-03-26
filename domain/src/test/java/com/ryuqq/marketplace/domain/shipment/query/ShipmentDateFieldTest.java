package com.ryuqq.marketplace.domain.shipment.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentDateField 단위 테스트")
class ShipmentDateFieldTest {

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("ShipmentDateField는 3가지 값이다")
        void dateFieldValues() {
            assertThat(ShipmentDateField.values())
                    .containsExactlyInAnyOrder(
                            ShipmentDateField.PAYMENT,
                            ShipmentDateField.ORDER_CONFIRMED,
                            ShipmentDateField.SHIPPED);
        }

        @Test
        @DisplayName("각 필드의 name()이 올바르다")
        void fieldNames() {
            assertThat(ShipmentDateField.PAYMENT.name()).isEqualTo("PAYMENT");
            assertThat(ShipmentDateField.ORDER_CONFIRMED.name()).isEqualTo("ORDER_CONFIRMED");
            assertThat(ShipmentDateField.SHIPPED.name()).isEqualTo("SHIPPED");
        }
    }

    @Nested
    @DisplayName("valueOf() 테스트")
    class ValueOfTest {

        @Test
        @DisplayName("PAYMENT를 문자열로 조회한다")
        void valueOfPayment() {
            assertThat(ShipmentDateField.valueOf("PAYMENT")).isEqualTo(ShipmentDateField.PAYMENT);
        }

        @Test
        @DisplayName("ORDER_CONFIRMED를 문자열로 조회한다")
        void valueOfOrderConfirmed() {
            assertThat(ShipmentDateField.valueOf("ORDER_CONFIRMED"))
                    .isEqualTo(ShipmentDateField.ORDER_CONFIRMED);
        }

        @Test
        @DisplayName("SHIPPED를 문자열로 조회한다")
        void valueOfShipped() {
            assertThat(ShipmentDateField.valueOf("SHIPPED")).isEqualTo(ShipmentDateField.SHIPPED);
        }
    }
}
