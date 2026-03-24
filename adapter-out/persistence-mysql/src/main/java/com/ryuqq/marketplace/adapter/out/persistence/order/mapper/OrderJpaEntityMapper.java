package com.ryuqq.marketplace.adapter.out.persistence.order.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemHistoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.PaymentJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.common.vo.Email;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItemHistory;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.id.OrderItemNumber;
import com.ryuqq.marketplace.domain.order.id.OrderNumber;
import com.ryuqq.marketplace.domain.order.id.PaymentNumber;
import com.ryuqq.marketplace.domain.order.vo.BuyerInfo;
import com.ryuqq.marketplace.domain.order.vo.BuyerName;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderItemPrice;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderReference;
import com.ryuqq.marketplace.domain.order.vo.ExternalProductSnapshot;
import com.ryuqq.marketplace.domain.order.vo.InternalProductReference;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import com.ryuqq.marketplace.domain.order.vo.PaymentInfo;
import com.ryuqq.marketplace.domain.order.vo.PaymentStatus;
import com.ryuqq.marketplace.domain.order.vo.ReceiverInfo;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Order JPA Entity Mapper. */
@Component
public class OrderJpaEntityMapper {

    public OrderJpaEntity toOrderEntity(Order order) {
        BuyerInfo buyer = order.buyerInfo();
        ExternalOrderReference ext = order.externalOrderReference();
        // DB의 orders.status 컬럼은 하위 호환 유지를 위해 고정값("ACTIVE") 저장
        return OrderJpaEntity.create(
                order.idValue(),
                order.orderNumberValue(),
                "ACTIVE",
                buyer.buyerName().value(),
                buyer.email() != null ? buyer.email().value() : null,
                buyer.phoneNumber() != null ? buyer.phoneNumber().value() : null,
                ext.salesChannelId(),
                ext.shopId(),
                ext.externalOrderNo(),
                ext.externalOrderedAt(),
                ext.shopCode(),
                ext.shopName(),
                order.createdAt(),
                order.updatedAt(),
                null);
    }

    public PaymentJpaEntity toPaymentEntity(Order order, String paymentId) {
        PaymentInfo payment = order.paymentInfo();
        String status =
                payment != null && payment.paidAt() != null
                        ? PaymentStatus.COMPLETED.name()
                        : PaymentStatus.PENDING.name();
        String paymentNumber =
                payment != null && payment.paymentNumber() != null
                        ? payment.paymentNumber().value()
                        : null;
        return PaymentJpaEntity.create(
                paymentId,
                order.idValue(),
                paymentNumber,
                status,
                payment != null ? payment.paymentMethod() : null,
                null,
                payment != null ? payment.totalPaymentAmount().value() : 0,
                payment != null ? payment.paidAt() : null,
                null,
                order.createdAt(),
                order.updatedAt());
    }

    public OrderItemJpaEntity toOrderItemEntity(OrderItem item, String orderId) {
        return OrderItemJpaEntity.create(
                item.idValue(),
                item.orderItemNumberValue(),
                orderId,
                item.internalProduct().productGroupId(),
                item.internalProduct().productId(),
                item.internalProduct().skuCode(),
                item.internalProduct().productGroupName(),
                item.internalProduct().brandName(),
                item.internalProduct().sellerName(),
                item.internalProduct().mainImageUrl(),
                item.externalProduct().externalProductId(),
                item.externalProduct().externalOptionId(),
                item.externalProduct().externalProductName(),
                item.externalProduct().externalOptionName(),
                item.externalProduct().externalImageUrl(),
                item.price().unitPrice().value(),
                item.price().quantity(),
                item.price().totalAmount().value(),
                item.price().discountAmount().value(),
                item.price().sellerBurdenDiscountAmount().value(),
                item.price().paymentAmount().value(),
                item.receiverInfo().receiverName(),
                item.receiverInfo().receiverPhone() != null
                        ? item.receiverInfo().receiverPhone().value()
                        : null,
                item.receiverInfo().address() != null
                        ? item.receiverInfo().address().zipcode()
                        : null,
                item.receiverInfo().address() != null
                        ? item.receiverInfo().address().line1()
                        : null,
                item.receiverInfo().address() != null
                        ? item.receiverInfo().address().line2()
                        : null,
                item.receiverInfo().deliveryRequest(),
                item.status().name(),
                Instant.now(),
                Instant.now());
    }

    public List<OrderItemJpaEntity> toOrderItemEntities(List<OrderItem> items, String orderId) {
        return items.stream().map(item -> toOrderItemEntity(item, orderId)).toList();
    }

    public OrderItemHistoryJpaEntity toOrderItemHistoryEntity(OrderItemHistory history) {
        return OrderItemHistoryJpaEntity.create(
                history.id(),
                history.orderItemIdValue(),
                history.fromStatus() != null ? history.fromStatus().name() : null,
                history.toStatus().name(),
                history.changedBy(),
                history.reason(),
                history.changedAt(),
                history.changedAt(),
                history.changedAt());
    }

    public List<OrderItemHistoryJpaEntity> toOrderItemHistoryEntities(
            List<OrderItemHistory> histories) {
        return histories.stream().map(this::toOrderItemHistoryEntity).toList();
    }

