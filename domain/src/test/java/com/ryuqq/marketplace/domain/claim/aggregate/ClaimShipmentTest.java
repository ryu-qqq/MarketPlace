package com.ryuqq.marketplace.domain.claim.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.claim.ClaimFixtures;
import com.ryuqq.marketplace.domain.claim.vo.ClaimShipmentMethod;
import com.ryuqq.marketplace.domain.claim.vo.ClaimShipmentStatus;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ClaimShipment Aggregate 단위 테스트")
class ClaimShipmentTest {

    @Nested
    @DisplayName("forNew() - 신규 클레임 배송 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 클레임 배송을 생성하면 PENDING 상태로 초기화된다")
        void createNewClaimShipment_StatusIsPending() {
            // when
            ClaimShipment shipment = ClaimFixtures.newClaimShipment();

            // then
            assertThat(shipment.status()).isEqualTo(ClaimShipmentStatus.PENDING);
        }

        @Test
        @DisplayName("신규 클레임 배송 생성 시 trackingNumber, shippedAt, receivedAt은 null이다")
        void createNewClaimShipment_NullableFieldsAreNull() {
            // when
            ClaimShipment shipment = ClaimFixtures.newClaimShipment();

            // then
            assertThat(shipment.trackingNumber()).isNull();
            assertThat(shipment.shippedAt()).isNull();
            assertThat(shipment.receivedAt()).isNull();
        }

        @Test
        @DisplayName("신규 클레임 배송 생성 시 ID, 배송 방식, 비용 정보, 발송인, 수령인이 설정된다")
        void createNewClaimShipment_RequiredFieldsAreSet() {
            // when
            ClaimShipment shipment = ClaimFixtures.newClaimShipment();

            // then
            assertThat(shipment.id()).isNotNull();
            assertThat(shipment.method()).isNotNull();
            assertThat(shipment.feeInfo()).isNotNull();
            assertThat(shipment.sender()).isNotNull();
            assertThat(shipment.receiver()).isNotNull();
        }

        @Test
        @DisplayName("신규 클레임 배송은 미배송 상태이다")
        void createNewClaimShipment_IsNotDelivered() {
            // when
            ClaimShipment shipment = ClaimFixtures.newClaimShipment();

            // then
            assertThat(shipment.isDelivered()).isFalse();
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("PENDING 상태로 재구성한다")
        void reconstituteWithPendingStatus() {
            // when
            ClaimShipment shipment =
                    ClaimFixtures.reconstitutedClaimShipment(
                            ClaimFixtures.defaultClaimShipmentId(), ClaimShipmentStatus.PENDING);

            // then
            assertThat(shipment.status()).isEqualTo(ClaimShipmentStatus.PENDING);
            assertThat(shipment.id()).isEqualTo(ClaimFixtures.defaultClaimShipmentId());
        }

        @Test
        @DisplayName("IN_TRANSIT 상태로 재구성한다")
        void reconstituteWithInTransitStatus() {
            // when
            ClaimShipment shipment =
                    ClaimFixtures.reconstitutedClaimShipment(
                            ClaimFixtures.defaultClaimShipmentId(), ClaimShipmentStatus.IN_TRANSIT);

            // then
            assertThat(shipment.status()).isEqualTo(ClaimShipmentStatus.IN_TRANSIT);
            assertThat(shipment.trackingNumber()).isNotNull();
            assertThat(shipment.shippedAt()).isNotNull();
        }

        @Test
        @DisplayName("DELIVERED 상태로 재구성한다")
        void reconstituteWithDeliveredStatus() {
            // when
            ClaimShipment shipment =
                    ClaimFixtures.reconstitutedClaimShipment(
                            ClaimFixtures.defaultClaimShipmentId(), ClaimShipmentStatus.DELIVERED);

            // then
            assertThat(shipment.status()).isEqualTo(ClaimShipmentStatus.DELIVERED);
            assertThat(shipment.receivedAt()).isNotNull();
            assertThat(shipment.isDelivered()).isTrue();
        }
    }

    @Nested
    @DisplayName("ship() - 배송 시작")
    class ShipTest {

        @Test
        @DisplayName("배송 시작 시 상태가 IN_TRANSIT으로 변경된다")
        void ship_StatusBecomesInTransit() {
            // given
            ClaimShipment shipment = ClaimFixtures.newClaimShipment();
            Instant now = CommonVoFixtures.now();

            // when
            shipment.ship("1234567890123", now);

            // then
            assertThat(shipment.status()).isEqualTo(ClaimShipmentStatus.IN_TRANSIT);
        }

        @Test
        @DisplayName("배송 시작 시 운송장 번호가 설정된다")
        void ship_TrackingNumberIsSet() {
            // given
            ClaimShipment shipment = ClaimFixtures.newClaimShipment();
            String trackingNumber = "1234567890123";

            // when
            shipment.ship(trackingNumber, CommonVoFixtures.now());

            // then
            assertThat(shipment.trackingNumber()).isEqualTo(trackingNumber);
        }

