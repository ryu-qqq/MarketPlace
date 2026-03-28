package com.ryuqq.marketplace.domain.shipment.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.exception.ShipmentException;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Shipment Aggregate 테스트")
class ShipmentTest {

    @Nested
    @DisplayName("forNew() - 신규 배송 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 배송을 생성하면 READY 상태이다")
        void createNewShipmentWithReadyStatus() {
            // when
            Shipment shipment = ShipmentFixtures.newShipment();

            // then
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.READY);
        }

        @Test
        @DisplayName("신규 배송 생성 시 기본 필드가 올바르게 설정된다")
        void newShipmentFieldsSetCorrectly() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            OrderItemId orderItemId = OrderItemId.of(1001L);
            Shipment shipment =
                    Shipment.forNew(
                            ShipmentFixtures.defaultShipmentId(),
                            ShipmentFixtures.defaultShipmentNumber(),
                            orderItemId,
                            now);

            // then
            assertThat(shipment.id()).isEqualTo(ShipmentFixtures.defaultShipmentId());
            assertThat(shipment.shipmentNumber())
                    .isEqualTo(ShipmentFixtures.defaultShipmentNumber());
            assertThat(shipment.orderItemId()).isEqualTo(orderItemId);
            assertThat(shipment.orderItemIdValue()).isEqualTo(1001L);
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.READY);
            assertThat(shipment.shipmentMethod()).isNull();
            assertThat(shipment.trackingNumber()).isNull();
            assertThat(shipment.orderConfirmedAt()).isNull();
            assertThat(shipment.shippedAt()).isNull();
            assertThat(shipment.deliveredAt()).isNull();
            assertThat(shipment.createdAt()).isEqualTo(now);
            assertThat(shipment.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("idValue()와 shipmentNumberValue() 편의 메서드가 올바르게 동작한다")
        void convenienceGettersReturnCorrectValues() {
            // when
            Shipment shipment = ShipmentFixtures.newShipment();

            // then
            assertThat(shipment.idValue()).isEqualTo(ShipmentFixtures.defaultShipmentId().value());
            assertThat(shipment.shipmentNumberValue())
                    .isEqualTo(ShipmentFixtures.defaultShipmentNumber().value());
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("READY 상태로 재구성한다")
        void reconstituteReadyShipment() {
            // when
            Shipment shipment = ShipmentFixtures.readyShipment();

            // then
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.READY);
            assertThat(shipment.shipmentMethod()).isNull();
            assertThat(shipment.trackingNumber()).isNull();
        }

        @Test
        @DisplayName("SHIPPED 상태로 재구성하면 송장번호와 배송방법이 설정된다")
        void reconstituteShippedShipment() {
            // when
            Shipment shipment = ShipmentFixtures.shippedShipment();

            // then
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.SHIPPED);
            assertThat(shipment.trackingNumber()).isNotNull();
            assertThat(shipment.shipmentMethod()).isNotNull();
            assertThat(shipment.shippedAt()).isNotNull();
        }

        @Test
        @DisplayName("DELIVERED 상태로 재구성하면 배송완료 시간이 설정된다")
        void reconstituteDeliveredShipment() {
            // when
            Shipment shipment = ShipmentFixtures.deliveredShipment();

            // then
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.DELIVERED);
            assertThat(shipment.deliveredAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("prepare() - 발주확인 (READY → PREPARING)")
    class PrepareTest {

        @Test
        @DisplayName("READY 상태의 배송을 발주확인 처리한다")
        void prepareReadyShipment() {
            // given
            Shipment shipment = ShipmentFixtures.readyShipment();
            Instant now = CommonVoFixtures.now();

            // when
            shipment.prepare(now);

            // then
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.PREPARING);
            assertThat(shipment.orderConfirmedAt()).isEqualTo(now);
            assertThat(shipment.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("READY가 아닌 상태에서 prepare()를 호출하면 예외가 발생한다")
        void prepareNotReadyShipment_ThrowsException() {
            // given
            Shipment shipment = ShipmentFixtures.preparingShipment();

            // when & then
            assertThatThrownBy(() -> shipment.prepare(CommonVoFixtures.now()))
                    .isInstanceOf(ShipmentException.class);
        }

        @Test
        @DisplayName("SHIPPED 상태에서 prepare()를 호출하면 예외가 발생한다")
        void prepareShippedShipment_ThrowsException() {
            // given
            Shipment shipment = ShipmentFixtures.shippedShipment();

            // when & then
            assertThatThrownBy(() -> shipment.prepare(CommonVoFixtures.now()))
                    .isInstanceOf(ShipmentException.class);
        }
    }

    @Nested
    @DisplayName("ship() - 송장등록 (PREPARING → SHIPPED)")
    class ShipTest {

        @Test
        @DisplayName("PREPARING 상태에서 송장번호와 배송방법을 등록하여 발송 처리한다")
        void shipPreparingShipment() {
            // given
            Shipment shipment = ShipmentFixtures.preparingShipment();
            String trackingNumber = "9876543210";
            Instant now = CommonVoFixtures.now();

            // when
            shipment.ship(trackingNumber, ShipmentFixtures.defaultShipmentMethod(), now);

            // then
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.SHIPPED);
            assertThat(shipment.trackingNumber()).isEqualTo(trackingNumber);
            assertThat(shipment.shipmentMethod())
                    .isEqualTo(ShipmentFixtures.defaultShipmentMethod());
            assertThat(shipment.shippedAt()).isEqualTo(now);
            assertThat(shipment.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("송장번호가 null이면 예외가 발생한다")
        void shipWithNullTrackingNumber_ThrowsException() {
            // given
            Shipment shipment = ShipmentFixtures.preparingShipment();

            // when & then
            assertThatThrownBy(
                            () ->
                                    shipment.ship(
                                            null,
                                            ShipmentFixtures.defaultShipmentMethod(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(ShipmentException.class);
        }

        @Test
        @DisplayName("송장번호가 빈 문자열이면 예외가 발생한다")
        void shipWithBlankTrackingNumber_ThrowsException() {
            // given
            Shipment shipment = ShipmentFixtures.preparingShipment();

            // when & then
            assertThatThrownBy(
                            () ->
                                    shipment.ship(
                                            "   ",
                                            ShipmentFixtures.defaultShipmentMethod(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(ShipmentException.class);
        }

        @Test
        @DisplayName("READY 상태에서 ship()을 호출하면 예외가 발생한다")
        void shipReadyShipment_ThrowsException() {
            // given
            Shipment shipment = ShipmentFixtures.readyShipment();

            // when & then
            assertThatThrownBy(
                            () ->
                                    shipment.ship(
                                            "1234567890",
                                            ShipmentFixtures.defaultShipmentMethod(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(ShipmentException.class);
        }
    }

    @Nested
    @DisplayName("startTransit() - 배송중 (SHIPPED → IN_TRANSIT)")
    class StartTransitTest {

        @Test
        @DisplayName("SHIPPED 상태의 배송을 배송중으로 변경한다")
        void startTransitShippedShipment() {
            // given
            Shipment shipment = ShipmentFixtures.shippedShipment();
            Instant now = CommonVoFixtures.now();

            // when
            shipment.startTransit(now);

            // then
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.IN_TRANSIT);
            assertThat(shipment.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("SHIPPED가 아닌 상태에서 startTransit()을 호출하면 예외가 발생한다")
        void startTransitNotShippedShipment_ThrowsException() {
            // given
            Shipment shipment = ShipmentFixtures.preparingShipment();

            // when & then
            assertThatThrownBy(() -> shipment.startTransit(CommonVoFixtures.now()))
                    .isInstanceOf(ShipmentException.class);
        }
    }

    @Nested
    @DisplayName("deliver() - 배송완료 (IN_TRANSIT → DELIVERED)")
    class DeliverTest {

        @Test
        @DisplayName("IN_TRANSIT 상태의 배송을 배송완료 처리한다")
        void deliverInTransitShipment() {
            // given
            Shipment shipment = ShipmentFixtures.inTransitShipment();
            Instant now = CommonVoFixtures.now();

            // when
            shipment.deliver(now);

            // then
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.DELIVERED);
            assertThat(shipment.deliveredAt()).isEqualTo(now);
            assertThat(shipment.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("IN_TRANSIT가 아닌 상태에서 deliver()를 호출하면 예외가 발생한다")
        void deliverNotInTransitShipment_ThrowsException() {
            // given
            Shipment shipment = ShipmentFixtures.shippedShipment();

            // when & then
            assertThatThrownBy(() -> shipment.deliver(CommonVoFixtures.now()))
                    .isInstanceOf(ShipmentException.class);
        }
    }

    @Nested
    @DisplayName("fail() - 배송실패 (IN_TRANSIT → FAILED)")
    class FailTest {

        @Test
        @DisplayName("IN_TRANSIT 상태의 배송을 배송실패 처리한다")
        void failInTransitShipment() {
            // given
            Shipment shipment = ShipmentFixtures.inTransitShipment();
            Instant now = CommonVoFixtures.now();

            // when
            shipment.fail(now);

            // then
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.FAILED);
            assertThat(shipment.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("IN_TRANSIT가 아닌 상태에서 fail()을 호출하면 예외가 발생한다")
        void failNotInTransitShipment_ThrowsException() {
            // given
            Shipment shipment = ShipmentFixtures.shippedShipment();

            // when & then
            assertThatThrownBy(() -> shipment.fail(CommonVoFixtures.now()))
                    .isInstanceOf(ShipmentException.class);
        }

        @Test
        @DisplayName("DELIVERED 상태에서 fail()을 호출하면 예외가 발생한다")
        void failDeliveredShipment_ThrowsException() {
            // given
            Shipment shipment = ShipmentFixtures.deliveredShipment();

            // when & then
            assertThatThrownBy(() -> shipment.fail(CommonVoFixtures.now()))
                    .isInstanceOf(ShipmentException.class);
        }
    }

    @Nested
    @DisplayName("cancel() - 취소 (PREPARING → CANCELLED)")
    class CancelTest {

        @Test
        @DisplayName("PREPARING 상태의 배송을 취소한다")
        void cancelPreparingShipment() {
            // given
            Shipment shipment = ShipmentFixtures.preparingShipment();
            Instant now = CommonVoFixtures.now();

            // when
            shipment.cancel(now);

            // then
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.CANCELLED);
            assertThat(shipment.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("READY 상태에서 cancel()을 호출하면 예외가 발생한다")
        void cancelReadyShipment_ThrowsException() {
            // given
            Shipment shipment = ShipmentFixtures.readyShipment();

            // when & then
            assertThatThrownBy(() -> shipment.cancel(CommonVoFixtures.now()))
                    .isInstanceOf(ShipmentException.class);
        }

        @Test
        @DisplayName("SHIPPED 상태에서 cancel()을 호출하면 예외가 발생한다")
        void cancelShippedShipment_ThrowsException() {
            // given
            Shipment shipment = ShipmentFixtures.shippedShipment();

            // when & then
            assertThatThrownBy(() -> shipment.cancel(CommonVoFixtures.now()))
                    .isInstanceOf(ShipmentException.class);
        }
    }

    @Nested
    @DisplayName("validateTransition() - 잘못된 상태 전이 검증")
    class InvalidTransitionTest {

        @Test
        @DisplayName("예외 메시지에 현재 상태 정보가 포함된다")
        void invalidTransitionExceptionContainsStatusInfo() {
            // given - SHIPPED 상태에서 prepare()는 실패해야 한다
            Shipment shippedShipment = ShipmentFixtures.shippedShipment();

            // when & then
            assertThatThrownBy(() -> shippedShipment.prepare(CommonVoFixtures.now()))
                    .isInstanceOf(ShipmentException.class)
                    .hasMessageContaining("SHIPPED");
        }

        @Test
        @DisplayName("상태 전이 순서를 건너뛸 수 없다 - READY에서 직접 SHIPPED로 변경 불가")
        void cannotSkipTransitionStep() {
            // given
            Shipment shipment = ShipmentFixtures.readyShipment();

            // when & then
            assertThatThrownBy(
                            () ->
                                    shipment.ship(
                                            "1234567890",
                                            ShipmentFixtures.defaultShipmentMethod(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(ShipmentException.class);
        }

        @Test
        @DisplayName("완료 상태(DELIVERED)에서 어떤 전이도 불가능하다")
        void noTransitionFromDelivered() {
            // given
            Shipment shipment = ShipmentFixtures.deliveredShipment();

            // when & then
            assertThatThrownBy(() -> shipment.fail(CommonVoFixtures.now()))
                    .isInstanceOf(ShipmentException.class);
        }

        @Test
        @DisplayName("완료 상태(FAILED)에서 어떤 전이도 불가능하다")
        void noTransitionFromFailed() {
            // given
            Shipment shipment = ShipmentFixtures.failedShipment();

            // when & then
            assertThatThrownBy(() -> shipment.deliver(CommonVoFixtures.now()))
                    .isInstanceOf(ShipmentException.class);
        }
    }

    @Nested
    @DisplayName("전체 배송 흐름 통합 테스트")
    class FullFlowTest {

        @Test
        @DisplayName("정상 배송 흐름: READY → PREPARING → SHIPPED → IN_TRANSIT → DELIVERED")
        void normalDeliveryFlow() {
            // given
            Shipment shipment = ShipmentFixtures.readyShipment();
            Instant now = CommonVoFixtures.now();

            // when & then
            shipment.prepare(now);
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.PREPARING);

            shipment.ship("9876543210", ShipmentFixtures.defaultShipmentMethod(), now);
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.SHIPPED);

            shipment.startTransit(now);
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.IN_TRANSIT);

            shipment.deliver(now);
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.DELIVERED);
            assertThat(shipment.deliveredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("배송 실패 흐름: READY → PREPARING → SHIPPED → IN_TRANSIT → FAILED")
        void failedDeliveryFlow() {
            // given
            Shipment shipment = ShipmentFixtures.readyShipment();
            Instant now = CommonVoFixtures.now();

            // when & then
            shipment.prepare(now);
            shipment.ship("9876543210", ShipmentFixtures.defaultShipmentMethod(), now);
            shipment.startTransit(now);
            shipment.fail(now);

            assertThat(shipment.status()).isEqualTo(ShipmentStatus.FAILED);
        }

        @Test
        @DisplayName("취소 흐름: READY → PREPARING → CANCELLED")
        void cancelledFlow() {
            // given
            Shipment shipment = ShipmentFixtures.readyShipment();
            Instant now = CommonVoFixtures.now();

            // when
            shipment.prepare(now);
            shipment.cancel(now);

            // then
            assertThat(shipment.status()).isEqualTo(ShipmentStatus.CANCELLED);
        }
    }
}
