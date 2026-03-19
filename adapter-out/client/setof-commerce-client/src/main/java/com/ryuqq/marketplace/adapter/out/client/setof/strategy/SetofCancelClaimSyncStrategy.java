package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import com.ryuqq.marketplace.application.cancel.port.out.client.CancelClaimSyncStrategy;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.cancel.outbox.vo.CancelOutboxType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 취소 클레임 동기화 전략.
 *
 * <p>취소 Outbox 유형에 따라 세토프 API를 호출합니다.
 *
 * <ul>
 *   <li>SELLER_CANCEL: 판매자 취소 → 세토프 취소 승인 API
 *   <li>APPROVE: 구매자 취소 승인 → 세토프 취소 승인 API
 *   <li>REJECT: 취소 거절 → 세토프 취소 거절 API
 * </ul>
 */
@Component
@ConditionalOnProperty(
        prefix = "setof-commerce.claim-sync",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class SetofCancelClaimSyncStrategy implements CancelClaimSyncStrategy {

    private static final Logger log = LoggerFactory.getLogger(SetofCancelClaimSyncStrategy.class);

    @Override
    public OutboxSyncResult execute(CancelOutbox outbox) {
        CancelOutboxType type = outbox.outboxType();

        // TODO: 세토프 취소 API 스펙 확정 후 구현
        log.info("세토프 취소 동기화 (미구현): orderItemId={}, type={}", outbox.orderItemIdValue(), type);

        return OutboxSyncResult.success();
    }
}
