package com.ryuqq.marketplace.application.shipment.internal;

import com.ryuqq.marketplace.application.shipment.port.out.client.ShipmentSyncStrategy;
import com.ryuqq.marketplace.domain.shipment.exception.SyncChannelNotSupportedException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 배송 동기화 전략 Provider.
 *
 * <p>List&lt;ShipmentSyncStrategy&gt;를 주입받아 channelCode 기반 Map으로 O(1) 라우팅합니다.
 */
@Component
@ConditionalOnProperty(prefix = "sqs.queues", name = "shipment-outbox")
public class ShipmentSyncStrategyProvider {

    private final Map<String, ShipmentSyncStrategy> strategyMap;

    public ShipmentSyncStrategyProvider(List<ShipmentSyncStrategy> strategies) {
        this.strategyMap =
                strategies.stream()
                        .collect(
                                Collectors.toUnmodifiableMap(
                                        ShipmentSyncStrategy::channelCode, Function.identity()));
    }

    /**
     * 채널 코드에 해당하는 전략을 반환합니다.
     *
     * @param channelCode 판매채널 코드
     * @return 배송 동기화 전략
     * @throws IllegalStateException 지원하지 않는 채널 코드
     */
    public ShipmentSyncStrategy getStrategy(String channelCode) {
        ShipmentSyncStrategy strategy = strategyMap.get(channelCode);
        if (strategy == null) {
            throw new SyncChannelNotSupportedException(channelCode);
        }
        return strategy;
    }
}
