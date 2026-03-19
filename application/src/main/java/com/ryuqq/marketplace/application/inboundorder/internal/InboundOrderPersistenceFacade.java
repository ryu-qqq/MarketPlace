package com.ryuqq.marketplace.application.inboundorder.internal;

import com.ryuqq.marketplace.application.claimsync.port.out.command.ExternalOrderItemMappingCommandPort;
import com.ryuqq.marketplace.application.inboundorder.port.out.command.InboundOrderCommandPort;
import com.ryuqq.marketplace.application.order.port.out.command.OrderCommandPort;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrderItem;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Order와 InboundOrder를 하나의 트랜잭션으로 저장하는 파사드. */
@Component
public class InboundOrderPersistenceFacade {

    private final OrderCommandPort orderCommandPort;
    private final InboundOrderCommandPort inboundOrderCommandPort;
    private final ExternalOrderItemMappingCommandPort externalOrderItemMappingCommandPort;

    public InboundOrderPersistenceFacade(
            OrderCommandPort orderCommandPort,
            InboundOrderCommandPort inboundOrderCommandPort,
            ExternalOrderItemMappingCommandPort externalOrderItemMappingCommandPort) {
        this.orderCommandPort = orderCommandPort;
        this.inboundOrderCommandPort = inboundOrderCommandPort;
        this.externalOrderItemMappingCommandPort = externalOrderItemMappingCommandPort;
    }

    /**
     * Order 저장 + InboundOrder 상태 저장 + ExternalOrderItemMapping 생성을 하나의 트랜잭션으로 묶는다.
     *
     * <p>InboundOrder.items()와 Order.items()는 동일한 순서로 생성되므로 인덱스 기반 매칭을 사용한다.
     */
    @Transactional
    public void persistOrderAndInbound(Order order, InboundOrder inboundOrder, Instant now) {
        orderCommandPort.persist(order);
        inboundOrderCommandPort.persist(inboundOrder);

        List<ExternalOrderItemMapping> mappings = createMappings(order, inboundOrder, now);
        externalOrderItemMappingCommandPort.persistAll(mappings);
    }

    /** 변환되지 않은 InboundOrder(PENDING_MAPPING, FAILED)를 일괄 저장한다. */
    @Transactional
    public void persistRemainingInboundOrders(List<InboundOrder> inboundOrders) {
        inboundOrderCommandPort.persistAll(inboundOrders);
    }

    /**
     * InboundOrder의 아이템과 Order의 아이템을 인덱스 기반으로 매칭하여 ExternalOrderItemMapping 목록을 생성한다.
     *
     * <p>매칭 보장 근거: - InboundOrderConversionFactory: inbound.items().stream().map(...).toList() 순서
     * 유지 - OrderCommandFactory: for 루프로 command.items().get(i) 순서대로 OrderItem 생성
     */
    private List<ExternalOrderItemMapping> createMappings(
            Order order, InboundOrder inboundOrder, Instant now) {
        List<InboundOrderItem> inboundItems = inboundOrder.items();
        List<OrderItem> orderItems = order.items();

        long salesChannelId = inboundOrder.salesChannelId();
        String channelCode = order.externalOrderReference().shopCode();
        String externalOrderId = inboundOrder.externalOrderNo();

        List<ExternalOrderItemMapping> mappings = new ArrayList<>(inboundItems.size());
        for (int i = 0; i < inboundItems.size(); i++) {
            InboundOrderItem inboundItem = inboundItems.get(i);
            OrderItem orderItem = orderItems.get(i);

            mappings.add(
                    ExternalOrderItemMapping.forNew(
                            salesChannelId,
                            channelCode,
                            externalOrderId,
                            inboundItem.externalProductOrderId(),
                            orderItem.id(),
                            now));
        }
        return mappings;
    }
}
