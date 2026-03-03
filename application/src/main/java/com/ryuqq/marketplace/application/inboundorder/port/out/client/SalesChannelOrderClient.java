package com.ryuqq.marketplace.application.inboundorder.port.out.client;

import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import java.time.Instant;
import java.util.List;

/** 외부 판매채널 주문 조회 클라이언트. */
public interface SalesChannelOrderClient {

    boolean supports(String channelCode);

    @SuppressWarnings("PMD.ExcessiveParameterList")
    List<ExternalOrderPayload> fetchNewOrders(
            long salesChannelId,
            long shopId,
            long sellerId,
            String channelCode,
            String apiKey,
            String apiSecret,
            Instant fromTime,
            Instant toTime);
}
