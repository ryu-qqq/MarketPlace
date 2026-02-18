package com.ryuqq.marketplace.domain.shipment.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentStatus 테스트")
class ShipmentStatusTest {

    @Nested
    @DisplayName("열거형 값 존재 테스트")
    class EnumValueTest {

        @Test
        @DisplayName("배송 상태 열거형 값이 모두 존재한다")
        void allStatusValuesExist() {
            assertThat(ShipmentStatus.values())
                    .containsExactlyInAnyOrder(
                            ShipmentStatus.READY,
                            ShipmentStatus.PREPARING,
                            ShipmentStatus.SHIPPED,
                            ShipmentStatus.IN_TRANSIT,
                            ShipmentStatus.DELIVERED,
                            ShipmentStatus.FAILED,
                            ShipmentStatus.CANCELLED);
        }

        @Test
        @DisplayName("READY 상태가 존재한다")
        void readyStatusExists() {
            assertThat(ShipmentStatus.READY).isNotNull();
            assertThat(ShipmentStatus.READY.name()).isEqualTo("READY");
        }

        @Test
        @DisplayName("PREPARING 상태가 존재한다")
        void preparingStatusExists() {
            assertThat(ShipmentStatus.PREPARING).isNotNull();
            assertThat(ShipmentStatus.PREPARING.name()).isEqualTo("PREPARING");
        }

        @Test
        @DisplayName("SHIPPED 상태가 존재한다")
        void shippedStatusExists() {
            assertThat(ShipmentStatus.SHIPPED).isNotNull();
            assertThat(ShipmentStatus.SHIPPED.name()).isEqualTo("SHIPPED");
        }

        @Test
        @DisplayName("IN_TRANSIT 상태가 존재한다")
        void inTransitStatusExists() {
            assertThat(ShipmentStatus.IN_TRANSIT).isNotNull();
            assertThat(ShipmentStatus.IN_TRANSIT.name()).isEqualTo("IN_TRANSIT");
        }

        @Test
        @DisplayName("DELIVERED 상태가 존재한다")
        void deliveredStatusExists() {
            assertThat(ShipmentStatus.DELIVERED).isNotNull();
            assertThat(ShipmentStatus.DELIVERED.name()).isEqualTo("DELIVERED");
        }

        @Test
        @DisplayName("FAILED 상태가 존재한다")
        void failedStatusExists() {
            assertThat(ShipmentStatus.FAILED).isNotNull();
            assertThat(ShipmentStatus.FAILED.name()).isEqualTo("FAILED");
        }

        @Test
        @DisplayName("CANCELLED 상태가 존재한다")
        void cancelledStatusExists() {
            assertThat(ShipmentStatus.CANCELLED).isNotNull();
            assertThat(ShipmentStatus.CANCELLED.name()).isEqualTo("CANCELLED");
        }
    }

    @Nested
    @DisplayName("valueOf() 조회 테스트")
    class ValueOfTest {

        @Test
        @DisplayName("이름으로 상태를 조회한다")
        void findByName() {
            assertThat(ShipmentStatus.valueOf("READY")).isEqualTo(ShipmentStatus.READY);
            assertThat(ShipmentStatus.valueOf("PREPARING")).isEqualTo(ShipmentStatus.PREPARING);
            assertThat(ShipmentStatus.valueOf("SHIPPED")).isEqualTo(ShipmentStatus.SHIPPED);
            assertThat(ShipmentStatus.valueOf("IN_TRANSIT")).isEqualTo(ShipmentStatus.IN_TRANSIT);
            assertThat(ShipmentStatus.valueOf("DELIVERED")).isEqualTo(ShipmentStatus.DELIVERED);
            assertThat(ShipmentStatus.valueOf("FAILED")).isEqualTo(ShipmentStatus.FAILED);
            assertThat(ShipmentStatus.valueOf("CANCELLED")).isEqualTo(ShipmentStatus.CANCELLED);
        }

        @Test
        @DisplayName("존재하지 않는 이름으로 조회하면 예외가 발생한다")
        void findByInvalidName_ThrowsException() {
            assertThatThrownBy(() -> ShipmentStatus.valueOf("UNKNOWN"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("ordinal 순서 테스트")
    class OrdinalTest {

        @Test
        @DisplayName("READY가 첫 번째 상태이다")
        void readyIsFirst() {
            assertThat(ShipmentStatus.READY.ordinal()).isEqualTo(0);
        }
    }
}
