package com.ryuqq.marketplace.adapter.out.persistence.cancel.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.CancelJpaEntity;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.id.CancelNumber;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReason;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReasonType;
import com.ryuqq.marketplace.domain.cancel.vo.CancelRefundInfo;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import org.springframework.stereotype.Component;

/**
 * Cancel JPA Entity Mapper.
 *
 * <p>도메인 객체와 JPA 엔티티 간의 변환을 담당합니다.
 */
@Component
public class CancelJpaEntityMapper {

    public CancelJpaEntity toEntity(Cancel domain) {
        CancelRefundInfo refundInfo = domain.refundInfo();

        return CancelJpaEntity.create(
                domain.idValue(),
                domain.cancelNumberValue(),
                domain.orderItemIdValue(),
                domain.sellerId(),
                domain.cancelQty(),
                domain.type().name(),
                domain.status().name(),
                domain.reason().reasonType().name(),
                domain.reason().reasonDetail(),
                refundInfo != null ? refundInfo.refundAmount().value() : null,
                refundInfo != null ? refundInfo.refundMethod() : null,
                refundInfo != null ? refundInfo.refundStatus() : null,
                refundInfo != null ? refundInfo.refundedAt() : null,
                refundInfo != null ? refundInfo.pgRefundId() : null,
                domain.requestedBy(),
                domain.processedBy(),
                domain.requestedAt(),
                domain.processedAt(),
                domain.completedAt(),
                domain.createdAt(),
                domain.updatedAt());
    }

    public Cancel toDomain(CancelJpaEntity entity) {
        CancelRefundInfo refundInfo = resolveRefundInfo(entity);
        CancelReason reason =
                new CancelReason(
                        CancelReasonType.valueOf(entity.getReasonType()), entity.getReasonDetail());

        return Cancel.reconstitute(
                CancelId.of(entity.getId()),
                CancelNumber.of(entity.getCancelNumber()),
                OrderItemId.of(entity.getOrderItemId()),
                entity.getSellerId(),
                entity.getCancelQty(),
                CancelType.valueOf(entity.getCancelType()),
                CancelStatus.valueOf(entity.getCancelStatus()),
                reason,
                refundInfo,
                entity.getRequestedBy(),
                entity.getProcessedBy(),
                entity.getRequestedAt(),
                entity.getProcessedAt(),
                entity.getCompletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private CancelRefundInfo resolveRefundInfo(CancelJpaEntity entity) {
        if (entity.getRefundAmount() == null) {
            return null;
        }
        return CancelRefundInfo.of(
                Money.of(entity.getRefundAmount()),
                entity.getRefundMethod(),
                entity.getRefundStatus(),
                entity.getRefundedAt(),
                entity.getPgRefundId());
    }
}
