package com.ryuqq.marketplace.application.legacyconversion.dto.bundle;

import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;

/**
 * 레거시 주문 변환 결과 번들.
 *
 * <p>Factory에서 도메인 객체를 조립한 결과를 담습니다.
 * PersistenceFacade에서 하나의 트랜잭션으로 저장합니다.
 *
 * @param order       변환된 Order (항상 존재)
 * @param shipment    배송 정보 (발주확인 이후 상태면 존재, nullable)
 * @param cancel      취소 건이면 Cancel (nullable)
 * @param refundClaim 반품 건이면 RefundClaim (nullable)
 * @param mapping     레거시 → 내부 ID 매핑 (항상 존재)
 */
public record LegacyOrderConversionBundle(
        Order order,
        Shipment shipment,
        Cancel cancel,
        RefundClaim refundClaim,
        LegacyOrderIdMapping mapping) {

    public boolean hasShipment() {
        return shipment != null;
    }

    public boolean hasCancel() {
        return cancel != null;
    }

    public boolean hasRefund() {
        return refundClaim != null;
    }
}
