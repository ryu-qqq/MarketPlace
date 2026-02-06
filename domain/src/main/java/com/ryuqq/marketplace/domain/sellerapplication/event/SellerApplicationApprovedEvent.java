package com.ryuqq.marketplace.domain.sellerapplication.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import java.time.Instant;

/**
 * 셀러 입점 신청 승인 이벤트.
 *
 * <p>입점 신청이 승인되었을 때 발행됩니다.
 *
 * @param applicationId 입점 신청 ID
 * @param approvedSellerId 승인 후 생성된 셀러 ID
 * @param processedBy 처리자 식별자 (UUIDv7 또는 이메일)
 * @param occurredAt 이벤트 발생 시각
 */
public record SellerApplicationApprovedEvent(
        SellerApplicationId applicationId,
        SellerId approvedSellerId,
        String processedBy,
        Instant occurredAt)
        implements DomainEvent {

    public static SellerApplicationApprovedEvent of(
            SellerApplicationId applicationId,
            SellerId approvedSellerId,
            String processedBy,
            Instant now) {
        return new SellerApplicationApprovedEvent(
                applicationId, approvedSellerId, processedBy, now);
    }
}
