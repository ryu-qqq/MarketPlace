package com.ryuqq.marketplace.domain.sellerapplication.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import java.time.Instant;

/**
 * 셀러 입점 신청 거절 이벤트.
 *
 * <p>입점 신청이 거절되었을 때 발행됩니다.
 *
 * @param applicationId 입점 신청 ID
 * @param rejectionReason 거절 사유
 * @param processedBy 처리자 식별자 (UUIDv7 또는 이메일)
 * @param occurredAt 이벤트 발생 시각
 */
public record SellerApplicationRejectedEvent(
        SellerApplicationId applicationId,
        String rejectionReason,
        String processedBy,
        Instant occurredAt)
        implements DomainEvent {

    public static SellerApplicationRejectedEvent of(
            SellerApplicationId applicationId,
            String rejectionReason,
            String processedBy,
            Instant now) {
        return new SellerApplicationRejectedEvent(applicationId, rejectionReason, processedBy, now);
    }
}
