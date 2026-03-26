package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverLastChangedStatus;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverLastChangedStatusesResponse;
import com.ryuqq.marketplace.application.inboundorder.port.out.client.SalesChannelPurchaseConfirmedClient;
import com.ryuqq.marketplace.domain.shop.vo.ShopCredentials;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 구매확정 폴링 클라이언트 어댑터.
 *
 * <p>last-changed-statuses API에서 PURCHASE_DECIDED 이벤트를 필터링하여 구매확정된 상품주문번호 목록을 반환합니다. claimType이
 * null인 건만 대상으로 합니다.
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommercePurchaseConfirmedClientAdapter
        implements SalesChannelPurchaseConfirmedClient {

    private static final Logger log =
            LoggerFactory.getLogger(NaverCommercePurchaseConfirmedClientAdapter.class);

    private final NaverCommerceOrderClientAdapter orderClientAdapter;

    public NaverCommercePurchaseConfirmedClientAdapter(
            NaverCommerceOrderClientAdapter orderClientAdapter) {
        this.orderClientAdapter = orderClientAdapter;
    }

    @Override
    public boolean supports(String channelCode) {
        return "NAVER".equals(channelCode);
    }

    @Override
    public List<String> fetchPurchaseConfirmedProductOrderIds(
            long salesChannelId,
            long shopId,
            ShopCredentials credentials,
            Instant fromTime,
            Instant toTime) {

        log.info(
                "네이버 구매확정 폴링 시작: salesChannelId={}, shopId={}, from={}, to={}",
                salesChannelId,
                shopId,
                fromTime,
                toTime);

        List<String> productOrderIds = pollPurchaseConfirmedIds(fromTime, toTime);

        log.info(
                "네이버 구매확정 {}건 감지: salesChannelId={}, shopId={}",
                productOrderIds.size(),
                salesChannelId,
                shopId);

        return productOrderIds;
    }

    private List<String> pollPurchaseConfirmedIds(Instant fromTime, Instant toTime) {
        List<String> result = new ArrayList<>();
        String moreSequence = null;

        do {
            NaverLastChangedStatusesResponse response =
                    orderClientAdapter.getLastChangedStatusesAll(fromTime, toTime, moreSequence);

            if (response == null || response.data() == null) {
                break;
            }

            response.data().lastChangeStatuses().stream()
                    .filter(this::isPurchaseConfirmed)
                    .map(NaverLastChangedStatus::productOrderId)
                    .forEach(result::add);

            moreSequence =
                    response.data().more() != null ? response.data().more().moreSequence() : null;

        } while (moreSequence != null);

        return result.stream().distinct().toList();
    }

    private boolean isPurchaseConfirmed(NaverLastChangedStatus status) {
        return "PURCHASE_DECIDED".equals(status.lastChangedType()) && status.claimType() == null;
    }
}
