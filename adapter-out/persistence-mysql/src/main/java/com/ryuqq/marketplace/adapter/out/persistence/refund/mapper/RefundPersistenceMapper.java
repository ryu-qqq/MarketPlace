package com.ryuqq.marketplace.adapter.out.persistence.refund.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.RefundClaimJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.RefundItemJpaEntity;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundItem;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimNumber;
import com.ryuqq.marketplace.domain.refund.id.RefundItemId;
import com.ryuqq.marketplace.domain.refund.vo.HoldInfo;
import com.ryuqq.marketplace.domain.refund.vo.RefundInfo;
import com.ryuqq.marketplace.domain.refund.vo.RefundReason;
import com.ryuqq.marketplace.domain.refund.vo.RefundReasonType;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/** 환불 클레임 JPA 엔티티 Mapper. */
@Component
public class RefundPersistenceMapper {

    public RefundClaimJpaEntity toEntity(RefundClaim domain) {
        RefundInfo refundInfo = domain.refundInfo();
        return RefundClaimJpaEntity.create(
                domain.idValue(),
                domain.claimNumberValue(),
                domain.orderId(),
                domain.status().name(),
                domain.reason().reasonType().name(),
                domain.reason().reasonDetail(),
                refundInfo != null ? refundInfo.originalAmount().value() : null,
                refundInfo != null ? refundInfo.finalAmount().value() : null,
                refundInfo != null ? refundInfo.deductionAmount().value() : null,
                refundInfo != null ? refundInfo.deductionReason() : null,
                refundInfo != null ? refundInfo.refundMethod() : null,
                refundInfo != null ? refundInfo.refundedAt() : null,
                domain.collectShipment() != null ? domain.collectShipment().id().value() : null,
                domain.holdInfo() != null ? domain.holdInfo().holdReason() : null,
                domain.holdInfo() != null ? domain.holdInfo().holdAt() : null,
                domain.requestedBy(),
                domain.processedBy(),
                domain.requestedAt(),
                domain.processedAt(),
                domain.completedAt(),
                domain.createdAt(),
                domain.updatedAt());
    }

    public RefundItemJpaEntity toItemEntity(RefundItem item, String refundClaimId) {
        return RefundItemJpaEntity.create(
                item.idValue(),
                refundClaimId,
                String.valueOf(item.orderItemId()),
                item.refundQty(),
                java.time.Instant.now());
    }

    public RefundClaim toDomain(
            RefundClaimJpaEntity entity,
            List<RefundItemJpaEntity> itemEntities,
            ClaimShipment collectShipment) {
        List<RefundItem> items =
                itemEntities.stream()
                        .map(
                                i ->
                                        RefundItem.reconstitute(
                                                RefundItemId.of(i.getId()),
                                                Long.parseLong(i.getOrderItemId()),
                                                i.getRefundQty()))
                        .toList();

        RefundInfo refundInfo = resolveRefundInfo(entity);
        HoldInfo holdInfo = resolveHoldInfo(entity);

        return RefundClaim.reconstitute(
                RefundClaimId.of(entity.getId()),
                RefundClaimNumber.of(entity.getClaimNumber()),
                entity.getOrderId(),
                RefundStatus.valueOf(entity.getRefundStatus()),
                RefundReason.of(
                        RefundReasonType.valueOf(entity.getReasonType()), entity.getReasonDetail()),
                refundInfo,
                collectShipment,
                holdInfo,
                entity.getRequestedBy(),
                entity.getProcessedBy(),
                entity.getRequestedAt(),
                entity.getProcessedAt(),
                entity.getCompletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                items);
    }

    private RefundInfo resolveRefundInfo(RefundClaimJpaEntity entity) {
        if (entity.getOriginalAmount() == null
                && entity.getFinalAmount() == null
                && entity.getDeductionAmount() == null) {
            return null;
        }
        return RefundInfo.of(
                Money.of(entity.getOriginalAmount() != null ? entity.getOriginalAmount() : 0),
                Money.of(entity.getFinalAmount() != null ? entity.getFinalAmount() : 0),
                Money.of(entity.getDeductionAmount() != null ? entity.getDeductionAmount() : 0),
                entity.getDeductionReason(),
                entity.getRefundMethod(),
                entity.getRefundedAt());
    }

    private HoldInfo resolveHoldInfo(RefundClaimJpaEntity entity) {
        if (entity.getHoldReason() == null || entity.getHoldAt() == null) {
            return null;
        }
        return HoldInfo.of(entity.getHoldReason(), entity.getHoldAt());
    }
}
