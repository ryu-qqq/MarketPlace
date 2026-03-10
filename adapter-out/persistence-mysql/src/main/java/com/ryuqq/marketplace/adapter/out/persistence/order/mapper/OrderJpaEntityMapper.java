package com.ryuqq.marketplace.adapter.out.persistence.order.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderHistoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.PaymentJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.common.vo.Email;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.aggregate.OrderHistory;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderHistoryId;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.id.OrderNumber;
import com.ryuqq.marketplace.domain.order.id.PaymentNumber;
import com.ryuqq.marketplace.domain.order.vo.BuyerInfo;
import com.ryuqq.marketplace.domain.order.vo.BuyerName;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderItemPrice;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderReference;
import com.ryuqq.marketplace.domain.order.vo.ExternalProductSnapshot;
import com.ryuqq.marketplace.domain.order.vo.InternalProductReference;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
import com.ryuqq.marketplace.domain.order.vo.PaymentInfo;
import com.ryuqq.marketplace.domain.order.vo.PaymentStatus;
import com.ryuqq.marketplace.domain.order.vo.ReceiverInfo;
import java.util.List;
import org.springframework.stereotype.Component;

/** Order JPA Entity Mapper. */
@Component
public class OrderJpaEntityMapper {

    public OrderJpaEntity toOrderEntity(Order order) {
        BuyerInfo buyer = order.buyerInfo();
        ExternalOrderReference ext = order.externalOrderReference();
        return OrderJpaEntity.create(
                order.idValue(),
                order.orderNumberValue(),
                order.status().name(),
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
                orderId,
                item.internalProduct().productGroupId(),
                item.internalProduct().productId(),
                item.internalProduct().sellerId(),
                item.internalProduct().brandId(),
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
                "READY",
                null,
                null,
                null,
                0,
                0,
                0,
                0,
                0,
                null,
                null,
                null,
                null);
    }

    public List<OrderItemJpaEntity> toOrderItemEntities(List<OrderItem> items, String orderId) {
        return items.stream().map(item -> toOrderItemEntity(item, orderId)).toList();
    }

    public OrderHistoryJpaEntity toOrderHistoryEntity(OrderHistory history) {
        return OrderHistoryJpaEntity.create(
                history.idValue(),
                history.orderId().value(),
                history.fromStatus() != null ? history.fromStatus().name() : null,
                history.toStatus().name(),
                history.changedBy(),
                history.reason(),
                history.changedAt(),
                null,
                null);
    }

    public List<OrderHistoryJpaEntity> toOrderHistoryEntities(List<OrderHistory> histories) {
        return histories.stream().map(this::toOrderHistoryEntity).toList();
    }

    /** Entity → Domain 변환 (Query Phase에서 활용 예정). */
    public Order toDomain(
            OrderJpaEntity entity,
            PaymentJpaEntity paymentEntity,
            List<OrderItemJpaEntity> itemEntities,
            List<OrderHistoryJpaEntity> historyEntities) {
        return Order.reconstitute(
                OrderId.of(entity.getId()),
                OrderNumber.of(entity.getOrderNumber()),
                OrderStatus.valueOf(entity.getStatus()),
                resolveBuyerInfo(entity),
                resolvePaymentInfo(paymentEntity),
                resolveExternalOrderReference(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                itemEntities.stream().map(this::toOrderItem).toList(),
                historyEntities.stream().map(this::toOrderHistory).toList());
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

    private OrderItem toOrderItem(OrderItemJpaEntity entity) {
        return OrderItem.reconstitute(
                OrderItemId.of(entity.getId()),
                InternalProductReference.of(
                        entity.getProductGroupId(),
                        entity.getProductId(),
                        entity.getSellerId(),
                        entity.getBrandId(),
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
                        Money.of(entity.getPaymentAmount())),
                resolveReceiverInfo(entity),
                null,
                null);
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

    private OrderHistory toOrderHistory(OrderHistoryJpaEntity entity) {
        return OrderHistory.reconstitute(
                OrderHistoryId.of(entity.getId()),
                OrderId.of(entity.getOrderId()),
                entity.getFromStatus() != null ? OrderStatus.valueOf(entity.getFromStatus()) : null,
                OrderStatus.valueOf(entity.getToStatus()),
                entity.getChangedBy(),
                entity.getReason(),
                entity.getChangedAt());
    }
}
