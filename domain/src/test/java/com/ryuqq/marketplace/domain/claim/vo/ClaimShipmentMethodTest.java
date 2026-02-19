package com.ryuqq.marketplace.domain.claim.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ClaimShipmentMethod Value Object 단위 테스트")
class ClaimShipmentMethodTest {

    @Nested
    @DisplayName("of() - 일반 생성 테스트")
    class OfTest {

        @Test
        @DisplayName("COURIER 타입으로 택배사 코드와 함께 생성한다")
        void createCourierMethod() {
            // given & when
            ClaimShipmentMethod method =
                    ClaimShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");

            // then
            assertThat(method.type()).isEqualTo(ShipmentMethodType.COURIER);
            assertThat(method.courierCode()).isEqualTo("CJ");
            assertThat(method.courierName()).isEqualTo("CJ대한통운");
        }

        @Test
        @DisplayName("QUICK 타입으로 택배사 코드와 함께 생성한다")
        void createQuickMethod() {
            // given & when
            ClaimShipmentMethod method =
                    ClaimShipmentMethod.of(ShipmentMethodType.QUICK, "QUICK", "퀵서비스");

            // then
            assertThat(method.type()).isEqualTo(ShipmentMethodType.QUICK);
            assertThat(method.courierCode()).isEqualTo("QUICK");
        }

        @Test
        @DisplayName("AUTO_PICKUP 타입으로 택배사 코드와 함께 생성한다")
        void createAutoPickupMethod() {
            // given & when
            ClaimShipmentMethod method =
                    ClaimShipmentMethod.of(ShipmentMethodType.AUTO_PICKUP, "AP", "자동수거");

            // then
            assertThat(method.type()).isEqualTo(ShipmentMethodType.AUTO_PICKUP);
            assertThat(method.courierCode()).isEqualTo("AP");
        }

        @Test
        @DisplayName("DESIGNATED_COURIER 타입으로 택배사 코드와 함께 생성한다")
        void createDesignatedCourierMethod() {
            // given & when
            ClaimShipmentMethod method =
                    ClaimShipmentMethod.of(ShipmentMethodType.DESIGNATED_COURIER, "DC", "지정택배");

            // then
            assertThat(method.type()).isEqualTo(ShipmentMethodType.DESIGNATED_COURIER);
            assertThat(method.courierCode()).isEqualTo("DC");
        }

        @Test
        @DisplayName("type이 null이면 예외가 발생한다")
        void createWithNullType_ThrowsException() {
            assertThatThrownBy(() -> ClaimShipmentMethod.of(null, "CJ", "CJ대한통운"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("배송 방식은 필수");
        }

        @Test
        @DisplayName("VISIT이 아닌 타입에서 courierCode가 null이면 예외가 발생한다")
        void createNonVisitWithNullCourierCode_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    ClaimShipmentMethod.of(
                                            ShipmentMethodType.COURIER, null, "CJ대한통운"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("택배사 코드가 필수");
        }

        @Test
        @DisplayName("VISIT이 아닌 타입에서 courierCode가 빈 문자열이면 예외가 발생한다")
        void createNonVisitWithBlankCourierCode_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    ClaimShipmentMethod.of(
                                            ShipmentMethodType.COURIER, "  ", "CJ대한통운"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("택배사 코드가 필수");
        }
    }

    @Nested
    @DisplayName("VISIT 방식 - courierCode 불필요")
    class VisitMethodTest {

        @Test
        @DisplayName("VISIT 타입은 courierCode 없이 생성할 수 있다")
        void createVisitMethodWithoutCourierCode_DoesNotThrow() {
            assertThatCode(() -> ClaimShipmentMethod.of(ShipmentMethodType.VISIT, null, null))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("VISIT 타입에서 courierCode는 null이다")
        void visitMethodCourierCodeIsNull() {
            // given & when
            ClaimShipmentMethod method =
                    ClaimShipmentMethod.of(ShipmentMethodType.VISIT, null, null);

            // then
            assertThat(method.courierCode()).isNull();
            assertThat(method.courierName()).isNull();
        }
    }

    @Nested
    @DisplayName("visit() - 방문 수거 팩토리 메서드")
    class VisitFactoryTest {

        @Test
        @DisplayName("visit() 팩토리로 VISIT 타입의 배송 방식을 생성한다")
        void visitFactory_CreatesVisitMethod() {
            // given & when
            ClaimShipmentMethod method = ClaimShipmentMethod.visit();

            // then
            assertThat(method.type()).isEqualTo(ShipmentMethodType.VISIT);
        }

        @Test
        @DisplayName("visit() 팩토리로 생성하면 courierCode와 courierName이 null이다")
        void visitFactory_CourierFieldsAreNull() {
            // given & when
            ClaimShipmentMethod method = ClaimShipmentMethod.visit();

            // then
            assertThat(method.courierCode()).isNull();
            assertThat(method.courierName()).isNull();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 타입, 코드, 이름이면 동일하다")
        void sameFieldsAreEqual() {
            // given
            ClaimShipmentMethod method1 =
                    ClaimShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ClaimShipmentMethod method2 =
                    ClaimShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");

            // then
            assertThat(method1).isEqualTo(method2);
            assertThat(method1.hashCode()).isEqualTo(method2.hashCode());
        }

        @Test
        @DisplayName("다른 택배사 코드이면 동일하지 않다")
        void differentCourierCodeAreNotEqual() {
            // given
            ClaimShipmentMethod method1 =
                    ClaimShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
            ClaimShipmentMethod method2 =
                    ClaimShipmentMethod.of(ShipmentMethodType.COURIER, "HANJIN", "한진택배");

            // then
            assertThat(method1).isNotEqualTo(method2);
        }

        @Test
        @DisplayName("visit() 팩토리로 생성한 두 객체는 동일하다")
        void twoVisitMethodsAreEqual() {
            // given
            ClaimShipmentMethod visit1 = ClaimShipmentMethod.visit();
            ClaimShipmentMethod visit2 = ClaimShipmentMethod.visit();

            // then
            assertThat(visit1).isEqualTo(visit2);
        }
    }
}
