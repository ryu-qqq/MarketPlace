package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.shipment.port.out.client.ShipmentSyncStrategy;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 배송 상태 동기화 전략.
 *
 * <p>배송 Outbox 유형에 따라 세토프 API를 호출합니다.
 *
 * <ul>
 *   <li>CONFIRM: 발주확인 → 세토프 발주확인 API
 *   <li>SHIP: 발송처리 (운송장 등록) → 세토프 운송장 등록 API
 *   <li>DELIVER: 배송완료 → 세토프 자동 처리
 *   <li>CANCEL: 배송취소 → Cancel Outbox에서 처리
 * </ul>
 */
@Component
@ConditionalOnProperty(
        prefix = "setof-commerce.claim-sync",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class SetofShipmentSyncStrategy implements ShipmentSyncStrategy {

    private static final Logger log = LoggerFactory.getLogger(SetofShipmentSyncStrategy.class);

    @Override
    public OutboxSyncResult execute(ShipmentOutbox outbox) {
        ShipmentOutboxType type = outbox.outboxType();

        switch (type) {
            case CONFIRM -> {
                // TODO: 세토프 발주확인 API 호출
                log.info("세토프 발주확인 (미구현): orderItemId={}", outbox.orderItemIdValue());
            }
            case SHIP -> {
                // TODO: 세토프 운송장 등록 API 호출
                log.info("세토프 운송장 등록 (미구현): orderItemId={}", outbox.orderItemIdValue());
            }
            case DELIVER -> {
                log.info("세토프 배송완료 - 자동 처리: orderItemId={}", outbox.orderItemIdValue());
            }
            case CANCEL -> {
                log.info(
                        "세토프 배송취소 - Cancel Outbox에서 처리: orderItemId={}", outbox.orderItemIdValue());
            }
        }

        return OutboxSyncResult.success();
    }
}
