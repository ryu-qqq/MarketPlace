package com.ryuqq.marketplace.application.inboundorder.factory;

import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderItemPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrderItem;
import com.ryuqq.marketplace.domain.inboundorder.vo.InboundOrders;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalOrderPayload → InboundOrder 변환 팩토리. */
@Component
public class InboundOrderFactory {

    public InboundOrders createAll(
            List<ExternalOrderPayload> payloads, long salesChannelId, long shopId, Instant now) {
        List<InboundOrder> orders =
                payloads.stream().map(p -> toInboundOrder(p, salesChannelId, shopId, now)).toList();
        return InboundOrders.of(orders);
    }

    private InboundOrder toInboundOrder(
            ExternalOrderPayload payload, long salesChannelId, long shopId, Instant now) {
        List<InboundOrderItem> items = toItems(payload.items());
        return InboundOrder.forNew(
                salesChannelId,
                shopId,
                0L,
                payload.externalOrderNo(),
                payload.orderedAt(),
                payload.buyerName(),
                payload.buyerEmail(),
                payload.buyerPhone(),
                payload.paymentMethod(),
                payload.totalPaymentAmount(),
                payload.paidAt(),
                items,
                now);
    }

    private List<InboundOrderItem> toItems(List<ExternalOrderItemPayload> payloads) {
        return payloads.stream()
                .map(
                        p ->
                                InboundOrderItem.forNew(
                                        p.externalProductOrderId(),
                                        p.externalProductId(),
                                        p.externalOptionId(),
                                        p.externalProductName(),
                                        p.externalOptionName(),
                                        p.externalImageUrl(),
                                        p.unitPrice(),
                                        p.quantity(),
                                        p.totalAmount(),
                                        p.discountAmount(),
                                        p.sellerBurdenDiscountAmount(),
                                        p.paymentAmount(),
                                        p.receiverName(),
                                        p.receiverPhone(),
                                        p.receiverZipCode(),
                                        p.receiverAddress(),
                                        p.receiverAddressDetail(),
                                        p.deliveryRequest()))
                .toList();
    }
}
