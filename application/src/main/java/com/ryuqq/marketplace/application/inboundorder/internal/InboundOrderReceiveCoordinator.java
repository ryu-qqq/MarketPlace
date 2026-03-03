package com.ryuqq.marketplace.application.inboundorder.internal;

import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderItemPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.result.InboundOrderPollingResult;
import com.ryuqq.marketplace.application.inboundorder.manager.InboundOrderCommandManager;
import com.ryuqq.marketplace.application.inboundorder.manager.InboundOrderReadManager;
import com.ryuqq.marketplace.application.order.dto.command.CreateOrderCommand;
import com.ryuqq.marketplace.application.order.dto.command.CreateOrderItemCommand;
import com.ryuqq.marketplace.application.order.port.in.command.CreateOrderUseCase;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrderItem;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * InboundOrder 수신 코디네이터.
 *
 * <p>외부 주문 수신 → 중복 체크 → 매핑 → Order 변환 전체 흐름을 조율합니다.
 */
@Component
public class InboundOrderReceiveCoordinator {

    private static final Logger log = LoggerFactory.getLogger(InboundOrderReceiveCoordinator.class);

    private final InboundOrderReadManager readManager;
    private final InboundOrderCommandManager commandManager;
    private final InboundOrderMappingResolver mappingResolver;
    private final CreateOrderUseCase createOrderUseCase;

    public InboundOrderReceiveCoordinator(
            InboundOrderReadManager readManager,
            InboundOrderCommandManager commandManager,
            InboundOrderMappingResolver mappingResolver,
            CreateOrderUseCase createOrderUseCase) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.mappingResolver = mappingResolver;
        this.createOrderUseCase = createOrderUseCase;
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public InboundOrderPollingResult receiveAll(
            List<ExternalOrderPayload> payloads,
            long salesChannelId,
            long shopId,
            long sellerId,
            Instant now) {

        int created = 0;
        int pending = 0;
        int duplicated = 0;
        int failed = 0;

        for (ExternalOrderPayload payload : payloads) {
            if (readManager.existsBySalesChannelIdAndExternalOrderNo(
                    salesChannelId, payload.externalOrderNo())) {
                duplicated++;
                continue;
            }

            InboundOrder inbound =
                    InboundOrder.forNew(
                            salesChannelId,
                            shopId,
                            sellerId,
                            payload.externalOrderNo(),
                            payload.orderedAt(),
                            payload.buyerName(),
                            payload.buyerEmail(),
                            payload.buyerPhone(),
                            payload.paymentMethod(),
                            payload.totalPaymentAmount(),
                            payload.paidAt(),
                            toItems(payload.items()),
                            now);

            boolean allMapped = mappingResolver.resolveAndApply(inbound, now);

            if (!allMapped) {
                commandManager.persist(inbound);
                pending++;
                continue;
            }

            try {
                CreateOrderCommand command = toCreateOrderCommand(inbound);
                String orderId = createOrderUseCase.execute(command);
                inbound.markConverted(orderId, now);
                created++;
            } catch (Exception e) {
                log.warn("InboundOrder 변환 실패: externalOrderNo={}", payload.externalOrderNo(), e);
                inbound.markFailed(e.getMessage(), now);
                failed++;
            }

            commandManager.persist(inbound);
        }

        return InboundOrderPollingResult.of(payloads.size(), created, pending, duplicated, failed);
    }

    /**
     * PENDING_MAPPING 상태의 InboundOrder를 재시도합니다.
     *
     * @param inbound 재시도 대상 InboundOrder
     * @param now 현재 시각
     * @return 변환 성공 여부
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public boolean retryMapping(InboundOrder inbound, Instant now) {
        boolean allMapped = mappingResolver.resolveAndApply(inbound, now);

        if (!allMapped) {
            commandManager.persist(inbound);
            return false;
        }

        try {
            CreateOrderCommand command = toCreateOrderCommand(inbound);
            String orderId = createOrderUseCase.execute(command);
            inbound.markConverted(orderId, now);
        } catch (Exception e) {
            log.warn("InboundOrder 재시도 변환 실패: id={}", inbound.idValue(), e);
            inbound.markFailed(e.getMessage(), now);
        }

        commandManager.persist(inbound);
        return inbound.internalOrderId() != null;
    }

    private List<InboundOrderItem> toItems(List<ExternalOrderItemPayload> payloads) {
        return payloads.stream()
                .map(
                        p ->
                                InboundOrderItem.forNew(
                                        p.externalProductId(),
                                        p.externalOptionId(),
                                        p.externalProductName(),
                                        p.externalOptionName(),
                                        p.externalImageUrl(),
                                        p.unitPrice(),
                                        p.quantity(),
                                        p.totalAmount(),
                                        p.discountAmount(),
                                        p.paymentAmount(),
                                        p.receiverName(),
                                        p.receiverPhone(),
                                        p.receiverZipCode(),
                                        p.receiverAddress(),
                                        p.receiverAddressDetail(),
                                        p.deliveryRequest()))
                .toList();
    }

    private CreateOrderCommand toCreateOrderCommand(InboundOrder inbound) {
        List<CreateOrderItemCommand> items =
                inbound.items().stream().map(this::toCreateOrderItemCommand).toList();

        return new CreateOrderCommand(
                inbound.salesChannelId(),
                inbound.shopId(),
                inbound.externalOrderNo(),
                inbound.externalOrderedAt(),
                inbound.buyerName(),
                inbound.buyerEmail(),
                inbound.buyerPhone(),
                items,
                "inbound-order-system");
    }

    private CreateOrderItemCommand toCreateOrderItemCommand(InboundOrderItem item) {
        return new CreateOrderItemCommand(
                item.resolvedProductGroupId() != null ? item.resolvedProductGroupId() : 0L,
                item.resolvedProductId() != null ? item.resolvedProductId() : 0L,
                item.resolvedSellerId() != null ? item.resolvedSellerId() : 0L,
                item.resolvedBrandId() != null ? item.resolvedBrandId() : 0L,
                item.resolvedSkuCode(),
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
