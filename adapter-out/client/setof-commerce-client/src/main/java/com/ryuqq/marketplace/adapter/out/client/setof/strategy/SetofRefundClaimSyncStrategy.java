package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.refund.port.out.client.RefundClaimSyncStrategy;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 환불(반품) 클레임 동기화 전략.
 *
 * <p>환불 Outbox 유형에 따라 세토프 API를 호출합니다.
 *
 * <ul>
 *   <li>APPROVE: 반품 승인 → 세토프 반품 승인 API
 *   <li>REJECT: 반품 거절 → 세토프 반품 거절 API
 *   <li>REQUEST, COLLECT, COMPLETE: 내부 상태 변경만 → 성공 처리
 *   <li>HOLD, RELEASE_HOLD: 세토프 미지원 → 성공 처리
 * </ul>
 */
@Component
@ConditionalOnProperty(
        prefix = "setof-commerce.claim-sync",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class SetofRefundClaimSyncStrategy implements RefundClaimSyncStrategy {

    private static final Logger log = LoggerFactory.getLogger(SetofRefundClaimSyncStrategy.class);

    @Override
    public OutboxSyncResult execute(RefundOutbox outbox) {
        RefundOutboxType type = outbox.outboxType();

        switch (type) {
            case APPROVE -> {
                // TODO: 세토프 반품 승인 API 호출
                log.info("세토프 반품 승인 (미구현): orderItemId={}", outbox.orderItemIdValue());
            }
            case REJECT -> {
                // TODO: 세토프 반품 거절 API 호출
                log.info("세토프 반품 거절 (미구현): orderItemId={}", outbox.orderItemIdValue());
            }
            case REQUEST, COLLECT, COMPLETE -> {
                log.info("세토프 환불 {} - 내부 처리만: orderItemId={}", type, outbox.orderItemIdValue());
            }
            case HOLD, RELEASE_HOLD -> {
                // 세토프는 보류/해제 미지원 → 성공 처리
                log.info("세토프 환불 {} - 미지원 기능, 스킵: orderItemId={}", type, outbox.orderItemIdValue());
            }
        }

        return OutboxSyncResult.success();
    }
}