        @Test
        @DisplayName("배송 시작 시 발송 시각이 설정된다")
        void ship_ShippedAtIsSet() {
            // given
            ClaimShipment shipment = ClaimFixtures.newClaimShipment();
            Instant now = CommonVoFixtures.now();

            // when
            shipment.ship("1234567890123", now);

            // then
            assertThat(shipment.shippedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("배송 시작 후에도 수령 시각은 null이다")
        void ship_ReceivedAtRemainsNull() {
            // given
            ClaimShipment shipment = ClaimFixtures.newClaimShipment();

            // when
            shipment.ship("1234567890123", CommonVoFixtures.now());

            // then
            assertThat(shipment.receivedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("complete() - 배송 완료")
    class CompleteTest {

        @Test
        @DisplayName("배송 완료 시 상태가 DELIVERED로 변경된다")
        void complete_StatusBecomesDelivered() {
            // given
            ClaimShipment shipment = ClaimFixtures.inTransitClaimShipment();
            Instant now = CommonVoFixtures.now();

            // when
            shipment.complete(now);

            // then
            assertThat(shipment.status()).isEqualTo(ClaimShipmentStatus.DELIVERED);
        }

        @Test
        @DisplayName("배송 완료 시 수령 시각이 설정된다")
        void complete_ReceivedAtIsSet() {
            // given
            ClaimShipment shipment = ClaimFixtures.inTransitClaimShipment();
            Instant now = CommonVoFixtures.now();

            // when
            shipment.complete(now);

            // then
            assertThat(shipment.receivedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("배송 완료 시 isDelivered가 true를 반환한다")
        void complete_IsDeliveredReturnsTrue() {
            // given
            ClaimShipment shipment = ClaimFixtures.inTransitClaimShipment();

            // when
            shipment.complete(CommonVoFixtures.now());

            // then
            assertThat(shipment.isDelivered()).isTrue();
        }
    }

    @Nested
    @DisplayName("fail() - 배송 실패")
    class FailTest {

        @Test
        @DisplayName("배송 실패 시 상태가 FAILED로 변경된다")
        void fail_StatusBecomesFailed() {
            // given
            ClaimShipment shipment = ClaimFixtures.inTransitClaimShipment();

            // when
            shipment.fail();

            // then
            assertThat(shipment.status()).isEqualTo(ClaimShipmentStatus.FAILED);
        }

        @Test
        @DisplayName("배송 실패 시 isDelivered는 false이다")
        void fail_IsDeliveredReturnsFalse() {
            // given
            ClaimShipment shipment = ClaimFixtures.inTransitClaimShipment();

            // when
            shipment.fail();

            // then
            assertThat(shipment.isDelivered()).isFalse();
        }
    }

    @Nested
    @DisplayName("updateMethod() - 배송 방식 변경")
    class UpdateMethodTest {

        @Test
        @DisplayName("배송 방식을 변경한다")
        void updateMethod_MethodIsUpdated() {
            // given
            ClaimShipment shipment = ClaimFixtures.newClaimShipment();
            ClaimShipmentMethod visitMethod = ClaimFixtures.visitMethod();

            // when
            shipment.updateMethod(visitMethod);

            // then
            assertThat(shipment.method()).isEqualTo(visitMethod);
        }

        @Test
        @DisplayName("VISIT 방식으로 변경한다")
        void updateMethod_ToVisitMethod() {
            // given
            ClaimShipment shipment = ClaimFixtures.newClaimShipment();

            // when
            shipment.updateMethod(ClaimFixtures.visitMethod());

            // then
            assertThat(shipment.method()).isEqualTo(ClaimFixtures.visitMethod());
        }
    }

    @Nested
    @DisplayName("updateTrackingNumber() - 운송장 번호 변경")
    class UpdateTrackingNumberTest {

        @Test
        @DisplayName("운송장 번호를 변경한다")
        void updateTrackingNumber_TrackingNumberIsUpdated() {
            // given
            ClaimShipment shipment = ClaimFixtures.inTransitClaimShipment();
            String newTrackingNumber = "9999999999999";

            // when
            shipment.updateTrackingNumber(newTrackingNumber);

            // then
            assertThat(shipment.trackingNumber()).isEqualTo(newTrackingNumber);
        }
    }

    @Nested
    @DisplayName("isDelivered() - 배송 완료 여부")
    class IsDeliveredTest {

        @Test
        @DisplayName("PENDING 상태에서 isDelivered는 false이다")
        void pendingShipment_IsNotDelivered() {
            // given
            ClaimShipment shipment = ClaimFixtures.newClaimShipment();

            // then
            assertThat(shipment.isDelivered()).isFalse();
        }

        @Test
        @DisplayName("IN_TRANSIT 상태에서 isDelivered는 false이다")
        void inTransitShipment_IsNotDelivered() {
            // given
            ClaimShipment shipment = ClaimFixtures.inTransitClaimShipment();

            // then
            assertThat(shipment.isDelivered()).isFalse();
        }

        @Test
        @DisplayName("DELIVERED 상태에서 isDelivered는 true이다")
        void deliveredShipment_IsDelivered() {
            // given
            ClaimShipment shipment = ClaimFixtures.deliveredClaimShipment();

            // then
            assertThat(shipment.isDelivered()).isTrue();
        }

        @Test
        @DisplayName("FAILED 상태에서 isDelivered는 false이다")
        void failedShipment_IsNotDelivered() {
            // given
            ClaimShipment shipment = ClaimFixtures.failedClaimShipment();

            // then
            assertThat(shipment.isDelivered()).isFalse();
        }
    }
}
