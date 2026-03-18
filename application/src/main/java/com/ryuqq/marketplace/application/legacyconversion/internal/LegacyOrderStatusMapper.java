package com.ryuqq.marketplace.application.legacyconversion.internal;

import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;

import org.springframework.stereotype.Component;

/**
 * 레거시 주문 상태 매퍼.
 *
 * <p>레거시 luxurydb orders.ORDER_STATUS 값을 내부 도메인 상태로 변환합니다.
 * 외부 의존성(DB/API 호출) 없는 순수 비즈니스 로직 컴포넌트입니다.
 */
@Component
public class LegacyOrderStatusMapper {

    /**
     * 레거시 ORDER_STATUS를 내부 상태로 매핑합니다.
     *
     * <p>Order에서 상태가 제거되었으므로 배송 상태(deliveryStatus)와 클레임 상태만 매핑합니다.
     *
     * @param legacyStatus luxurydb orders.ORDER_STATUS
     * @return 매핑 결과
     * @throws IllegalArgumentException 매핑 불가능한 상태값인 경우
     */
    public OrderStatusResolution resolve(String legacyStatus) {
        return switch (legacyStatus) {

            // 정상 주문 흐름
            case "ORDER_PROCESSING" ->
                    OrderStatusResolution.normalOrder("READY");
            case "DELIVERY_PENDING" ->
                    OrderStatusResolution.normalOrder("READY");
            case "DELIVERY_PROCESSING" ->
                    OrderStatusResolution.normalOrder("IN_TRANSIT");
            case "DELIVERY_COMPLETED", "DELIVERY_COMPLETE" ->
                    OrderStatusResolution.normalOrder("DELIVERED");
            case "ORDER_COMPLETED" ->
                    OrderStatusResolution.normalOrder("DELIVERED");
            case "SETTLEMENT_PROCESSING", "SETTLEMENT_COMPLETED" ->
                    OrderStatusResolution.normalOrder("DELIVERED");

            // 취소
            case "SALE_CANCELLED", "SALE_CANCELLED_COMPLETED", "CANCEL_REQUEST_COMPLETED" ->
                    OrderStatusResolution.withCancel("DELIVERED", CancelStatus.COMPLETED);
            case "CANCEL_REQUEST_CONFIRMED" ->
                    OrderStatusResolution.withCancel("DELIVERED", CancelStatus.APPROVED);

            // 반품
            case "RETURN_REQUEST" ->
                    OrderStatusResolution.withRefund("DELIVERED", RefundStatus.REQUESTED);
            case "RETURN_REQUEST_COMPLETED" ->
                    OrderStatusResolution.withRefund("DELIVERED", RefundStatus.COMPLETED);
            case "RETURN_REQUEST_REJECTED" ->
                    OrderStatusResolution.withRefund("DELIVERED", RefundStatus.REJECTED);
            case "RETURN_REQUEST_CONFIRMED" ->
                    OrderStatusResolution.withRefund("DELIVERED", RefundStatus.COLLECTING);

            default ->
                    throw new IllegalArgumentException(
                            "매핑 불가능한 레거시 주문 상태: " + legacyStatus);
        };
    }

    /**
     * 이관 대상 여부를 판별합니다.
     *
     * <p>ORDER_FAILED 상태의 주문은 이관 제외 대상입니다.
     *
     * @param legacyStatus luxurydb orders.ORDER_STATUS
     * @return 이관 대상이면 true
     */
    public boolean isEligibleForMigration(String legacyStatus) {
        return !"ORDER_FAILED".equals(legacyStatus);
    }

    /**
     * 레거시 주문 상태 매핑 결과.
     *
     * <p>Order에서 상태가 제거되었으므로 deliveryStatus와 클레임 상태만 관리합니다.
     *
     * @param deliveryStatus 배송 상태 문자열
     * @param claimType     클레임 유형 (없으면 null)
     * @param cancelStatus  취소 상태 (없으면 null)
     * @param refundStatus  반품 상태 (없으면 null)
     */
    public record OrderStatusResolution(
            String deliveryStatus,
            ClaimType claimType,
            CancelStatus cancelStatus,
            RefundStatus refundStatus) {

        static OrderStatusResolution normalOrder(String deliveryStatus) {
            return new OrderStatusResolution(deliveryStatus, null, null, null);
        }

        static OrderStatusResolution withCancel(String deliveryStatus, CancelStatus cancelStatus) {
            return new OrderStatusResolution(deliveryStatus, ClaimType.CANCEL, cancelStatus, null);
        }

        static OrderStatusResolution withRefund(String deliveryStatus, RefundStatus refundStatus) {
            return new OrderStatusResolution(deliveryStatus, ClaimType.REFUND, null, refundStatus);
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
