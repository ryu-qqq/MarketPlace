package com.ryuqq.marketplace.application.inboundorder.service.command;

import com.ryuqq.marketplace.application.claimsync.manager.ExternalOrderItemMappingReadManager;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.ReceivePurchaseConfirmedWebhookUseCase;
import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.order.port.in.command.ConfirmOrderUseCase;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 구매 확정 웹훅 수신 서비스.
 *
 * <p>ExternalOrderItemMapping을 통해 내부 orderItemId를 역조회한 뒤, 확정 가능한(READY) 주문상품만 필터링하여
 * ConfirmOrderUseCase에 위임합니다. 이미 CONFIRMED 상태인 항목은 무시하여 멱등성을 보장합니다.
 */
@Service
public class ReceivePurchaseConfirmedWebhookService
        implements ReceivePurchaseConfirmedWebhookUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(ReceivePurchaseConfirmedWebhookService.class);
    private static final String WEBHOOK_ACTOR = "webhook-purchase-confirmed";

    private final ExternalOrderItemMappingReadManager mappingReadManager;
    private final OrderItemReadManager orderItemReadManager;
    private final ConfirmOrderUseCase confirmOrderUseCase;

    public ReceivePurchaseConfirmedWebhookService(
            ExternalOrderItemMappingReadManager mappingReadManager,
            OrderItemReadManager orderItemReadManager,
            ConfirmOrderUseCase confirmOrderUseCase) {
        this.mappingReadManager = mappingReadManager;
        this.orderItemReadManager = orderItemReadManager;
        this.confirmOrderUseCase = confirmOrderUseCase;
    }

    @Override
    public void execute(long salesChannelId, List<String> externalProductOrderIds) {
        List<OrderItemId> orderItemIds =
                externalProductOrderIds.stream()
                        .map(extId -> mappingReadManager.getMapping(salesChannelId, extId))
                        .filter(Objects::nonNull)
                        .map(ExternalOrderItemMapping::orderItemId)
                        .toList();

        if (orderItemIds.isEmpty()) {
            log.info(
                    "구매 확정 웹훅: 매핑된 주문상품 없음, salesChannelId={}, count={}",
                    salesChannelId,
                    externalProductOrderIds.size());
            return;
        }

        List<OrderItem> orderItems = orderItemReadManager.findAllByIds(orderItemIds);

        List<String> confirmableIds =
                orderItems.stream()
                        .filter(OrderItem::isConfirmable)
                        .map(item -> item.id().value())
                        .toList();

        if (confirmableIds.isEmpty()) {
            log.info("구매 확정 웹훅: 확정 가능한 주문상품 없음 (이미 확정됨), salesChannelId={}", salesChannelId);
            return;
        }

        confirmOrderUseCase.execute(new OrderItemStatusCommand(confirmableIds, WEBHOOK_ACTOR));

        log.info(
                "구매 확정 웹훅 처리 완료: salesChannelId={}, requested={}, confirmed={}",
                salesChannelId,
                externalProductOrderIds.size(),
                confirmableIds.size());
    }
}
