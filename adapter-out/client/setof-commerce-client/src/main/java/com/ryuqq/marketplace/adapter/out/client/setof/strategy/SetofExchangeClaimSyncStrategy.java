package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.exchange.port.out.client.ExchangeClaimSyncStrategy;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import com.ryuqq.marketplace.domain.exchange.outbox.vo.ExchangeOutboxType;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 교환 클레임 동기화 전략.
 *
 * <p>세토프 자사몰은 교환 전용 API를 제공하지 않습니다. 교환은 내부적으로 반품 + 재주문으로 처리되므로 모든 교환 Outbox는 성공으로 처리합니다.
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
    public OutboxSyncResult execute(ExchangeOutbox outbox, Shop shop) {
        ExchangeOutboxType type = outbox.outboxType();

        log.info(
                "세토프 교환 {} - 세토프 교환 API 미지원, 성공 처리: orderItemId={}",
                type,
                outbox.orderItemIdValue());

        return OutboxSyncResult.success();
    }
}
