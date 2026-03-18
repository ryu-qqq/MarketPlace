package com.ryuqq.marketplace.adapter.out.persistence.exchange.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeClaimJpaEntity;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claim.vo.FeePayer;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimNumber;
import com.ryuqq.marketplace.domain.exchange.vo.AmountAdjustment;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeOption;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReason;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReasonType;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.vo.HoldInfo;
import org.springframework.stereotype.Component;

/** 교환 클레임 JPA 엔티티 Mapper. */
@Component
public class ExchangePersistenceMapper {

    public ExchangeClaimJpaEntity toEntity(ExchangeClaim domain) {
        ExchangeOption option = domain.exchangeOption();
        AmountAdjustment adjustment = domain.amountAdjustment();
        HoldInfo holdInfo = domain.holdInfo();
        return ExchangeClaimJpaEntity.create(
                domain.idValue(),
                domain.claimNumberValue(),
                domain.orderItemIdValue(),
                domain.sellerId(),
                domain.exchangeQty(),
                domain.status().name(),
                domain.reason().reasonType().name(),
                domain.reason().reasonDetail(),
                option != null ? option.originalProductId() : null,
                option != null ? option.originalSkuCode() : null,
                option != null ? option.targetProductGroupId() : null,
                option != null ? option.targetProductId() : null,
                option != null ? option.targetSkuCode() : null,
                option != null ? option.quantity() : null,
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
                holdInfo != null ? holdInfo.holdReason() : null,
                holdInfo != null ? holdInfo.holdAt() : null,
                domain.createdAt(),
                domain.updatedAt());
    }

    public ExchangeClaim toDomain(ExchangeClaimJpaEntity entity, ClaimShipment collectShipment) {
        ExchangeOption option = resolveExchangeOption(entity);
        AmountAdjustment adjustment = resolveAmountAdjustment(entity);

        HoldInfo holdInfo = resolveHoldInfo(entity);

        return ExchangeClaim.reconstitute(
                ExchangeClaimId.of(entity.getId()),
                ExchangeClaimNumber.of(entity.getClaimNumber()),
                OrderItemId.of(entity.getOrderItemId()),
                entity.getSellerId(),
                entity.getExchangeQty(),
                ExchangeStatus.valueOf(entity.getExchangeStatus()),
                new ExchangeReason(
                        ExchangeReasonType.valueOf(entity.getReasonType()),
                        entity.getReasonDetail() != null ? entity.getReasonDetail() : ""),
                option,
                adjustment,
                collectShipment,
                holdInfo,
                entity.getLinkedOrderId(),
                entity.getRequestedBy(),
                entity.getProcessedBy(),
                entity.getRequestedAt(),
                entity.getProcessedAt(),
                entity.getCompletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private ExchangeOption resolveExchangeOption(ExchangeClaimJpaEntity entity) {
        if (entity.getTargetProductGroupId() == null
                || entity.getTargetProductId() == null
                || entity.getTargetSkuCode() == null
                || entity.getTargetQuantity() == null
                || entity.getOriginalSkuCode() == null) {
            return null;
        }
        return new ExchangeOption(
                entity.getOriginalProductId() != null ? entity.getOriginalProductId() : 0L,
                entity.getOriginalSkuCode(),
                entity.getTargetProductGroupId(),
                entity.getTargetProductId(),
                entity.getTargetSkuCode(),
                entity.getTargetQuantity());
    }

    private HoldInfo resolveHoldInfo(ExchangeClaimJpaEntity entity) {
        if (entity.getHoldReason() == null || entity.getHoldAt() == null) {
            return null;
        }
        return HoldInfo.of(entity.getHoldReason(), entity.getHoldAt());
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
