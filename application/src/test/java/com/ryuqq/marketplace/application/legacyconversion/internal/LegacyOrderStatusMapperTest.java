package com.ryuqq.marketplace.application.legacyconversion.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * LegacyOrderStatusMapper 단위 테스트.
 *
 * <p>레거시 주문 상태 문자열 → 내부 도메인 상태 매핑 로직을 검증합니다.
 */
@Tag("unit")
@DisplayName("LegacyOrderStatusMapper 단위 테스트")
class LegacyOrderStatusMapperTest {

    private LegacyOrderStatusMapper sut;

    @BeforeEach
    void setUp() {
        sut = new LegacyOrderStatusMapper();
    }

    @Nested
    @DisplayName("isEligibleForMigration 메서드 테스트")
    class IsEligibleForMigrationTest {

        @Test
        @DisplayName("ORDER_FAILED 상태는 이관 제외 대상이다")
        void isEligibleForMigration_WithOrderFailed_ReturnsFalse() {
            assertThat(sut.isEligibleForMigration("ORDER_FAILED")).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "ORDER_PROCESSING",
            "DELIVERY_PENDING",
            "DELIVERY_PROCESSING",
            "DELIVERY_COMPLETED",
            "ORDER_COMPLETED",
            "SALE_CANCELLED",
            "RETURN_REQUEST"
        })
        @DisplayName("ORDER_FAILED 이외의 상태는 이관 대상이다")
        void isEligibleForMigration_WithOtherStatus_ReturnsTrue(String status) {
            assertThat(sut.isEligibleForMigration(status)).isTrue();
        }
    }

    @Nested
    @DisplayName("resolve 메서드 — 정상 주문 흐름")
    class NormalOrderFlowTest {

        @Test
        @DisplayName("ORDER_PROCESSING → Shipment 없음, 클레임 없음")
        void resolve_WithOrderProcessing_ReturnsNormalOrderWithNoShipment() {
            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    sut.resolve("ORDER_PROCESSING");

            assertThat(resolution.needsShipment()).isFalse();
            assertThat(resolution.hasCancel()).isFalse();
            assertThat(resolution.hasRefund()).isFalse();
        }

        @Test
        @DisplayName("DELIVERY_PENDING → Shipment READY")
        void resolve_WithDeliveryPending_ReturnsShipmentReady() {
            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    sut.resolve("DELIVERY_PENDING");

            assertThat(resolution.needsShipment()).isTrue();
            assertThat(resolution.shipmentStatus()).isEqualTo(ShipmentStatus.READY);
            assertThat(resolution.hasCancel()).isFalse();
            assertThat(resolution.hasRefund()).isFalse();
        }

        @Test
        @DisplayName("DELIVERY_PROCESSING → Shipment IN_TRANSIT")
        void resolve_WithDeliveryProcessing_ReturnsShipmentInTransit() {
            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    sut.resolve("DELIVERY_PROCESSING");

            assertThat(resolution.needsShipment()).isTrue();
            assertThat(resolution.shipmentStatus()).isEqualTo(ShipmentStatus.IN_TRANSIT);
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "DELIVERY_COMPLETED",
            "DELIVERY_COMPLETE",
            "ORDER_COMPLETED",
            "SETTLEMENT_PROCESSING",
            "SETTLEMENT_COMPLETED"
        })
        @DisplayName("배송완료/정산 관련 상태 → Shipment DELIVERED")
        void resolve_WithDeliveredStatuses_ReturnsShipmentDelivered(String status) {
            LegacyOrderStatusMapper.OrderStatusResolution resolution = sut.resolve(status);

            assertThat(resolution.needsShipment()).isTrue();
            assertThat(resolution.shipmentStatus()).isEqualTo(ShipmentStatus.DELIVERED);
            assertThat(resolution.hasCancel()).isFalse();
            assertThat(resolution.hasRefund()).isFalse();
        }
    }

    @Nested
    @DisplayName("resolve 메서드 — 취소 흐름")
    class CancelFlowTest {

        @ParameterizedTest
        @ValueSource(strings = {
            "SALE_CANCELLED",
            "SALE_CANCELLED_COMPLETED",
            "CANCEL_REQUEST_COMPLETED"
        })
        @DisplayName("취소 완료 상태 → Cancel COMPLETED")
        void resolve_WithCancelCompletedStatuses_ReturnsCancelCompleted(String status) {
            LegacyOrderStatusMapper.OrderStatusResolution resolution = sut.resolve(status);

            assertThat(resolution.hasCancel()).isTrue();
            assertThat(resolution.cancelStatus()).isEqualTo(CancelStatus.COMPLETED);
            assertThat(resolution.hasRefund()).isFalse();
        }

        @Test
        @DisplayName("CANCEL_REQUEST_CONFIRMED → Cancel APPROVED")
        void resolve_WithCancelRequestConfirmed_ReturnsCancelApproved() {
            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    sut.resolve("CANCEL_REQUEST_CONFIRMED");

            assertThat(resolution.hasCancel()).isTrue();
            assertThat(resolution.cancelStatus()).isEqualTo(CancelStatus.APPROVED);
        }
    }

    @Nested
    @DisplayName("resolve 메서드 — 반품 흐름")
    class RefundFlowTest {

        @Test
        @DisplayName("RETURN_REQUEST → Refund REQUESTED")
        void resolve_WithReturnRequest_ReturnsRefundRequested() {
            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    sut.resolve("RETURN_REQUEST");

            assertThat(resolution.hasRefund()).isTrue();
            assertThat(resolution.refundStatus()).isEqualTo(RefundStatus.REQUESTED);
            assertThat(resolution.hasCancel()).isFalse();
        }

        @Test
        @DisplayName("RETURN_REQUEST_COMPLETED → Refund COMPLETED")
        void resolve_WithReturnRequestCompleted_ReturnsRefundCompleted() {
            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    sut.resolve("RETURN_REQUEST_COMPLETED");

            assertThat(resolution.hasRefund()).isTrue();
            assertThat(resolution.refundStatus()).isEqualTo(RefundStatus.COMPLETED);
        }

        @Test
        @DisplayName("RETURN_REQUEST_REJECTED → Refund REJECTED")
        void resolve_WithReturnRequestRejected_ReturnsRefundRejected() {
            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    sut.resolve("RETURN_REQUEST_REJECTED");

            assertThat(resolution.hasRefund()).isTrue();
            assertThat(resolution.refundStatus()).isEqualTo(RefundStatus.REJECTED);
        }

        @Test
        @DisplayName("RETURN_REQUEST_CONFIRMED → Refund COLLECTING")
        void resolve_WithReturnRequestConfirmed_ReturnsRefundCollecting() {
            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    sut.resolve("RETURN_REQUEST_CONFIRMED");

            assertThat(resolution.hasRefund()).isTrue();
            assertThat(resolution.refundStatus()).isEqualTo(RefundStatus.COLLECTING);
        }
    }

    @Nested
    @DisplayName("resolve 메서드 — 예외 처리")
    class UnknownStatusTest {

        @Test
        @DisplayName("매핑 불가능한 상태는 IllegalArgumentException을 던진다")
        void resolve_WithUnknownStatus_ThrowsIllegalArgumentException() {
            assertThatThrownBy(() -> sut.resolve("UNKNOWN_STATUS"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("매핑 불가능한 레거시 주문 상태: UNKNOWN_STATUS");
        }
    }

    @Nested
    @DisplayName("OrderStatusResolution 도우미 메서드 테스트")
    class OrderStatusResolutionHelperTest {

        @Test
        @DisplayName("normalOrder(null) 은 Shipment 불필요")
        void normalOrder_WithNullShipmentStatus_NeedsShipmentFalse() {
            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    LegacyOrderStatusMapper.OrderStatusResolution.normalOrder(null);

            assertThat(resolution.needsShipment()).isFalse();
            assertThat(resolution.hasClaim()).isFalse();
        }

        @Test
        @DisplayName("normalOrder(READY) 는 Shipment 필요, 클레임 없음")
        void normalOrder_WithShipmentStatus_NeedsShipmentTrue() {
            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    LegacyOrderStatusMapper.OrderStatusResolution.normalOrder(ShipmentStatus.READY);

            assertThat(resolution.needsShipment()).isTrue();
            assertThat(resolution.hasClaim()).isFalse();
        }

        @Test
        @DisplayName("withCancel 은 hasCancel=true, hasRefund=false")
        void withCancel_HasCancelTrueRefundFalse() {
            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    LegacyOrderStatusMapper.OrderStatusResolution.withCancel(
                            ShipmentStatus.DELIVERED, CancelStatus.COMPLETED);

            assertThat(resolution.hasCancel()).isTrue();
            assertThat(resolution.hasRefund()).isFalse();
            assertThat(resolution.hasClaim()).isTrue();
        }

        @Test
        @DisplayName("withRefund 는 hasRefund=true, hasCancel=false")
        void withRefund_HasRefundTrueCancelFalse() {
            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    LegacyOrderStatusMapper.OrderStatusResolution.withRefund(
                            ShipmentStatus.DELIVERED, RefundStatus.REQUESTED);

            assertThat(resolution.hasRefund()).isTrue();
            assertThat(resolution.hasCancel()).isFalse();
            assertThat(resolution.hasClaim()).isTrue();
        }
    }
}
