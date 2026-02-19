package com.ryuqq.marketplace.domain.claim.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ClaimShipmentStatus 단위 테스트")
class ClaimShipmentStatusTest {

    @Nested
    @DisplayName("열거형 값 검증")
    class EnumValuesTest {

        @Test
        @DisplayName("PENDING 상태가 존재한다")
        void pendingStatusExists() {
            assertThat(ClaimShipmentStatus.PENDING).isNotNull();
        }

        @Test
        @DisplayName("IN_TRANSIT 상태가 존재한다")
        void inTransitStatusExists() {
            assertThat(ClaimShipmentStatus.IN_TRANSIT).isNotNull();
        }

        @Test
        @DisplayName("DELIVERED 상태가 존재한다")
        void deliveredStatusExists() {
            assertThat(ClaimShipmentStatus.DELIVERED).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태가 존재한다")
        void failedStatusExists() {
            assertThat(ClaimShipmentStatus.FAILED).isNotNull();
        }

        @Test
        @DisplayName("ClaimShipmentStatus는 총 4개의 상태를 가진다")
        void hasExactlyFourStatuses() {
            assertThat(ClaimShipmentStatus.values()).hasSize(4);
        }
    }

    @Nested
    @DisplayName("상태 전이는 ClaimShipment Aggregate에서 검증한다")
    class StateTransitionNoteTest {

        @Test
        @DisplayName("PENDING → IN_TRANSIT 전이는 ship() 에서 수행된다")
        void pendingToInTransit_ViaShip() {
            // ClaimShipmentTest.ShipTest 에서 검증
            assertThat(ClaimShipmentStatus.PENDING.name()).isEqualTo("PENDING");
            assertThat(ClaimShipmentStatus.IN_TRANSIT.name()).isEqualTo("IN_TRANSIT");
        }

        @Test
        @DisplayName("IN_TRANSIT → DELIVERED 전이는 complete() 에서 수행된다")
        void inTransitToDelivered_ViaComplete() {
            // ClaimShipmentTest.CompleteTest 에서 검증
            assertThat(ClaimShipmentStatus.IN_TRANSIT.name()).isEqualTo("IN_TRANSIT");
            assertThat(ClaimShipmentStatus.DELIVERED.name()).isEqualTo("DELIVERED");
        }

        @Test
        @DisplayName("IN_TRANSIT → FAILED 전이는 fail() 에서 수행된다")
        void inTransitToFailed_ViaFail() {
            // ClaimShipmentTest.FailTest 에서 검증
            assertThat(ClaimShipmentStatus.IN_TRANSIT.name()).isEqualTo("IN_TRANSIT");
            assertThat(ClaimShipmentStatus.FAILED.name()).isEqualTo("FAILED");
        }
    }
}
