package com.ryuqq.marketplace.adapter.in.rest.internal.mapper;

import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.OrderCancelledWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.OrderCancelledWebhookRequest.CancelledItemRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.OrderCreatedWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.OrderCreatedWebhookRequest.OrderCreatedItemRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.ReturnRequestedWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.ReturnRequestedWebhookRequest.ReturnRequestedItemRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.ReturnWithdrawnWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.ReturnWithdrawnWebhookRequest.ReturnWithdrawnItemRequest;
import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderItemPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 내부 웹훅 요청 → Application DTO 변환 매퍼.
 *
 * <p>API-MAP-001: @Component 어노테이션 필수.
 *
 * <p>API-MAP-002: 순수 변환 로직만 포함.
 *
 * @author ryu-qqq
 * @since 1.2.0
 */
@Component
public class InternalWebhookApiMapper {

    /**
     * 주문 생성 요청 → ExternalOrderPayload 변환.
     */
    public ExternalOrderPayload toExternalOrderPayload(OrderCreatedWebhookRequest request) {
        List<ExternalOrderItemPayload> items =
                request.items().stream().map(this::toExternalOrderItemPayload).toList();

        return new ExternalOrderPayload(
                request.externalOrderNo(),
                request.orderedAt(),
                request.buyerName(),
                request.buyerEmail(),
                request.buyerPhone(),
                request.paymentMethod(),
                request.totalPaymentAmount(),
                request.paidAt(),
                items);
    }

    /**
     * 취소 요청 → ExternalClaimPayload 목록 변환.
     */
    public List<ExternalClaimPayload> toExternalClaimPayloads(OrderCancelledWebhookRequest request) {
        return request.items().stream()
                .map(item -> toCancelClaimPayload(request.externalOrderId(), item))
                .toList();
    }

    /**
     * 반품 요청 → ExternalClaimPayload 목록 변환.
     */
    public List<ExternalClaimPayload> toExternalClaimPayloads(ReturnRequestedWebhookRequest request) {
        return request.items().stream()
                .map(item -> toReturnRequestClaimPayload(request.externalOrderId(), item))
                .toList();
    }

    /**
     * 반품 철회 → ExternalClaimPayload 목록 변환.
     */
    public List<ExternalClaimPayload> toExternalClaimPayloads(ReturnWithdrawnWebhookRequest request) {
        return request.items().stream()
                .map(item -> toReturnWithdrawnClaimPayload(request.externalOrderId(), item))
                .toList();
    }

    private ExternalOrderItemPayload toExternalOrderItemPayload(OrderCreatedItemRequest item) {
        return new ExternalOrderItemPayload(
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
                item.deliveryRequest());
    }

    private ExternalClaimPayload toCancelClaimPayload(
            String externalOrderId, CancelledItemRequest item) {
        return new ExternalClaimPayload(
                externalOrderId,
                item.externalProductOrderId(),
                "CANCEL",
                "CANCEL_REQUEST",
                null,
                null,
                item.cancelReason(),
                item.cancelDetailedReason(),
                item.cancelQuantity(),
                "BUYER",
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now());
    }

    private ExternalClaimPayload toReturnRequestClaimPayload(
            String externalOrderId, ReturnRequestedItemRequest item) {
        return new ExternalClaimPayload(
                externalOrderId,
                item.externalProductOrderId(),
                "RETURN",
                "RETURN_REQUEST",
                null,
                null,
                item.returnReason(),
                item.returnDetailedReason(),
                item.returnQuantity(),
                "BUYER",
                item.collectDeliveryCompany(),
                item.collectTrackingNumber(),
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now());
    }

    private ExternalClaimPayload toReturnWithdrawnClaimPayload(
            String externalOrderId, ReturnWithdrawnItemRequest item) {
        return new ExternalClaimPayload(
                externalOrderId,
                item.externalProductOrderId(),
                "RETURN",
                "RETURN_REJECT",
                null,
                null,
                null,
                null,
                null,
                "BUYER",
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now());
    }
}
