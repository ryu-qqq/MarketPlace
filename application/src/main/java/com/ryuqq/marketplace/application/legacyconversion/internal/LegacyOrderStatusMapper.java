package com.ryuqq.marketplace.application.legacyconversion.internal;

import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 상태 매퍼.
 *
 * <p>레거시 luxurydb orders.ORDER_STATUS 값을 내부 도메인 상태로 변환합니다. 외부 의존성(DB/API 호출) 없는 순수 비즈니스 로직 컴포넌트입니다.
 */
@Component
public class LegacyOrderStatusMapper {

    public OrderStatusResolution resolve(String legacyStatus) {
        return switch (legacyStatus) {

                // 정상 주문 흐름
            case "ORDER_PROCESSING" -> OrderStatusResolution.normalOrder(null);
            case "DELIVERY_PENDING" -> OrderStatusResolution.normalOrder(ShipmentStatus.READY);
            case "DELIVERY_PROCESSING" ->
                    OrderStatusResolution.normalOrder(ShipmentStatus.IN_TRANSIT);
            case "ORDER_COMPLETED" -> OrderStatusResolution.normalOrder(ShipmentStatus.READY);
            case "DELIVERY_COMPLETED",
                            "DELIVERY_COMPLETE",
                            "SETTLEMENT_PROCESSING",
                            "SETTLEMENT_COMPLETED" ->
                    OrderStatusResolution.normalOrder(ShipmentStatus.DELIVERED);

                // 취소 (발송 전 취소는 Shipment 없음, 발송 후 취소는 DELIVERED)
            case "SALE_CANCELLED", "SALE_CANCELLED_COMPLETED", "CANCEL_REQUEST_COMPLETED" ->
                    OrderStatusResolution.withCancel(
                            ShipmentStatus.DELIVERED, CancelStatus.COMPLETED);
            case "CANCEL_REQUEST_CONFIRMED" ->
                    OrderStatusResolution.withCancel(
                            ShipmentStatus.DELIVERED, CancelStatus.APPROVED);

                // 반품 (배송 완료 후 반품이므로 Shipment DELIVERED)
            case "RETURN_REQUEST" ->
                    OrderStatusResolution.withRefund(
                            ShipmentStatus.DELIVERED, RefundStatus.REQUESTED);
            case "RETURN_REQUEST_COMPLETED" ->
                    OrderStatusResolution.withRefund(
                            ShipmentStatus.DELIVERED, RefundStatus.COMPLETED);
            case "RETURN_REQUEST_REJECTED" ->
                    OrderStatusResolution.withRefund(
                            ShipmentStatus.DELIVERED, RefundStatus.REJECTED);
            case "RETURN_REQUEST_CONFIRMED" ->
                    OrderStatusResolution.withRefund(
                            ShipmentStatus.DELIVERED, RefundStatus.COLLECTING);

            default -> throw new IllegalArgumentException("매핑 불가능한 레거시 주문 상태: " + legacyStatus);
        };
    }

    public boolean isEligibleForMigration(String legacyStatus) {
        return !"ORDER_FAILED".equals(legacyStatus);
    }

    /**
     * 주문 상태 매핑 결과.
     *
     * @param shipmentStatus 배송 상태 (null이면 Shipment 생성 불필요 — ORDER_PROCESSING 등)
     * @param claimType 클레임 유형 (없으면 null)
     * @param cancelStatus 취소 상태 (없으면 null)
     * @param refundStatus 반품 상태 (없으면 null)
     */
    public record OrderStatusResolution(
            ShipmentStatus shipmentStatus,
            ClaimType claimType,
            CancelStatus cancelStatus,
            RefundStatus refundStatus) {

        static OrderStatusResolution normalOrder(ShipmentStatus shipmentStatus) {
            return new OrderStatusResolution(shipmentStatus, null, null, null);
        }

        static OrderStatusResolution withCancel(
                ShipmentStatus shipmentStatus, CancelStatus cancelStatus) {
            return new OrderStatusResolution(shipmentStatus, ClaimType.CANCEL, cancelStatus, null);
        }

        static OrderStatusResolution withRefund(
                ShipmentStatus shipmentStatus, RefundStatus refundStatus) {
            return new OrderStatusResolution(shipmentStatus, ClaimType.REFUND, null, refundStatus);
        }

        public boolean needsShipment() {
            return shipmentStatus != null;
        }

        public boolean hasClaim() {
            return claimType != null;
        }

        public boolean hasCancel() {
            return claimType == ClaimType.CANCEL;
        }

        public boolean hasRefund() {
            return claimType == ClaimType.REFUND;
        }
    }

    /** 클레임 유형. */
    public enum ClaimType {
        CANCEL,
        REFUND
    }
}
