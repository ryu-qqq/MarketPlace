package com.ryuqq.marketplace.domain.shipment.id;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentNumber 테스트")
class ShipmentNumberTest {

    @Nested
    @DisplayName("of() - 배송 번호 생성")
    class OfTest {

        @Test
        @DisplayName("SHP-YYYYMMDD-XXXX 포맷으로 배송 번호를 생성한다")
        void createWithValidFormat() {
            // given
            String number = "SHP-20260218-0001";

            // when
            ShipmentNumber shipmentNumber = ShipmentNumber.of(number);

            // then
            assertThat(shipmentNumber.value()).isEqualTo(number);
        }

        @Test
        @DisplayName("다양한 SHP 포맷 번호를 생성할 수 있다")
        void createWithVariousValidFormats() {
            assertThatCode(() -> ShipmentNumber.of("SHP-20260218-0001")).doesNotThrowAnyException();
            assertThatCode(() -> ShipmentNumber.of("SHP-20260101-9999")).doesNotThrowAnyException();
            assertThatCode(() -> ShipmentNumber.of("SHP-20261231-0100")).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> ShipmentNumber.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithEmpty_ThrowsException() {
            assertThatThrownBy(() -> ShipmentNumber.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("공백 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            assertThatThrownBy(() -> ShipmentNumber.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 배송 번호이면 동일하다")
        void sameValueAreEqual() {
            // given
            ShipmentNumber number1 = ShipmentNumber.of("SHP-20260218-0001");
            ShipmentNumber number2 = ShipmentNumber.of("SHP-20260218-0001");

            // then
            assertThat(number1).isEqualTo(number2);
            assertThat(number1.hashCode()).isEqualTo(number2.hashCode());
        }

        @Test
        @DisplayName("다른 배송 번호이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            ShipmentNumber number1 = ShipmentNumber.of("SHP-20260218-0001");
            ShipmentNumber number2 = ShipmentNumber.of("SHP-20260218-0002");

            // then
            assertThat(number1).isNotEqualTo(number2);
        }

        @Test
        @DisplayName("날짜가 다른 배송 번호는 동일하지 않다")
        void differentDateIsNotEqual() {
            // given
            ShipmentNumber number1 = ShipmentNumber.of("SHP-20260218-0001");
            ShipmentNumber number2 = ShipmentNumber.of("SHP-20260219-0001");

            // then
            assertThat(number1).isNotEqualTo(number2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("ShipmentNumber는 record이므로 불변이다")
        void shipmentNumberIsImmutable() {
            // given
            String numberValue = "SHP-20260218-0001";
            ShipmentNumber number = ShipmentNumber.of(numberValue);

            // then
            assertThat(number.value()).isEqualTo(numberValue);
        }
    }
}
