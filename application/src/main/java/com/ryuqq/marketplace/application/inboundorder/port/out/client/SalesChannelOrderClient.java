package com.ryuqq.marketplace.application.inboundorder.port.out.client;

import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.domain.shop.vo.ShopCredentials;
import java.time.Instant;
import java.util.List;

/** 외부 판매채널 주문 조회 클라이언트. */
public interface SalesChannelOrderClient {

    /** 이 클라이언트가 담당하는 판매채널 코드. */
    String channelCode();

    List<ExternalOrderPayload> fetchNewOrders(
            long salesChannelId,
            long shopId,
            ShopCredentials credentials,
            Instant fromTime,
            Instant toTime);
}
