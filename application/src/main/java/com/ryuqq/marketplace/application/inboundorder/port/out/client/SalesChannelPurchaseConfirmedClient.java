package com.ryuqq.marketplace.application.inboundorder.port.out.client;

import com.ryuqq.marketplace.domain.shop.vo.ShopCredentials;
import java.time.Instant;
import java.util.List;

/**
 * 구매확정 감지 클라이언트 포트.
 *
 * <p>외부 판매채널의 구매확정 이벤트를 감지하여 productOrderId 목록을 반환한다.
 */
public interface SalesChannelPurchaseConfirmedClient {

    boolean supports(String channelCode);

    List<String> fetchPurchaseConfirmedProductOrderIds(
            long salesChannelId,
            long shopId,
            ShopCredentials credentials,
            Instant fromTime,
            Instant toTime);
}
