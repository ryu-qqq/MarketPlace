package com.ryuqq.marketplace.application.inboundorder.internal;

import com.ryuqq.marketplace.application.inboundorder.factory.InboundOrderConversionFactory;
import com.ryuqq.marketplace.application.inboundorder.manager.InboundOrderCommandManager;
import com.ryuqq.marketplace.application.order.dto.command.CreateOrderCommand;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory;
import com.ryuqq.marketplace.application.order.manager.OrderCommandManager;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.vo.InboundOrders;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** 매핑 완료된 InboundOrder를 내부 Order로 변환하고 전체 결과를 저장하는 프로세서. */
@Component
public class InboundOrderConversionProcessor {

    private static final Logger log =
            LoggerFactory.getLogger(InboundOrderConversionProcessor.class);

    private final InboundOrderConversionFactory conversionFactory;
    private final OrderCommandFactory orderCommandFactory;
    private final OrderCommandManager orderCommandManager;
    private final InboundOrderCommandManager inboundOrderCommandManager;

    public InboundOrderConversionProcessor(
            InboundOrderConversionFactory conversionFactory,
            OrderCommandFactory orderCommandFactory,
            OrderCommandManager orderCommandManager,
            InboundOrderCommandManager inboundOrderCommandManager) {
        this.conversionFactory = conversionFactory;
        this.orderCommandFactory = orderCommandFactory;
        this.orderCommandManager = orderCommandManager;
        this.inboundOrderCommandManager = inboundOrderCommandManager;
    }

    /**
     * 매핑 완료/미완료 InboundOrder를 처리하고 전체를 저장합니다.
     *
     * @return ConversionResult (created, failed, pending)
     */
    public ConversionResult processAndPersist(InboundOrders orders, Instant now) {
        int created = 0;
        int failed = 0;

        List<InboundOrder> fullyMapped = orders.fullyMapped();
        for (InboundOrder inbound : fullyMapped) {
            try {
                CreateOrderCommand command = conversionFactory.toCreateOrderCommand(inbound);
                Order order = orderCommandFactory.createOrder(command);
                orderCommandManager.persist(order);
                inbound.markConverted(order.idValue(), now);
                created++;
            } catch (Exception e) {
                log.warn("InboundOrder 변환 실패: externalOrderNo={}", inbound.externalOrderNo(), e);
                inbound.markFailed(e.getMessage(), now);
                failed++;
            }
        }

        int pending = orders.pendingMapping().size();

        inboundOrderCommandManager.persistAll(orders.all());

        return new ConversionResult(created, failed, pending);
    }

    public record ConversionResult(int created, int failed, int pending) {}
}
