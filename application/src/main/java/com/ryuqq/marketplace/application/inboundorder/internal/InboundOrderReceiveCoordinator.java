package com.ryuqq.marketplace.application.inboundorder.internal;

import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.result.InboundOrderPollingResult;
import com.ryuqq.marketplace.application.inboundorder.factory.InboundOrderFactory;
import com.ryuqq.marketplace.application.inboundorder.manager.InboundOrderCommandManager;
import com.ryuqq.marketplace.application.inboundorder.manager.InboundOrderReadManager;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrderItem;
import com.ryuqq.marketplace.domain.inboundorder.vo.InboundOrders;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * InboundOrder 수신 코디네이터.
 *
 * <p>외부 주문 수신 → 중복 체크 → 매핑 → Order 변환 전체 흐름을 조율합니다.
 */
@Component
public class InboundOrderReceiveCoordinator {

    private final InboundOrderFactory factory;
    private final InboundOrderReadManager readManager;
    private final InboundOrderCommandManager commandManager;
    private final InboundOrderMappingResolver mappingResolver;
    private final InboundOrderConversionProcessor conversionProcessor;

    public InboundOrderReceiveCoordinator(
            InboundOrderFactory factory,
            InboundOrderReadManager readManager,
            InboundOrderCommandManager commandManager,
            InboundOrderMappingResolver mappingResolver,
            InboundOrderConversionProcessor conversionProcessor) {
        this.factory = factory;
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.mappingResolver = mappingResolver;
        this.conversionProcessor = conversionProcessor;
    }

    public InboundOrderPollingResult receiveAll(
            List<ExternalOrderPayload> payloads, long salesChannelId, long shopId, Instant now) {

        InboundOrders allOrders = factory.createAll(payloads, salesChannelId, shopId, now);

        Set<String> existingOrderNos =
                readManager.findExistingExternalOrderNos(
                        salesChannelId, allOrders.externalOrderNos());
        int duplicated = existingOrderNos.size();

        InboundOrders newOrders = allOrders.excludeDuplicates(existingOrderNos);
        if (newOrders.isEmpty()) {
            return InboundOrderPollingResult.of(payloads.size(), 0, 0, duplicated, 0);
        }

        mappingResolver.resolveAndApply(newOrders, salesChannelId, now);

        InboundOrderConversionProcessor.ConversionResult result =
                conversionProcessor.processAndPersist(newOrders, now);

        return InboundOrderPollingResult.of(
                payloads.size(), result.created(), result.pending(), duplicated, result.failed());
    }

    /**
     * PENDING_MAPPING 상태의 InboundOrder를 재시도합니다.
     *
     * @param inbound 재시도 대상 InboundOrder
     * @param now 현재 시각
     * @return 변환 성공 여부
     */
    public boolean retryMapping(InboundOrder inbound, Instant now) {
        InboundOrders orders = InboundOrders.of(List.of(inbound));
        mappingResolver.resolveAndApply(orders, inbound.salesChannelId(), now);

        if (!inbound.items().stream().allMatch(InboundOrderItem::isMapped)) {
            commandManager.persist(inbound);
            return false;
        }

        InboundOrderConversionProcessor.ConversionResult result =
                conversionProcessor.processAndPersist(orders, now);

        return result.created() > 0;
    }
}
