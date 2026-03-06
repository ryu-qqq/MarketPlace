package com.ryuqq.marketplace.application.outboundsync.internal;

import com.ryuqq.marketplace.application.outboundsync.port.out.strategy.OutboundSyncExecutionStrategy;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 외부 채널 연동 전략 라우터.
 *
 * <p>채널 코드와 SyncType에 따라 적절한 Strategy 구현체를 선택합니다.
 */
@Component
public class OutboundSyncStrategyRouter {

    private final List<OutboundSyncExecutionStrategy> strategies;

    public OutboundSyncStrategyRouter(List<OutboundSyncExecutionStrategy> strategies) {
        this.strategies = List.copyOf(strategies);
    }

    /**
     * 채널 코드와 SyncType에 맞는 Strategy를 라우팅합니다.
     *
     * @param channelCode 채널 코드 (예: "NAVER")
     * @param syncType 연동 타입
     * @return 매칭된 Strategy
     * @throws IllegalStateException 지원하지 않는 채널/타입 조합
     */
    public OutboundSyncExecutionStrategy route(String channelCode, SyncType syncType) {
        return strategies.stream()
                .filter(s -> s.supports(channelCode, syncType))
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "지원하지 않는 채널/타입: " + channelCode + "/" + syncType));
    }
}
