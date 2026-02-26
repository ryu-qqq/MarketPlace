package com.ryuqq.marketplace.domain.shipment.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentMethod Value Object 테스트")
class ShipmentMethodTest {

    @Nested
    @DisplayName("of() - 생성 테스트")
    class OfTest {

        @Test
        @DisplayName("유효한 값으로 ShipmentMethod를 생성한다")
        void createWithValidValues() {
            // when
            ShipmentMethod method = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");

            // then
            assertThat(method.type()).isEqualTo(ShipmentMethodType.COURIER);
            assertThat(method.courierCode()).isEqualTo("CJ");
            assertThat(method.courierName()).isEqualTo("CJ대한통운");
        }

        @Test
        @DisplayName("type이 null이면 예외가 발생한다")
        void createWithNullType_ThrowsException() {
            assertThatThrownBy(() -> ShipmentMethod.of(null, "CJ", "CJ대한통운"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("courierCode와 courierName이 null이어도 생성된다")
        void createWithNullCourierInfo() {
            // when
            ShipmentMethod method = ShipmentMethod.of(ShipmentMethodType.VISIT, null, null);

            // then
            assertThat(method.type()).isEqualTo(ShipmentMethodType.VISIT);
            assertThat(method.courierCode()).isNull();
            assertThat(method.courierName()).isNull();
        }

        @Test
        @DisplayName("QUICK 타입으로 ShipmentMethod를 생성한다")
        void createQuickShipmentMethod() {
            // when
            ShipmentMethod method = ShipmentMethod.of(ShipmentMethodType.QUICK, null, "퀵배송");

            // then
            assertThat(method.type()).isEqualTo(ShipmentMethodType.QUICK);
            assertThat(method.courierCode()).isNull();
            assertThat(method.courierName()).isEqualTo("퀵배송");
        }

        @Test
        @DisplayName("DESIGNATED_COURIER 타입으로 ShipmentMethod를 생성한다")
        void createDesignatedCourierShipmentMethod() {
            // when
            ShipmentMethod method =
                    ShipmentMethod.of(ShipmentMethodType.DESIGNATED_COURIER, "LOGEN", "로젠택배");

            // then
            assertThat(method.type()).isEqualTo(ShipmentMethodType.DESIGNATED_COURIER);
            assertThat(method.courierCode()).isEqualTo("LOGEN");
            assertThat(method.courierName()).isEqualTo("로젠택배");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            // given
            ShipmentMethod method1 = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ShipmentMethod method2 = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");

            // then
            assertThat(method1).isEqualTo(method2);
            assertThat(method1.hashCode()).isEqualTo(method2.hashCode());
        }

        @Test
        @DisplayName("다른 type이면 동일하지 않다")
        void differentTypeIsNotEqual() {
            // given
            ShipmentMethod courier = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ShipmentMethod quick = ShipmentMethod.of(ShipmentMethodType.QUICK, "CJ", "CJ대한통운");

            // then
            assertThat(courier).isNotEqualTo(quick);
        }

        @Test
        @DisplayName("다른 courierCode이면 동일하지 않다")
        void differentCourierCodeIsNotEqual() {
            // given
            ShipmentMethod cj = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ShipmentMethod logen = ShipmentMethod.of(ShipmentMethodType.COURIER, "LOGEN", "로젠택배");

            // then
            assertThat(cj).isNotEqualTo(logen);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("ShipmentMethod는 record이므로 불변이다")
        void shipmentMethodIsImmutable() {
            // given
            ShipmentMethod method = ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");

            // then - record는 setter가 없으므로 컴파일 타임에 불변성이 보장됨
            assertThat(method.type()).isEqualTo(ShipmentMethodType.COURIER);
            assertThat(method.courierCode()).isEqualTo("CJ");
            assertThat(method.courierName()).isEqualTo("CJ대한통운");
        }
    }
}
