package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.port.out.client.SalesChannelOrderClient;
import com.ryuqq.marketplace.domain.shop.vo.ShopCredentials;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 주문 조회 클라이언트 어댑터.
 *
 * <p>세토프는 자사몰이므로 폴링 대신 웹훅 방식을 사용할 예정입니다. 이 어댑터는 SalesChannelOrderClient 인터페이스 호환을 위한 껍데기이며, 실제 주문
 * 수신은 웹훅 컨트롤러(adapter-in)에서 처리합니다.
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceOrderClientAdapter implements SalesChannelOrderClient {

    private static final Logger log =
            LoggerFactory.getLogger(SetofCommerceOrderClientAdapter.class);

    @Override
    public boolean supports(String channelCode) {
        return "SETOF".equals(channelCode);
    }

    @Override
    public List<ExternalOrderPayload> fetchNewOrders(
            long salesChannelId,
            long shopId,
            ShopCredentials credentials,
            Instant fromTime,
            Instant toTime) {

        // 세토프는 웹훅 기반 → 폴링 불필요
        log.debug("세토프 주문 폴링 스킵 (웹훅 기반): salesChannelId={}", salesChannelId);
        return List.of();
    }
}
