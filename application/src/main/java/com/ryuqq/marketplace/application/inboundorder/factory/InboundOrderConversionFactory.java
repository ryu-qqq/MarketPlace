package com.ryuqq.marketplace.application.inboundorder.factory;

import com.ryuqq.marketplace.application.order.dto.command.CreateOrderCommand;
import com.ryuqq.marketplace.application.order.dto.command.CreateOrderItemCommand;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrderItem;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundOrder → CreateOrderCommand 변환 팩토리. */
@Component
public class InboundOrderConversionFactory {

    private final ShopReadManager shopReadManager;

    public InboundOrderConversionFactory(ShopReadManager shopReadManager) {
        this.shopReadManager = shopReadManager;
    }

    public CreateOrderCommand toCreateOrderCommand(InboundOrder inbound) {
        Shop shop = shopReadManager.getById(ShopId.of(inbound.shopId()));

        List<CreateOrderItemCommand> items =
                inbound.items().stream().map(this::toCreateOrderItemCommand).toList();

        return new CreateOrderCommand(
                inbound.salesChannelId(),
                inbound.shopId(),
                shop.channelCode(),
                shop.shopName(),
                inbound.externalOrderNo(),
                inbound.externalOrderedAt(),
                inbound.buyerName(),
                inbound.buyerEmail(),
                inbound.buyerPhone(),
                inbound.paymentMethod(),
                inbound.totalPaymentAmount(),
                inbound.paidAt(),
                items,
                "inbound-order-system");
    }

    private CreateOrderItemCommand toCreateOrderItemCommand(InboundOrderItem item) {
        return new CreateOrderItemCommand(
                item.resolvedProductGroupId(),
                item.resolvedProductId(),
                item.resolvedSellerId(),
                item.resolvedBrandId(),
                item.resolvedSkuCode(),
                item.resolvedProductGroupName(),
                null,
                null,
                null,
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
                item.deliveryRequest());
    }
}
