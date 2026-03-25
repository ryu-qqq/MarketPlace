package com.ryuqq.marketplace.application.legacy.sellicorder.port.out;

import com.ryuqq.marketplace.application.legacy.sellicorder.dto.command.IssueSellicLegacyOrderCommand;

/**
 * 셀릭 주문 luxurydb 저장 Port.
 *
 * <p>단일 트랜잭션으로 payment, orders, shipment, settlement, external_order, interlocking_order,
 * payment_snapshot_shipping_address 등 레거시 테이블에 INSERT합니다.
 */
public interface SellicLegacyOrderPersistencePort {

    /**
     * 셀릭 주문을 luxurydb에 레거시 형식으로 저장합니다.
     *
     * @param command 저장 커맨드
     * @return 생성된 주문 ID (luxurydb orders.ORDER_ID)
     */
    long persist(IssueSellicLegacyOrderCommand command);
}
