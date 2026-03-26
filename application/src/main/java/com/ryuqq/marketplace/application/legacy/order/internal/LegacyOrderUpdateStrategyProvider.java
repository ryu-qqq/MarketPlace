package com.ryuqq.marketplace.application.legacy.order.internal;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 상태변경 전략 프로바이더.
 *
 * <p>orderStatus → Strategy Map 기반 O(1) 조회.
 */
@Component
public class LegacyOrderUpdateStrategyProvider {

    private final Map<String, LegacyOrderUpdateStrategy> strategyMap;

    public LegacyOrderUpdateStrategyProvider(List<LegacyOrderUpdateStrategy> strategies) {
        this.strategyMap =
                strategies.stream()
                        .collect(
                                Collectors.toUnmodifiableMap(
                                        LegacyOrderUpdateStrategy::supportedStatus,
                                        Function.identity()));
    }

    public LegacyOrderUpdateStrategy getStrategy(String orderStatus) {
        LegacyOrderUpdateStrategy strategy = strategyMap.get(orderStatus);
        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 레거시 주문 상태: " + orderStatus);
        }
        return strategy;
    }
}