    /** Order 내 모든 OrderItem의 histories를 합산하여 엔티티 목록으로 변환합니다. */
    public List<OrderItemHistoryJpaEntity> collectAllItemHistoryEntities(Order order) {
        return order.items().stream()
                .flatMap(item -> item.histories().stream())
                .map(this::toOrderItemHistoryEntity)
                .toList();
    }

    /** Entity → Domain 변환 (Query Phase에서 활용). */
    public Order toDomain(
            OrderJpaEntity entity,
            PaymentJpaEntity paymentEntity,
            List<OrderItemJpaEntity> itemEntities,
            List<OrderItemHistoryJpaEntity> historyEntities) {
        // orderItemId 기준으로 histories 그룹핑
        Map<String, List<OrderItemHistoryJpaEntity>> historiesByItemId =
                historyEntities.stream()
                        .collect(Collectors.groupingBy(OrderItemHistoryJpaEntity::getOrderItemId));

        List<OrderItem> items =
                itemEntities.stream()
                        .map(
                                itemEntity -> {
                                    List<OrderItemHistoryJpaEntity> itemHistories =
                                            historiesByItemId.getOrDefault(
                                                    itemEntity.getId(), List.of());
                                    return toOrderItem(itemEntity, itemHistories);
                                })
                        .toList();

        return Order.reconstitute(
                OrderId.of(entity.getId()),
                OrderNumber.of(entity.getOrderNumber()),
                resolveBuyerInfo(entity),
                resolvePaymentInfo(paymentEntity),
                resolveExternalOrderReference(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                items);
    }

    private BuyerInfo resolveBuyerInfo(OrderJpaEntity entity) {
        return BuyerInfo.of(
                BuyerName.of(entity.getBuyerName()),
                entity.getBuyerEmail() != null ? Email.of(entity.getBuyerEmail()) : null,
                entity.getBuyerPhone() != null ? PhoneNumber.of(entity.getBuyerPhone()) : null);
    }

    private PaymentInfo resolvePaymentInfo(PaymentJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return PaymentInfo.of(
                entity.getPaymentNumber() != null
                        ? PaymentNumber.of(entity.getPaymentNumber())
                        : null,
                entity.getPaymentMethod(),
                Money.of(entity.getPaymentAmount()),
                entity.getPaidAt());
    }

    private ExternalOrderReference resolveExternalOrderReference(OrderJpaEntity entity) {
        return ExternalOrderReference.of(
                entity.getSalesChannelId(),
                entity.getShopId(),
                entity.getShopCode(),
                entity.getShopName(),
                entity.getExternalOrderNo(),
                entity.getExternalOrderedAt());
    }

    public OrderItem toOrderItem(OrderItemJpaEntity entity) {
        return toOrderItem(entity, List.of());
    }

    public OrderItem toOrderItem(
            OrderItemJpaEntity entity, List<OrderItemHistoryJpaEntity> historyEntities) {
        List<OrderItemHistory> histories =
                historyEntities.stream().map(this::toOrderItemHistory).toList();
        return OrderItem.reconstitute(
                OrderItemId.of(entity.getId()),
                OrderItemNumber.of(entity.getOrderItemNumber()),
                InternalProductReference.of(
                        entity.getProductGroupId(),
                        entity.getProductId(),
                        null,
                        null,
                        entity.getSkuCode(),
                        entity.getProductGroupName(),
                        entity.getBrandName(),
                        entity.getSellerName(),
                        entity.getMainImageUrl()),
                ExternalProductSnapshot.of(
                        entity.getExternalProductId(),
                        entity.getExternalOptionId(),
                        entity.getExternalProductName(),
                        entity.getExternalOptionName(),
                        entity.getExternalImageUrl()),
                ExternalOrderItemPrice.of(
                        Money.of(entity.getUnitPrice()),
                        entity.getQuantity(),
                        Money.of(entity.getTotalAmount()),
                        Money.of(entity.getDiscountAmount()),
                        Money.of(entity.getSellerBurdenDiscountAmount()),
                        Money.of(entity.getPaymentAmount())),
                resolveReceiverInfo(entity),
                OrderItemStatus.valueOf(entity.getOrderItemStatus()),
                histories);
    }

    private ReceiverInfo resolveReceiverInfo(OrderItemJpaEntity entity) {
        Address address = null;
        if (entity.getReceiverZipcode() != null && entity.getReceiverAddress() != null) {
            address =
                    Address.of(
                            entity.getReceiverZipcode(),
                            entity.getReceiverAddress(),
                            entity.getReceiverAddressDetail());
        }
        return ReceiverInfo.of(
                entity.getReceiverName(),
                entity.getReceiverPhone() != null
                        ? PhoneNumber.of(entity.getReceiverPhone())
                        : null,
                address,
                entity.getDeliveryRequest());
    }

    private OrderItemHistory toOrderItemHistory(OrderItemHistoryJpaEntity entity) {
        return OrderItemHistory.reconstitute(
                entity.getId(),
                OrderItemId.of(entity.getOrderItemId()),
                entity.getFromStatus() != null
                        ? OrderItemStatus.valueOf(entity.getFromStatus())
                        : null,
                OrderItemStatus.valueOf(entity.getToStatus()),
                entity.getChangedBy(),
                entity.getReason(),
                entity.getChangedAt());
    }
}
