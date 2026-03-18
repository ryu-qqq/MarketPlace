package com.ryuqq.marketplace.application.exchange.port.out.client;

import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;

/**
 * 교환 클레임 동기화 전략 포트.
 *
 * <p>외부 판매채널에 교환 상태를 동기화하는 전략 인터페이스입니다. 구현체는 adapter-out 레이어에 위치합니다.
 */
public interface ExchangeClaimSyncStrategy {

    OutboxSyncResult execute(ExchangeOutbox outbox);
}
