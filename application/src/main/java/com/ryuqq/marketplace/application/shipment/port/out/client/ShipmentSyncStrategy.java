package com.ryuqq.marketplace.application.shipment.port.out.client;

import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;

/**
 * 배송 상태 동기화 전략 포트.
 *
 * <p>외부 판매채널에 배송 상태(발주확인, 송장등록 등)를 동기화하는 전략 인터페이스입니다. 구현체는 adapter-out 레이어에 위치합니다.
 */
public interface ShipmentSyncStrategy {

    /** 이 전략이 담당하는 판매채널 코드. */
    String channelCode();

    OutboxSyncResult execute(ShipmentOutbox outbox);
}
