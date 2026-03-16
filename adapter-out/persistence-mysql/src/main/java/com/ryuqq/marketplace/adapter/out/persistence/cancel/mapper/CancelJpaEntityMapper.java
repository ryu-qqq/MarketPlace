package com.ryuqq.marketplace.adapter.out.persistence.cancel.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.CancelItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.CancelJpaEntity;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.aggregate.CancelItem;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.id.CancelItemId;
import com.ryuqq.marketplace.domain.cancel.id.CancelNumber;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReason;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReasonType;
import com.ryuqq.marketplace.domain.cancel.vo.CancelRefundInfo;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import com.ryuqq.marketplace.domain.common.vo.Money;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Cancel JPA Entity Mapper.
 *
 * <p>도메인 객체와 JPA 엔티티 간의 변환을 담당합니다. cancelItems는 별도 CancelItemJpaEntity 목록으로 변환됩니다.
 */
@Component
public class CancelJpaEntityMapper {

    /**
     * 도메인 객체를 JPA 엔티티로 변환합니다.
     *
     * @param domain Cancel 도메인 객체
     * @return CancelJpaEntity
     */
    public CancelJpaEntity toEntity(Cancel domain) {
        CancelRefundInfo refundInfo = domain.refundInfo();

        return CancelJpaEntity.create(
                domain.idValue(),
                domain.cancelNumberValue(),
                domain.orderId(),
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

    /**
     * 취소 상품 도메인 목록을 JPA 엔티티 목록으로 변환합니다.
     *
     * @param cancelId 취소 ID
     * @param cancelItems 취소 상품 도메인 목록
     * @return CancelItemJpaEntity 목록
     */
    public List<CancelItemJpaEntity> toItemEntities(String cancelId, List<CancelItem> cancelItems) {
        Instant now = Instant.now();
        return cancelItems.stream().map(item -> toItemEntity(cancelId, item, now)).toList();
    }

    /**
     * 취소 상품 도메인을 JPA 엔티티로 변환합니다.
     *
     * @param cancelId 취소 ID
     * @param item 취소 상품 도메인
     * @param createdAt 생성 일시
     * @return CancelItemJpaEntity
     */
    private CancelItemJpaEntity toItemEntity(String cancelId, CancelItem item, Instant createdAt) {
        Long itemId = item.id().isNew() ? null : item.idValue();
        return CancelItemJpaEntity.create(
                itemId, cancelId, String.valueOf(item.orderItemId()), item.cancelQty(), createdAt);
    }

    /**
     * JPA 엔티티를 도메인 객체로 변환합니다.
     *
     * @param entity CancelJpaEntity
     * @param itemEntities 취소 상품 엔티티 목록
     * @return Cancel 도메인 객체
     */
    public Cancel toDomain(CancelJpaEntity entity, List<CancelItemJpaEntity> itemEntities) {
        List<CancelItem> cancelItems = itemEntities.stream().map(this::toCancelItemDomain).toList();

        CancelRefundInfo refundInfo = resolveRefundInfo(entity);
        CancelReason reason =
                new CancelReason(
                        CancelReasonType.valueOf(entity.getReasonType()), entity.getReasonDetail());

        return Cancel.reconstitute(
                CancelId.of(entity.getId()),
                CancelNumber.of(entity.getCancelNumber()),
                entity.getOrderId(),
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
                entity.getUpdatedAt(),
                cancelItems);
    }

    private CancelItem toCancelItemDomain(CancelItemJpaEntity entity) {
        return CancelItem.reconstitute(
                CancelItemId.of(entity.getId()), Long.parseLong(entity.getOrderItemId()), entity.getCancelQty());
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
