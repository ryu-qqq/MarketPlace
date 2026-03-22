package com.ryuqq.marketplace.adapter.out.persistence.inboundorder.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity.InboundOrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity.InboundOrderJpaEntity;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrderItem;
import com.ryuqq.marketplace.domain.inboundorder.id.InboundOrderId;
import com.ryuqq.marketplace.domain.inboundorder.id.InboundOrderItemId;
import com.ryuqq.marketplace.domain.inboundorder.vo.InboundOrderStatus;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundOrder JPA Entity Mapper. */
@Component
public class InboundOrderJpaEntityMapper {

    public InboundOrderJpaEntity toEntity(InboundOrder order) {
        return InboundOrderJpaEntity.create(
                order.idValue(),
                order.salesChannelId(),
                order.shopId(),
                order.sellerId(),
                order.externalOrderNo(),
                order.externalOrderedAt(),
                order.buyerName(),
                order.buyerEmail(),
                order.buyerPhone(),
                order.paymentMethod(),
                order.totalPaymentAmount(),
                order.paidAt(),
                InboundOrderJpaEntity.Status.valueOf(order.status().name()),
                order.internalOrderId(),
                order.failureReason(),
                order.createdAt(),
                order.updatedAt());
    }

    public InboundOrderItemJpaEntity toItemEntity(InboundOrderItem item, long inboundOrderId) {
        return InboundOrderItemJpaEntity.create(
                item.idValue(),
                inboundOrderId,
                item.externalProductOrderId(),
                item.externalProductId(),
                item.externalOptionId(),
                item.externalProductName(),
                item.externalOptionName(),
                item.externalImageUrl(),
                item.unitPrice(),
                item.quantity(),
                item.totalAmount(),
                item.discountAmount(),
                item.paymentAmount(),
                item.receiverName(),
                item.receiverPhone(),
                item.receiverZipCode(),
                item.receiverAddress(),
                item.receiverAddressDetail(),
                item.deliveryRequest(),
                item.resolvedProductGroupId(),
                item.resolvedProductId(),
                item.resolvedSellerId(),
                item.resolvedBrandId(),
                item.resolvedSkuCode(),
                item.resolvedProductGroupName(),
                item.isMapped(),
                Instant.now(),
                Instant.now());
    }

    public List<InboundOrderItemJpaEntity> toItemEntities(
            List<InboundOrderItem> items, long inboundOrderId) {
        return items.stream().map(item -> toItemEntity(item, inboundOrderId)).toList();
    }

    public InboundOrder toDomain(
            InboundOrderJpaEntity entity, List<InboundOrderItemJpaEntity> itemEntities) {
        List<InboundOrderItem> items = itemEntities.stream().map(this::toItemDomain).toList();

        return InboundOrder.reconstitute(
                InboundOrderId.of(entity.getId()),
                entity.getSalesChannelId(),
                entity.getShopId(),
                entity.getSellerId(),
                entity.getExternalOrderNo(),
                entity.getExternalOrderedAt(),
                entity.getBuyerName(),
                entity.getBuyerEmail(),
                entity.getBuyerPhone(),
                entity.getPaymentMethod(),
                entity.getTotalPaymentAmount(),
                entity.getPaidAt(),
                InboundOrderStatus.valueOf(entity.getStatus().name()),
                entity.getInternalOrderId(),
                entity.getFailureReason(),
                items,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private InboundOrderItem toItemDomain(InboundOrderItemJpaEntity entity) {
        return InboundOrderItem.reconstitute(
                InboundOrderItemId.of(entity.getId()),
                entity.getExternalProductOrderId(),
                entity.getExternalProductId(),
                entity.getExternalOptionId(),
                entity.getExternalProductName(),
                entity.getExternalOptionName(),
                entity.getExternalImageUrl(),
                entity.getUnitPrice(),
                entity.getQuantity(),
                entity.getTotalAmount(),
                entity.getDiscountAmount(),
                entity.getPaymentAmount(),
                entity.getReceiverName(),
                entity.getReceiverPhone(),
                entity.getReceiverZipCode(),
                entity.getReceiverAddress(),
                entity.getReceiverAddressDetail(),
                entity.getDeliveryRequest(),
                entity.getResolvedProductGroupId(),
                entity.getResolvedProductId(),
                entity.getResolvedSellerId(),
                entity.getResolvedBrandId(),
                entity.getResolvedSkuCode(),
                entity.getResolvedProductGroupName(),
                entity.isMapped());
    }
}
