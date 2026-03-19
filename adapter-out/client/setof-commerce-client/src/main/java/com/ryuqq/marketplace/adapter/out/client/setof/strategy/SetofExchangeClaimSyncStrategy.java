package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.exchange.port.out.client.ExchangeClaimSyncStrategy;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import com.ryuqq.marketplace.domain.exchange.outbox.vo.ExchangeOutboxType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 교환 클레임 동기화 전략.
 *
 * <p>교환 Outbox 유형에 따라 세토프 API를 호출합니다.
 *
 * <ul>
 *   <li>COLLECT: 수거 완료 → 내부 처리만
 *   <li>SHIP: 재배송 → 세토프 교환 재배송 API (운송장 등록)
 *   <li>REJECT: 교환 거절 → 세토프 교환 거절 API
 *   <li>HOLD, RELEASE_HOLD: 세토프 미지원 → 성공 처리
 * </ul>
 */
@Component
@ConditionalOnProperty(
        prefix = "setof-commerce.claim-sync",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class SetofExchangeClaimSyncStrategy implements ExchangeClaimSyncStrategy {

    private static final Logger log = LoggerFactory.getLogger(SetofExchangeClaimSyncStrategy.class);

    @Override
    public OutboxSyncResult execute(ExchangeOutbox outbox) {
        ExchangeOutboxType type = outbox.outboxType();

        switch (type) {
            case COLLECT -> {
                log.info("세토프 교환 수거 완료 - 내부 처리만: orderItemId={}", outbox.orderItemIdValue());
            }
            case SHIP -> {
                // TODO: 세토프 교환 재배송 API 호출 (운송장 등록)
                log.info("세토프 교환 재배송 (미구현): orderItemId={}", outbox.orderItemIdValue());
            }
            case REJECT -> {
                // TODO: 세토프 교환 거절 API 호출
                log.info("세토프 교환 거절 (미구현): orderItemId={}", outbox.orderItemIdValue());
            }
            case HOLD, RELEASE_HOLD -> {
                // 세토프는 보류/해제 미지원 → 성공 처리
                log.info("세토프 교환 {} - 미지원 기능, 스킵: orderItemId={}", type, outbox.orderItemIdValue());
            }
        }

        return OutboxSyncResult.success();
    }
}
