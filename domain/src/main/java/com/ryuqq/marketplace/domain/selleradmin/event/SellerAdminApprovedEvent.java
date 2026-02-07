package com.ryuqq.marketplace.domain.selleradmin.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import java.time.Instant;

/**
 * 셀러 관리자 가입 신청 승인 이벤트.
 *
 * <p>셀러 관리자 가입 신청이 승인되었을 때 발행됩니다. 이 이벤트를 수신하여 인증 서버에 사용자를 등록합니다.
 *
 * @param sellerAdminId 셀러 관리자 ID
 * @param sellerId 셀러 ID
 * @param occurredAt 이벤트 발생 시각
 */
public record SellerAdminApprovedEvent(
        SellerAdminId sellerAdminId, SellerId sellerId, Instant occurredAt) implements DomainEvent {

    public static SellerAdminApprovedEvent of(
            SellerAdminId sellerAdminId, SellerId sellerId, Instant now) {
        return new SellerAdminApprovedEvent(sellerAdminId, sellerId, now);
    }
}
