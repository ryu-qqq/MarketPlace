package com.ryuqq.marketplace.application.refund.port.out.client;

import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;

/**
 * 환불 클레임 동기화 전략 포트.
 *
 * <p>외부 판매채널에 환불 상태를 동기화하는 전략 인터페이스입니다. 구현체는 adapter-out 레이어에 위치합니다.
 */
public interface RefundClaimSyncStrategy {

    OutboxSyncResult execute(RefundOutbox outbox, Shop shop);
}
