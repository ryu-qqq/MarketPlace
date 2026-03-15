package com.ryuqq.marketplace.adapter.out.persistence.exchange.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeClaimJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeItemJpaEntity;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claim.vo.FeePayer;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeItem;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimNumber;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeItemId;
import com.ryuqq.marketplace.domain.exchange.vo.AmountAdjustment;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReason;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReasonType;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeTarget;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/** 교환 클레임 JPA 엔티티 Mapper. */
@Component
public class ExchangePersistenceMapper {

    public ExchangeClaimJpaEntity toEntity(ExchangeClaim domain) {
        ExchangeTarget target = domain.exchangeTarget();
        AmountAdjustment adjustment = domain.amountAdjustment();
        return ExchangeClaimJpaEntity.create(
                domain.idValue(),
                domain.claimNumberValue(),
                domain.orderId(),
                domain.status().name(),
                domain.reason().reasonType().name(),
                domain.reason().reasonDetail(),
                target != null ? target.productGroupId() : null,
                target != null ? target.productId() : null,
                target != null ? target.skuCode() : null,
                target != null ? target.quantity() : null,
                adjustment != null ? adjustment.originalPrice().value() : null,
                adjustment != null ? adjustment.targetPrice().value() : null,
                adjustment != null ? adjustment.priceDifference().value() : null,
                adjustment != null && adjustment.additionalPaymentRequired(),
                adjustment != null && adjustment.partialRefundRequired(),
                adjustment != null ? adjustment.collectShippingFee().value() : null,
                adjustment != null ? adjustment.reshipShippingFee().value() : null,
                adjustment != null ? adjustment.totalShippingFee().value() : null,
                adjustment != null ? adjustment.shippingFeePayer().name() : null,
                domain.collectShipment() != null ? domain.collectShipment().id().value() : null,
                domain.linkedOrderId(),
                domain.requestedBy(),
                domain.processedBy(),
                domain.requestedAt(),
                domain.processedAt(),
                domain.completedAt(),
                domain.createdAt(),
                domain.updatedAt());
    }

    public ExchangeItemJpaEntity toItemEntity(ExchangeItem item, String exchangeClaimId) {
        return ExchangeItemJpaEntity.create(
                item.idValue(),
                exchangeClaimId,
                item.orderItemId(),
                item.exchangeQty(),
                Instant.now());
    }

    public ExchangeClaim toDomain(
            ExchangeClaimJpaEntity entity,
            List<ExchangeItemJpaEntity> itemEntities,
            ClaimShipment collectShipment) {
        List<ExchangeItem> items =
                itemEntities.stream()
                        .map(
                                i ->
                                        ExchangeItem.reconstitute(
                                                ExchangeItemId.of(i.getId()),
                                                i.getOrderItemId(),
                                                i.getExchangeQty()))
                        .toList();

        ExchangeTarget target = resolveExchangeTarget(entity);
        AmountAdjustment adjustment = resolveAmountAdjustment(entity);

        return ExchangeClaim.reconstitute(
                ExchangeClaimId.of(entity.getId()),
                ExchangeClaimNumber.of(entity.getClaimNumber()),
                entity.getOrderId(),
                ExchangeStatus.valueOf(entity.getExchangeStatus()),
                new ExchangeReason(
                        ExchangeReasonType.valueOf(entity.getReasonType()),
                        entity.getReasonDetail() != null ? entity.getReasonDetail() : ""),
                target,
                adjustment,
                collectShipment,
                entity.getLinkedOrderId(),
                entity.getRequestedBy(),
                entity.getProcessedBy(),
                entity.getRequestedAt(),
                entity.getProcessedAt(),
                entity.getCompletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                items);
    }

    private ExchangeTarget resolveExchangeTarget(ExchangeClaimJpaEntity entity) {
        if (entity.getTargetProductGroupId() == null
                || entity.getTargetProductId() == null
                || entity.getTargetSkuCode() == null
                || entity.getTargetQuantity() == null) {
            return null;
        }
        return new ExchangeTarget(
                entity.getTargetProductGroupId(),
                entity.getTargetProductId(),
                entity.getTargetSkuCode(),
                entity.getTargetQuantity());
    }

    private AmountAdjustment resolveAmountAdjustment(ExchangeClaimJpaEntity entity) {
        if (entity.getOriginalPrice() == null
                || entity.getTargetPrice() == null
                || entity.getShippingFeePayer() == null) {
            return null;
        }
        return new AmountAdjustment(
                Money.of(entity.getOriginalPrice()),
                Money.of(entity.getTargetPrice()),
                Money.of(entity.getPriceDifference() != null ? entity.getPriceDifference() : 0),
                entity.isAdditionalPaymentRequired(),
                entity.isPartialRefundRequired(),
                Money.of(
                        entity.getCollectShippingFee() != null
                                ? entity.getCollectShippingFee()
                                : 0),
                Money.of(entity.getReshipShippingFee() != null ? entity.getReshipShippingFee() : 0),
                Money.of(entity.getTotalShippingFee() != null ? entity.getTotalShippingFee() : 0),
                FeePayer.valueOf(entity.getShippingFeePayer()));
    }
}
