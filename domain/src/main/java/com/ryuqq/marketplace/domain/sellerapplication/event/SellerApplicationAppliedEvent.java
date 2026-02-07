package com.ryuqq.marketplace.domain.sellerapplication.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import java.time.Instant;

/**
 * 셀러 입점 신청 이벤트.
 *
 * <p>신규 입점 신청이 접수되었을 때 발행됩니다.
 *
 * @param applicationId 입점 신청 ID
 * @param sellerName 셀러명
 * @param registrationNumber 사업자등록번호
 * @param occurredAt 이벤트 발생 시각
 */
public record SellerApplicationAppliedEvent(
        SellerApplicationId applicationId,
        String sellerName,
        String registrationNumber,
        Instant occurredAt)
        implements DomainEvent {

    public static SellerApplicationAppliedEvent of(
            SellerApplicationId applicationId,
            String sellerName,
            String registrationNumber,
            Instant now) {
        return new SellerApplicationAppliedEvent(
                applicationId, sellerName, registrationNumber, now);
    }
}
