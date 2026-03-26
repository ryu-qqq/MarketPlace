package com.ryuqq.marketplace.application.inboundorder.manager;

import com.ryuqq.marketplace.application.inboundorder.port.out.client.SalesChannelOrderClient;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 판매채널 주문 클라이언트 전략 프로바이더.
 *
 * <p>channelCode → SalesChannelOrderClient Map 기반 O(1) 조회.
 */
@Component
public class SalesChannelOrderClientManager {

    private final Map<String, SalesChannelOrderClient> clientMap;

    public SalesChannelOrderClientManager(List<SalesChannelOrderClient> clients) {
        this.clientMap =
                clients.stream()
                        .collect(
                                Collectors.toUnmodifiableMap(
                                        SalesChannelOrderClient::channelCode, Function.identity()));
    }

    public boolean supports(String channelCode) {
        return clientMap.containsKey(channelCode);
    }

    public SalesChannelOrderClient getClient(String channelCode) {
        SalesChannelOrderClient client = clientMap.get(channelCode);
        if (client == null) {
            throw new IllegalArgumentException("지원하지 않는 주문 채널: " + channelCode);
        }
        return client;
    }
}
